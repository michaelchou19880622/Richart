package com.bcs.core.bot.api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bcs.core.api.service.LineTokenApiService;
import com.bcs.core.bot.api.model.SendToBotModel;
import com.bcs.core.bot.enums.SEND_TYPE;
import com.bcs.core.db.entity.SystemConfig;
import com.bcs.core.db.entity.SystemLog;
import com.bcs.core.db.service.SystemConfigService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.LINE_HEADER;
import com.bcs.core.enums.LOG_TARGET_ACTION_TYPE;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.log.util.SystemLogUtil;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.DataSyncUtil;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.HttpClientUtil;
import com.bcs.core.utils.ObjectUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.response.BotApiResponse;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

@Slf4j
@Service
public class LineAccessApiService {
	public static final String LINE_API_SYNC = "LINE_API_SYNC";

	private Timer flushTimer = new Timer();

	private Timer checkTokenTimer = new Timer();

	private class CustomTask extends TimerTask {

		@Override
		public void run() {

			try {
				// Check Data Sync
				Boolean isReSyncData = DataSyncUtil.isReSyncData(LINE_API_SYNC);
				if (isReSyncData) {
					lineMessagingServiceMap.clear();
					HttpClientUtil.clearData();
					DataSyncUtil.syncDataFinish(LINE_API_SYNC);
				}
			} catch (Throwable e) {
				log.error(ErrorRecord.recordError(e));
			}
		}
	}

	public LineAccessApiService() {

		flushTimer.schedule(new CustomTask(), 120000, 30000);
		checkTokenTimer.schedule(new CustomTaskCheckToken(), 150_000, 43_200_000);
	}

	private class CustomTaskCheckToken extends TimerTask {

		@Override
		public void run() {

			try {
				List<Object[]> channels = ApplicationContextProvider.getApplicationContext().getBean(SystemConfigService.class).findLikeConfigId("%.ChannelId");
				if (channels != null && channels.size() > 0) {
					for (Object[] channel : channels) {
						String configId = (String) channel[0];
						log.debug("callVerifyAPIAndIssueToken configId:" + configId);

						if (StringUtils.isNotBlank(configId) && configId.indexOf(".") > 0) {
							String[] split = configId.split("\\.");
							if (split != null && split.length == 2) {
								String channelId = split[0];
								ObjectNode node = LineAccessApiService.callVerifyAPIAndIssueToken(channelId, true);
								log.debug("callVerifyAPIAndIssueToken:" + channelId + ", isReIssue: " + node.get("isReIssue"));
								log.debug("callVerifyAPIAndIssueToken:" + ObjectUtil.objectToJsonStr(node));
							}
						}
					}
				}
			} catch (Throwable e) {
				log.error(ErrorRecord.recordError(e));
			}
		}
	}

	@PreDestroy
	public void cleanUp() {
		log.debug("[DESTROY] LineAccessApiService cleaning up...");

		flushTimer.cancel();
		checkTokenTimer.cancel();
		log.debug("[DESTROY] LineAccessApiService destroyed.");
	}

	private static Map<String, List<LineMessagingService>> lineMessagingServiceMap = new HashMap<String, List<LineMessagingService>>();

	private static LineMessagingService getService(String ChannelId, String ChannelName) {
		log.info("getService");
		
		log.debug("ChannelId = {}", ChannelId);
		log.debug("ChannelName = {}", ChannelName);
		
		String Channel = ChannelId + ChannelName;
		log.debug("Channel = {}", Channel);

		List<LineMessagingService> lineMessagingServices = lineMessagingServiceMap.get(Channel);

		if (lineMessagingServices == null || lineMessagingServices.size() == 0) {
			String channelToken = CoreConfigReader.getString(ChannelId, CONFIG_STR.ChannelToken.toString(), true);
			log.debug("channelToken = {}", channelToken);

			if (lineMessagingServices == null) {
				lineMessagingServices = new ArrayList<LineMessagingService>();
				lineMessagingServiceMap.put(Channel, lineMessagingServices);
			}

			if (lineMessagingServices.size() == 0) {
				for (int i = 0; i < 300; i++) {
					LineMessagingServiceBuilder builder = LineMessagingServiceBuilder.create(channelToken);

					Interceptor interceptor = new Interceptor() {
						@Override
						public okhttp3.Response intercept(Chain chain) throws IOException {
							Request request = chain.request().newBuilder().build();
							log.info("request.headers = {}", request.headers()); 
							return chain.proceed(request);
						}
					};

					builder.addInterceptor(interceptor);
					builder.connectTimeout(300_000);
					builder.readTimeout(300_000);
					builder.writeTimeout(300_000);
					LineMessagingService lineMessagingService = builder.build();

					try {
						String proxyUrl = CoreConfigReader.getString(CONFIG_STR.RICHART_PROXY_URL.toString(), true);

						if (StringUtils.isNotBlank(proxyUrl)) {
							LineMessagingServiceBuilderBcs bcs = new LineMessagingServiceBuilderBcs();
							lineMessagingService = bcs.build(builder, true, proxyUrl);
						}
					} catch (Exception e) {
						log.error(ErrorRecord.recordError(e));
					}

					lineMessagingServices.add(lineMessagingService);
				}
			}
		}

		return randomOne(lineMessagingServices);
	}

