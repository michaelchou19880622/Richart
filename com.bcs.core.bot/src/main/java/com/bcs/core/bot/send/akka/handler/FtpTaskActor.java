package com.bcs.core.bot.send.akka.handler;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import com.bcs.core.api.service.model.PushApiModel;
import com.bcs.core.bot.pnp.model.FtpTaskModel;
import com.bcs.core.db.entity.PushMessageRecord;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.LINE_HEADER;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.RestfulUtil;

import akka.actor.UntypedActor;

public class FtpTaskActor extends UntypedActor {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(FtpTaskActor.class);

	@Override
	public void onReceive(Object object) throws Exception {
		logger.info("---------- FtpTaskActor ----------");
		
		if (object instanceof FtpTaskModel) {
			logger.info("[ onReceive ] object instanceof FtpTaskModel");
			
			FtpTaskModel ftpTaskModel = (FtpTaskModel) object;
			logger.info("[ onReceive ] ftpTaskModel = {}", ftpTaskModel);

			String fileName = ftpTaskModel.getFileName();
			String product = ftpTaskModel.getFileHead().getProduct();
			String sendType = ftpTaskModel.getFileHead().getMessageSendType();
			Date createTime = ftpTaskModel.getTimestamp();
			
			List<String> requestBodiesString = ftpTaskModel.getLineMessageObjects();

			String url = CoreConfigReader.getString(CONFIG_STR.LINE_MESSAGE_PUSH_URL.toString());
			logger.info("[ onReceive ] url = {}", url);
			
			String accessToken = CoreConfigReader.getString(CONFIG_STR.Default.toString(), CONFIG_STR.ChannelToken.toString(), true);
			logger.info("[ onReceive ] accessToken = {}", accessToken);
			
//			String serviceCode = CoreConfigReader.getString(CONFIG_STR.AutoReply.toString(), CONFIG_STR.ChannelServiceCode.toString(), true);
//			logger.info("[ onReceive ] serviceCode = {}", serviceCode);
			
			PushMessageRecord record = null;

			/* 設定 request headers */
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
//			headers.set(LINE_HEADER.HEADER_BOT_ServiceCode.toString(), serviceCode);
			
			logger.info("[ onReceive ] headers = {}", headers);

			for (String requestBodyString : requestBodiesString) {
				logger.info("[ onReceive ] requestBodyString = {}", requestBodyString);
				
				JSONObject requestBody = new JSONObject(requestBodyString);
				logger.info("[ onReceive ] requestBody = {}", requestBody);
				
				record = new PushMessageRecord();

				/* 將 headers 跟 body 塞進 HttpEntity 中 */
				HttpEntity<String> httpEntity = new HttpEntity<String>(requestBodyString, headers);

				RestfulUtil restfulUtil = new RestfulUtil(HttpMethod.POST, url, httpEntity);

				try {
					JSONObject jsonObjOfRestfulExcuted = restfulUtil.execute();
					logger.info("[ onReceive ] jsonObjOfRestfulExcuted = {}", jsonObjOfRestfulExcuted);

					record.setProduct(product);
					record.setUID(requestBody.getString("to"));
					record.setSourceType(PushMessageRecord.SOURCE_TYPE_FTP);
					record.setSource(fileName);
					record.setSendMessage(requestBody.getJSONArray("messages").getJSONObject(0).getString("text"));
					record.setStatusCode(HttpStatus.OK.toString());
					record.setMainMessage("Success");
					record.setSendType(sendType);
					record.setReservationTime(sendType.equals(PushApiModel.SEND_TYPE_DELAY) ? ftpTaskModel.getFileHead().getScheduledTime() : null);
					record.setSendTime(new Date());
					record.setCreateTime(createTime);
				} catch (HttpClientErrorException e) {
					logger.info("[ onReceive ] HttpClientErrorException = {}", e);
					
					JSONObject errorMessage = new JSONObject(e.getResponseBodyAsString());
					
					String httpClientErrorMessage = errorMessage.getString("message");

					if (errorMessage.has("message")) {
						record.setProduct(product);
						record.setUID(requestBody.getString("to"));
						record.setSourceType(PushMessageRecord.SOURCE_TYPE_FTP);
						record.setSource(fileName);
						record.setSendMessage(requestBody.getJSONArray("messages").getJSONObject(0).getString("text"));
						record.setStatusCode(e.getStatusCode().toString());
						record.setMainMessage(httpClientErrorMessage);
						record.setSendType(sendType);
						record.setReservationTime(sendType.equals(PushApiModel.SEND_TYPE_DELAY) ? ftpTaskModel.getFileHead().getScheduledTime() : null);
						record.setSendTime(new Date());
						record.setCreateTime(createTime);

						if (errorMessage.has("details"))
							record.setDetailMessage(errorMessage.getJSONArray("details").toString());
					}
				} catch (Exception e) {
					logger.info("[ onReceive ] Exception = {}", e);
					
					String exceptionErrorMessage = e.getMessage();
					
					if (exceptionErrorMessage.length() >= 200) {  // 因為DB欄位設定的關係，暫時修改最多只取200長度的data。
						exceptionErrorMessage = exceptionErrorMessage.substring(0, 200);
					}
					
					record.setProduct(product);
					record.setUID(requestBody.getString("to"));
					record.setSourceType(PushMessageRecord.SOURCE_TYPE_FTP);
					record.setSource(fileName);
					record.setSendMessage(requestBody.getJSONArray("messages").getJSONObject(0).getString("text"));
					record.setStatusCode("none");
					record.setMainMessage(exceptionErrorMessage);
					record.setSendType(sendType);
					record.setReservationTime(sendType.equals(PushApiModel.SEND_TYPE_DELAY) ? ftpTaskModel.getFileHead().getScheduledTime() : null);
					record.setSendTime(new Date());
					record.setCreateTime(createTime);
				}

				this.getSender().tell(record, this.getSelf());
			}
		}
	}
}