package com.bcs.core.bot.scheduler.handler;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import com.bcs.core.api.service.model.PushApiModel;
import com.bcs.core.bot.akka.service.PNPService;
import com.bcs.core.bot.api.service.LineAccessApiService;
import com.bcs.core.db.entity.PushMessageRecord;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.LINE_HEADER;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.RestfulUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PushMessageTask implements Job {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(PushMessageTask.class);
	
	/* 因為欄位設定(nvarchar 1024)，暫定最多只取1000長度的data。 */
    public static final int MAX_DATA_LENGTH = 1000;
	
	PNPService PNPService = ApplicationContextProvider.getApplicationContext().getBean(PNPService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("---------- PushMessageTask ----------");
		
		String url = CoreConfigReader.getString(CONFIG_STR.LINE_MESSAGE_PUSH_URL.toString());
		logger.info("[PushMessageTask] url = {}", url);
		
		try {
			ObjectNode callRefreshingResult = LineAccessApiService.callVerifyAPIAndIssueToken(CONFIG_STR.Default.toString(), true);
			logger.info("[PushMessageTask] callRefreshingResult : {}", callRefreshingResult);
		} catch (Exception e2) {
			logger.info("[PushMessageTask] Exception = {}", e2);
		}
		
		String accessToken = CoreConfigReader.getString(CONFIG_STR.Default.toString(), CONFIG_STR.ChannelToken.toString(), true);
		logger.info("[PushMessageTask] accessToken = {}", accessToken);
		
//		String serviceCode = CoreConfigReader.getString(CONFIG_STR.AutoReply.toString(), CONFIG_STR.ChannelServiceCode.toString(), true);
//		logger.info("[PushMessageTask] serviceCode = {}", serviceCode);
		
		PushApiModel pushApiModel = null;
		RestfulUtil restfulUtil = null;
		JSONObject requestBody = new JSONObject();
		PushMessageRecord record = null;
		
		/* 設定 request headers */
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
//		headers.set(LINE_HEADER.HEADER_BOT_ServiceCode.toString(), serviceCode);
		logger.info("[PushMessageTask] headers = {}", headers);
		
		try {
			pushApiModel = (PushApiModel) context.getScheduler().getContext().get("PushApiModel");
			logger.info("[PushMessageTask] pushApiModel = {}", pushApiModel);
		} catch (SchedulerException e) {
			logger.info("[PushMessageTask] SchedulerException = {}", e);
		}
		
		requestBody.put("messages", pushApiModel.getMessages());
		logger.info("[PushMessageTask] 1-1 requestBody = {}", requestBody);
		
		JSONArray uids = pushApiModel.getUid();
		logger.info("[PushMessageTask] uids = {}", uids);
		
		for(Integer i = 0; i < uids.length(); i++) {
			record = new PushMessageRecord();
			
			requestBody.put("to", uids.get(i));
			logger.info("[PushMessageTask] 1-2 requestBody = {}", requestBody);
			
			/* 將 headers 跟 body 塞進 HttpEntity 中  */
			HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody.toString(), headers);
			
			try {
				restfulUtil = new RestfulUtil(HttpMethod.POST, url, httpEntity);
				logger.info("[PushMessageTask] restfulUtil = {}", restfulUtil);

				JSONObject jsonObjOfRestfulExcuted = restfulUtil.execute();
				logger.info("[PushMessageTask] jsonObjOfRestfulExcuted = {}", jsonObjOfRestfulExcuted);
				
				String sendMessage = pushApiModel.getMessages().toString();
				logger.info("[PushMessageTask] sendMessage = {}", sendMessage);
				logger.info("[PushMessageTask] sendMessage.length() = {}", sendMessage.length());
				
				if (sendMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
					sendMessage = sendMessage.substring(0, MAX_DATA_LENGTH);
				}
				
				record.setProduct(pushApiModel.getDepartment());
				record.setUID(uids.get(i).toString());
				record.setSourceType(PushMessageRecord.SOURCE_TYPE_API);
				record.setSendMessage(sendMessage);
				record.setStatusCode(HttpStatus.OK.toString());
				record.setMainMessage("Success");
				record.setSendType(pushApiModel.getSendTimeType());
				record.setSendTime(new Date());
				record.setCreateTime(pushApiModel.getTriggerTime());
			} catch (KeyManagementException | NoSuchAlgorithmException e1) {
				logger.info("[PushMessageTask] KeyManagementException | NoSuchAlgorithmException : {}", e1);
			} catch (Exception e) {
				if (e instanceof HttpClientErrorException) {
					logger.info("[PushMessageTask] HttpClientErrorException = {}", e);
					
					HttpClientErrorException exception = (HttpClientErrorException) e;
					
					String sendMessage = pushApiModel.getMessages().toString();
					logger.info("[PushMessageTask] sendMessage = {}", sendMessage);
					logger.info("[PushMessageTask] sendMessage.length() = {}", sendMessage.length());
					
					if (sendMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
						sendMessage = sendMessage.substring(0, MAX_DATA_LENGTH);
						logger.info("[PushMessageTask] substring sendMessage = {}", sendMessage);
						logger.info("[PushMessageTask] substring sendMessage.length() = {}", sendMessage.length());
					}
					
					JSONObject errorMessage = new JSONObject(exception.getResponseBodyAsString());
					
					String strErrorMessage = errorMessage.getString("message");
					logger.info("[PushMessageTask] strErrorMessage = {}", strErrorMessage);
					logger.info("[PushMessageTask] strErrorMessage.length() = {}", strErrorMessage.length());
					
					if (strErrorMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
						strErrorMessage = strErrorMessage.substring(0, MAX_DATA_LENGTH);
						logger.info("[PushMessageTask] substring strErrorMessage = {}", strErrorMessage);
						logger.info("[PushMessageTask] substring strErrorMessage.length() = {}", strErrorMessage.length());
					}

					if (errorMessage.has("message")) {
						record.setProduct(pushApiModel.getDepartment());
						record.setUID(uids.get(i).toString());
						record.setSourceType(PushMessageRecord.SOURCE_TYPE_API);
						record.setSendMessage(sendMessage);
						record.setStatusCode(exception.getStatusCode().toString());
						record.setMainMessage(strErrorMessage);
						record.setSendType(pushApiModel.getSendTimeType());
						record.setSendTime(new Date());
						record.setCreateTime(pushApiModel.getTriggerTime());
						
						if (errorMessage.has("details")) {
							
							String detailMessage = errorMessage.getJSONArray("details").toString();
							logger.info("[PushMessageTask] detailMessage = {}", detailMessage);
							logger.info("[PushMessageTask] detailMessage.length() = {}", detailMessage.length());
							
							if (detailMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
								detailMessage = detailMessage.substring(0, MAX_DATA_LENGTH);
								logger.info("[PushMessageTask] substring detailMessage = {}", detailMessage);
								logger.info("[PushMessageTask] substring detailMessage.length() = {}", detailMessage.length());
							}
							
							record.setDetailMessage(detailMessage);
						}
					}
				} else {
					logger.info("[PushMessageTask] Exception = {}", e);
					
					String exceptionErrorMessage = e.getMessage();
					logger.info("[PushMessageTask] exceptionErrorMessage = {}", exceptionErrorMessage);
					logger.info("[PushMessageTask] exceptionErrorMessage.length() = {}", exceptionErrorMessage.length());
					
					if (exceptionErrorMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
						exceptionErrorMessage = exceptionErrorMessage.substring(0, MAX_DATA_LENGTH);
						logger.info("[PushMessageTask] substring exceptionErrorMessage = {}", exceptionErrorMessage);
						logger.info("[PushMessageTask] substring exceptionErrorMessage.length() = {}", exceptionErrorMessage.length());
					}
					
					String sendMessage = pushApiModel.getMessages().toString();
					logger.info("[PushMessageTask] sendMessage = {}", sendMessage);
					logger.info("[PushMessageTask] sendMessage.length() = {}", sendMessage.length());
					
					if (sendMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
						sendMessage = sendMessage.substring(0, MAX_DATA_LENGTH);
						logger.info("[PushMessageTask] substring sendMessage = {}", sendMessage);
						logger.info("[PushMessageTask] substring sendMessage.length() = {}", sendMessage.length());
					}
					
					record.setProduct(pushApiModel.getDepartment());
					record.setUID(uids.get(i).toString());
					record.setSourceType(PushMessageRecord.SOURCE_TYPE_API);
					record.setSendMessage(sendMessage);
					record.setMainMessage(exceptionErrorMessage);
					record.setSendType(pushApiModel.getSendTimeType());
					record.setSendTime(new Date());
					record.setCreateTime(pushApiModel.getTriggerTime());
				}
			}
			
			PNPService.tell(record);
		}
	}
}