	private static LineMessagingService getServiceWithServiceCode(String ChannelId, String ChannelName) {
		log.info("getServiceWithServiceCode");
		
		log.debug("ChannelId = {}", ChannelId);
		log.debug("ChannelName = {}", ChannelName);
		
		String Channel = ChannelId + ChannelName;
		log.debug("Channel = {}", Channel);

		List<LineMessagingService> lineMessagingServices = lineMessagingServiceMap.get(Channel);

		if (lineMessagingServices == null || lineMessagingServices.size() == 0) {
			String channelToken = CoreConfigReader.getString(ChannelId, CONFIG_STR.ChannelToken.toString(), true);
            log.debug("channelToken = {}", channelToken);
            
			final String serviceCode = CoreConfigReader.getString(ChannelName, CONFIG_STR.ChannelServiceCode.toString(), true);
            log.debug("serviceCode = {}", serviceCode);

			if (lineMessagingServices == null) {
				lineMessagingServices = new ArrayList<LineMessagingService>();
				lineMessagingServiceMap.put(Channel, lineMessagingServices);
			}

			if (lineMessagingServices.isEmpty()) {
				for (int i = 0; i < 300; i++) {
					LineMessagingServiceBuilder builder = LineMessagingServiceBuilder.create(channelToken);

					Interceptor interceptor = new Interceptor() {
						@Override
						public okhttp3.Response intercept(Chain chain) throws IOException {
							Request request = chain.request().newBuilder()
									.addHeader(LINE_HEADER.HEADER_BOT_ServiceCode.toString(), serviceCode)
									.build();
							log.info("request.headers = {}", request.headers()); 
							return chain.proceed(request);
						}
					};

					builder.addInterceptor(interceptor);
					builder.connectTimeout(300_000);
					builder.readTimeout(300_000);
					builder.writeTimeout(300_000);
					LineMessagingService lineMessagingService = builder.build();

					try {
						String proxyUrl = CoreConfigReader.getString(CONFIG_STR.RICHART_PROXY_URL.toString(), true);
						if (StringUtils.isNotBlank(proxyUrl)) {
							LineMessagingServiceBuilderBcs bcs = new LineMessagingServiceBuilderBcs();
							lineMessagingService = bcs.build(builder, true, proxyUrl);
						}
					} catch (Exception e) {
						log.error(ErrorRecord.recordError(e));
					}

					lineMessagingServices.add(lineMessagingService);
				}
			}
		}

		return randomOne(lineMessagingServices);
	}

	private static LineMessagingService randomOne(List<LineMessagingService> lineMessagingServices) {
		log.debug("LineMessagingService Size = " + lineMessagingServices.size());

		int index = new Random().nextInt(lineMessagingServices.size());
		log.debug("Get Random One, index = " + index);
		
		return lineMessagingServices.get(index);
	}

	public static void clearData() {
		for (List<LineMessagingService> list : lineMessagingServiceMap.values()) {
			list.clear();
		}
		lineMessagingServiceMap.clear();
		HttpClientUtil.clearData();
		DataSyncUtil.settingReSync(LINE_API_SYNC);
	}

