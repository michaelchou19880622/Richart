package com.bcs.core.bot.send.akka.handler;

import java.util.Date;

import org.json.JSONArray;
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
import com.bcs.core.bot.api.service.LineAccessApiService;
import com.bcs.core.bot.scheduler.service.PushMessageTaskService;
import com.bcs.core.db.entity.PushMessageRecord;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.LINE_HEADER;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.RestfulUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.UntypedActor;

public class PushMessageActor extends UntypedActor {
	
	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(PushMessageActor.class);
	
	/* 因為欄位設定(nvarchar 1024)，暫定最多只取1000長度的data。 */
    public static final int MAX_DATA_LENGTH = 1000;
	
	@Override
	public void onReceive(Object object) throws Exception {
		logger.info("---------- PushMessageActor ----------");
		
		if (object instanceof PushApiModel) {
			logger.info("[PushMessageActor] object instanceof PushApiModel");
			
			PushApiModel pushApiModel = (PushApiModel) object;
			logger.info("[PushMessageActor] pushApiModel = {}", pushApiModel);

			logger.info("[PushMessageActor] pushApiModel.getSendTimeType() = {}", pushApiModel.getSendTimeType());
			if (pushApiModel.getSendTimeType().equals(PushApiModel.SEND_TYPE_IMMEDIATE)) { // 立即發送

				logger.info("[PushMessageActor] [IMMEDIATE] pushApiModel = {}", pushApiModel);
				
				String url = CoreConfigReader.getString(CONFIG_STR.LINE_MESSAGE_PUSH_URL.toString());
				logger.info("[PushMessageActor] [IMMEDIATE] url = {}", url);
				
				ObjectNode callRefreshingResult = LineAccessApiService.callVerifyAPIAndIssueToken(CONFIG_STR.Default.toString(), true);
				logger.info("[PushMessageActor] [IMMEDIATE] callRefreshingResult : {}", callRefreshingResult);
				
				String accessToken = CoreConfigReader.getString(CONFIG_STR.Default.toString(), CONFIG_STR.ChannelToken.toString(), true);
				logger.info("[PushMessageActor] [IMMEDIATE] accessToken = {}", accessToken);
				
				String serviceCode = CoreConfigReader.getString(CONFIG_STR.AutoReply.toString(), CONFIG_STR.ChannelServiceCode.toString(), true);
				logger.info("[PushMessageActor] [IMMEDIATE] serviceCode = {}", serviceCode);
				
				JSONObject requestBody = new JSONObject();
				
				/* 設定 request headers */
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
				headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
				headers.set(LINE_HEADER.HEADER_BOT_ServiceCode.toString(), serviceCode);
				logger.info("[PushMessageActor] [IMMEDIATE] headers = {}", headers);

				requestBody.put("messages", pushApiModel.getMessages());

				JSONArray uids = pushApiModel.getUid();
				logger.info("[PushMessageActor] [IMMEDIATE] uids = {}", uids);
				
				for (Integer i = 0; i < uids.length(); i++) {
					PushMessageRecord record = new PushMessageRecord();

					requestBody.put("to", uids.get(i));

					logger.info("[PushMessageActor] [IMMEDIATE] requestBody.toString() = {}", requestBody.toString());

					/* 將 headers 跟 body 塞進 HttpEntity 中 */
					HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody.toString(), headers);

					RestfulUtil restfulUtil = new RestfulUtil(HttpMethod.POST, url, httpEntity);
					logger.info("[PushMessageActor] [IMMEDIATE] restfulUtil = {}", restfulUtil);
					
					try {
						JSONObject jsonObjOfRestfulExcuted = restfulUtil.execute();
						logger.info("[PushMessageActor] [IMMEDIATE] jsonObjOfRestfulExcuted = {}", jsonObjOfRestfulExcuted.toString());
						
						String sendMessage = pushApiModel.getMessages().toString();
						logger.info("[PushMessageActor] [IMMEDIATE] sendMessage = {}", sendMessage);
						logger.info("[PushMessageActor] [IMMEDIATE] sendMessage.length() = {}", sendMessage.length());
						
						if (sendMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
							sendMessage = sendMessage.substring(0, MAX_DATA_LENGTH);
							logger.info("[PushMessageActor] [IMMEDIATE] substring sendMessage = {}", sendMessage);
							logger.info("[PushMessageActor] [IMMEDIATE] substring sendMessage.length() = {}", sendMessage.length());
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
					} catch (HttpClientErrorException e) {
						logger.info("[PushMessageActor] [IMMEDIATE] HttpClientErrorException = {}", e);
						
						JSONObject errorMessage = new JSONObject(e.getResponseBodyAsString());
						
						String strErrorMessage = errorMessage.getString("message");
						logger.info("[PushMessageActor] [IMMEDIATE] strErrorMessage = {}", strErrorMessage);
						logger.info("[PushMessageActor] [IMMEDIATE] strErrorMessage.length() = {}", strErrorMessage.length());
						
						if (strErrorMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
							strErrorMessage = strErrorMessage.substring(0, MAX_DATA_LENGTH);
							logger.info("[PushMessageActor] [IMMEDIATE] substring strErrorMessage = {}", strErrorMessage);
							logger.info("[PushMessageActor] [IMMEDIATE] substring strErrorMessage.length() = {}", strErrorMessage.length());
						}
						
						String sendMessage = pushApiModel.getMessages().toString();
						logger.info("[PushMessageActor] [IMMEDIATE] sendMessage = {}", sendMessage);
						logger.info("[PushMessageActor] [IMMEDIATE] sendMessage.length() = {}", sendMessage.length());
						
						if (sendMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
							sendMessage = sendMessage.substring(0, MAX_DATA_LENGTH);
							logger.info("[PushMessageActor] [IMMEDIATE] substring sendMessage = {}", sendMessage);
							logger.info("[PushMessageActor] [IMMEDIATE] substring sendMessage.length() = {}", sendMessage.length());
						}

						if (errorMessage.has("message")) {
							record.setProduct(pushApiModel.getDepartment());
							record.setUID(uids.get(i).toString());
							record.setSourceType(PushMessageRecord.SOURCE_TYPE_API);
							record.setSendMessage(sendMessage);
							record.setStatusCode(e.getStatusCode().toString());
							record.setMainMessage(strErrorMessage);
							record.setSendType(pushApiModel.getSendTimeType());
							record.setSendTime(new Date());
							record.setCreateTime(pushApiModel.getTriggerTime());

							if (errorMessage.has("details")) {
								
								String detailMessage = errorMessage.getJSONArray("details").toString();
								logger.info("[PushMessageActor] [IMMEDIATE] detailMessage = {}", detailMessage);
								logger.info("[PushMessageActor] [IMMEDIATE] detailMessage.length() = {}", detailMessage.length());
								
								if (detailMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
									detailMessage = detailMessage.substring(0, MAX_DATA_LENGTH);
									logger.info("[PushMessageActor] [IMMEDIATE] substring detailMessage = {}", detailMessage);
									logger.info("[PushMessageActor] [IMMEDIATE] substring detailMessage.length() = {}", detailMessage.length());
								}
								
								record.setDetailMessage(detailMessage);
							}
						}
					} catch (Exception e) {
						logger.info("[PushMessageActor] [IMMEDIATE] Exception = {}", e);
						
						String exceptionErrorMessage = e.getMessage();
						logger.info("[PushMessageActor] [IMMEDIATE] exceptionErrorMessage = {}", exceptionErrorMessage);
						logger.info("[PushMessageActor] [IMMEDIATE] exceptionErrorMessage.length() = {}", exceptionErrorMessage.length());
						
						if (exceptionErrorMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
							exceptionErrorMessage = exceptionErrorMessage.substring(0, MAX_DATA_LENGTH);
							logger.info("[PushMessageActor] [IMMEDIATE] substring exceptionErrorMessage = {}", exceptionErrorMessage);
							logger.info("[PushMessageActor] [IMMEDIATE] substring exceptionErrorMessage.length() = {}", exceptionErrorMessage.length());
						}
						
						String sendMessage = pushApiModel.getMessages().toString();
						logger.info("[PushMessageActor] [IMMEDIATE] sendMessage = {}", sendMessage);
						logger.info("[PushMessageActor] [IMMEDIATE] sendMessage.length() = {}", sendMessage.length());
						
						if (sendMessage.length() >= MAX_DATA_LENGTH) {  // 因為DB欄位設定的關係(nvarchar 1024)，暫時修改最多只取MAX_DATA_LENGTH長度的data。
							sendMessage = sendMessage.substring(0, MAX_DATA_LENGTH);
							logger.info("[PushMessageActor] [IMMEDIATE] substring sendMessage = {}", sendMessage);
							logger.info("[PushMessageActor] [IMMEDIATE] substring sendMessage.length() = {}", sendMessage.length());
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
					
					this.getSender().tell(record, this.getSelf());
				}
			} else { // 預約發送
				logger.info("[PushMessageActor] [SCHEDULE] pushApiModel = {}", pushApiModel);
				PushMessageTaskService pushMessageTaskService = ApplicationContextProvider.getApplicationContext().getBean(PushMessageTaskService.class);

				pushMessageTaskService.startTask(pushApiModel);
			}
		}
	}
}