	public static Response<BotApiResponse> sendToLine(SendToBotModel sendToBotModel) throws Exception {
        log.info("sendToLine");
		log.debug("sendToBotModel = {}", sendToBotModel);
		log.debug("sendToBotModel.getSendType() = {}", sendToBotModel.getSendType());
		
		String ChannelId = sendToBotModel.getChannelId();
		String ChannelName = sendToBotModel.getChannelName();
		log.debug("ChannelId = {}", ChannelId);
		log.debug("ChannelName = {}", ChannelName);

		if (ChannelName.equals(CONFIG_STR.InManualReplyButNotSendMsg.toString())) {
			throw new BcsNoticeException("使用者在真人客服無法推播");
		}

		if (SEND_TYPE.REPLY_MSG.equals(sendToBotModel.getSendType())) {

			Date start = new Date();
			int status = 0;

			String postMsg = ObjectUtil.objectToJsonStr(sendToBotModel.getReplyMessage());
            log.debug("postMsg = {}", postMsg);
			
			try {
				Response<BotApiResponse> response;
                
                if (ChannelName.equals(CONFIG_STR.ManualReply.toString()) || ChannelName.equals(CONFIG_STR.AutoReply.toString())) {
					response = getServiceWithServiceCode(ChannelId, ChannelName)
							.replyMessage(sendToBotModel.getReplyMessage())
							.execute();
				} else {
					response = getService(ChannelId, ChannelName)
							.replyMessage(sendToBotModel.getReplyMessage())
							.execute();
				}
				log.debug("response = {}", response.body());

                status = response.code();
                log.debug("status = {}", status);

				if (401 == status) {
					callVerifyAPIAndIssueToken(sendToBotModel.getChannelId(), true);
					clearData();
				}

				SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi, start, status, postMsg, status + "");
				return response;
			} catch (Exception e) {
				String error = ErrorRecord.recordError(e, false);
				log.error(error);
				SystemLogUtil.saveLogError(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi, error, e.getMessage());
				SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi_Error, start, status, postMsg, status + "");
				throw e;
			}
		} else if (SEND_TYPE.PUSH_MSG.equals(sendToBotModel.getSendType())) {

			Date start = new Date();
			int status = 0;

			String postMsg = ObjectUtil.objectToJsonStr(sendToBotModel.getPushMessage());
			log.debug("postMsg : {}", postMsg);
			
			try {
                Response<BotApiResponse> response;
                
                if (ChannelName.equals(CONFIG_STR.ManualReply.toString()) || ChannelName.equals(CONFIG_STR.AutoReply.toString())) {
                    response = getServiceWithServiceCode(ChannelId, ChannelName)
    	                        .pushMessage(sendToBotModel.getPushMessage())
    	                        .execute();
                } else {
                    response = getService(ChannelId, ChannelName)
    	                        .pushMessage(sendToBotModel.getPushMessage())
    	                        .execute();
				}
				log.debug("response = {}", response.body());

                status = response.code();
                log.debug("status = {}", status);

				if (401 == status) {
					callVerifyAPIAndIssueToken(sendToBotModel.getChannelId(), true);
					clearData();
				}

				SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi, start, status, postMsg, status + "");
				return response;
			} catch (Exception e) {
				String error = ErrorRecord.recordError(e, false);
				log.error("Exception : {}", e);
				SystemLogUtil.saveLogError(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi, error, e.getMessage());
				SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi_Error, start, status, postMsg, status + "");
				throw e;
			}
		}

		return null;
	}
	
	public static Response<BotApiResponse> sendToLineWithServiceCode(SendToBotModel sendToBotModel) throws Exception {
        log.info("sendToLineWithServiceCode");
		log.debug("sendToBotModel = {}", sendToBotModel);
		log.debug("sendToBotModel.getSendType() = {}", sendToBotModel.getSendType());
		
		String ChannelId = sendToBotModel.getChannelId();
		String ChannelName = sendToBotModel.getChannelName();
		log.debug("ChannelId : {}", ChannelId);
		log.debug("ChannelName : {}", ChannelName);

        boolean isUsingServiceCode = false;
        if (ChannelName.equals(CONFIG_STR.ManualReply.toString()) || ChannelName.equals(CONFIG_STR.AutoReply.toString())) {
        	isUsingServiceCode = true;
        }
        log.debug("isUsingServiceCode = {}", isUsingServiceCode);

		if (ChannelName.equals(CONFIG_STR.InManualReplyButNotSendMsg.toString())) {
			throw new BcsNoticeException("使用者在真人客服無法推播");
		}

		if (SEND_TYPE.REPLY_MSG.equals(sendToBotModel.getSendType())) {

			Date start = new Date();
			int status = 0;

			String postMsg = ObjectUtil.objectToJsonStr(sendToBotModel.getReplyMessage());
			log.debug("postMsg : {}", postMsg);
			
			try {
				Response<BotApiResponse> response;
                
                if (isUsingServiceCode) {
					response = getServiceWithServiceCode(ChannelId, ChannelName)
							.replyMessage(sendToBotModel.getReplyMessage())
							.execute();
				} else {
					response = getService(ChannelId, ChannelName)
							.replyMessage(sendToBotModel.getReplyMessage())
							.execute();
				}
				log.debug("response = {}", response.body());

                status = response.code();
                log.debug("status = {}", status);

				if (401 == status) {
					callVerifyAPIAndIssueToken(sendToBotModel.getChannelId(), true);
					clearData();
				}

				SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi, start, status, postMsg, status + "");
				return response;
			} catch (Exception e) {
				String error = ErrorRecord.recordError(e, false);
				log.error(error);
				SystemLogUtil.saveLogError(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi, error, e.getMessage());
				SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi_Error, start, status, postMsg, status + "");
				throw e;
			}
		} else if (SEND_TYPE.PUSH_MSG.equals(sendToBotModel.getSendType())) {

			Date start = new Date();
			int status = 0;

			String postMsg = ObjectUtil.objectToJsonStr(sendToBotModel.getPushMessage());
			log.debug("postMsg : {}", postMsg);
			
			try {
                Response<BotApiResponse> response;

                if (isUsingServiceCode) {
                    response = getServiceWithServiceCode(ChannelId, ChannelName)
    	                        .pushMessage(sendToBotModel.getPushMessage())
    	                        .execute();
                } else {
                    response = getService(ChannelId, ChannelName)
    	                        .pushMessage(sendToBotModel.getPushMessage())
    	                        .execute();
				}

				log.debug("response = {}", response.body());

                status = response.code();
                log.debug("status = {}", status);

				if (401 == status) {
					callVerifyAPIAndIssueToken(sendToBotModel.getChannelId(), true);
					clearData();
				}

				SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi, start, status, postMsg, status + "");
				return response;
			} catch (Exception e) {
				String error = ErrorRecord.recordError(e, false);
				log.error("Exception : {}", e);
				SystemLogUtil.saveLogError(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi, error, e.getMessage());
				SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_SendToLineApi_Error, start, status, postMsg, status + "");
				throw e;
			}
		}

		return null;
	}

	public static Response<ResponseBody> getImageFromLine(String channelId, String channelName, String msgId) throws Exception {
		log.debug("getImageFromLine:" + msgId);

		Date start = new Date();
		int status = 0;

		try {

			Response<ResponseBody> response = getService(channelId, channelName).getMessageContent(msgId).execute();
            log.debug("response.code() = {}", response.code());

			status = response.code();

			if (401 == status) {
				callVerifyAPIAndIssueToken(channelId, true);
				clearData();
			}

			SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_GetFromLineApi, start, status, msgId, status + "");
			return response;
		} catch (Exception e) {
			String error = ErrorRecord.recordError(e, false);
			log.error(error);
			SystemLogUtil.saveLogError(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_GetFromLineApi, error, e.getMessage());
			SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_GetFromLineApi_Error, start, status, msgId, status + "");
			throw e;
		}
	}

	public static ObjectNode callVerifyAPIAndIssueToken(String channelId, boolean reIssue) throws Exception {
		String access_token = CoreConfigReader.getString(channelId, CONFIG_STR.ChannelToken.toString(), true);

		LineTokenApiService lineTokenApiService = ApplicationContextProvider.getApplicationContext().getBean(LineTokenApiService.class);
		ObjectNode callVerifyResult = lineTokenApiService.callVerifyAPI(access_token);
		log.debug("callVerifyResult:" + callVerifyResult);

		JsonNode expires_in = callVerifyResult.get("expires_in");
		log.debug("expires_in:" + expires_in);

		boolean isReIssue = false;
		if (expires_in != null) {
			Integer sec = expires_in.asInt();
			sec = sec / 60;
			sec = sec / 60;

			callVerifyResult.put("hr", sec);

			if (sec > 0 && sec < 24) { // Token Expired
				if (reIssue) {
					isReIssue = callRefreshingAPI(channelId);
				}
			}
		} else { // Token Expired
			if (reIssue) {
				isReIssue = callRefreshingAPI(channelId);
			}
		}

		callVerifyResult.put("isReIssue", isReIssue);

		return callVerifyResult;
	}

	public static boolean callRefreshingAPI(String channelId) throws Exception {
		String client_id = CoreConfigReader.getString(channelId, CONFIG_STR.ChannelID.toString(), true);
		String client_secret = CoreConfigReader.getString(channelId, CONFIG_STR.ChannelSecret.toString(), true);

		LineTokenApiService lineTokenApiService = ApplicationContextProvider.getApplicationContext().getBean(LineTokenApiService.class);
		SystemConfigService systemConfigService = ApplicationContextProvider.getApplicationContext().getBean(SystemConfigService.class);
		ObjectNode callRefreshingResult = lineTokenApiService.callRefreshingAPI(client_id, client_secret);
		log.debug("callRefreshingResult: " + callRefreshingResult);

		JsonNode access_token = callRefreshingResult.get("access_token");
		log.debug("access_token: " + access_token);

		if (access_token != null) {
			String token = access_token.asText();

			SystemConfig config = systemConfigService.findSystemConfig(channelId + "." + CONFIG_STR.ChannelToken.toString());
			config.setModifyTime(new Date());
			config.setValue(token);
			systemConfigService.save(config);
			systemConfigService.clearData();
			SystemLogUtil.saveLogDebug(LOG_TARGET_ACTION_TYPE.TARGET_LineApi.toString(), LOG_TARGET_ACTION_TYPE.ACTION_RefreshingApi.toString(), SystemLog.SYSTEM_EVENT, callRefreshingResult,
					channelId);
			return true;
		}

		return false;
	}
}
