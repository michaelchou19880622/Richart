package com.bcs.core.richart.post.akka.handler;

import java.security.MessageDigest;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import com.bcs.core.richart.api.model.LinePointPushModel;
import com.bcs.core.richart.api.model.LinePointResponseModel;
import com.bcs.core.richart.scheduler.service.LinePointPushMessageTaskService;
import com.bcs.core.richart.db.entity.LinePointDetail;
import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.richart.db.entity.LinePointPushMessageRecord;
import com.bcs.core.richart.db.repository.LinePointMainRepository;
import com.bcs.core.richart.db.service.LinePointDetailService;
import com.bcs.core.richart.db.service.LinePointPushMessageRecordService;
import com.bcs.core.bot.api.service.LineAccessApiService;
import com.bcs.core.bot.send.akka.handler.PushMessageActor;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.LINE_HEADER;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.RestfulUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.UntypedActor;

public class LinePointPushMessageActor extends UntypedActor {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(LinePointPushMessageActor.class);

	@Override
	public void onReceive(Object object) throws Exception {
		if(object instanceof LinePointPushModel) {
			// get bean
			LinePointMainRepository linePointMainRepository = ApplicationContextProvider.getApplicationContext().getBean(LinePointMainRepository.class);
			LinePointDetailService linePointDetailService = ApplicationContextProvider.getApplicationContext().getBean(LinePointDetailService.class);
			
			// get push data
			LinePointPushModel pushApiModel = (LinePointPushModel) object;
			logger.info("pushApiModel : {}", pushApiModel);
			
			Long eventId = pushApiModel.getEventId();
			logger.info("eventId : {}", eventId);
			
			JSONArray uids = pushApiModel.getUid();
			logger.info("uids : {}", uids);

			ObjectNode callRefreshingResult = LineAccessApiService.callVerifyAPIAndIssueToken(CONFIG_STR.Default.toString(), true);
			logger.info("callRefreshingResult : {}", callRefreshingResult);
			
			// initialize request header
			HttpHeaders headers = new HttpHeaders();
			String accessToken = CoreConfigReader.getString(CONFIG_STR.Default.toString(), CONFIG_STR.ChannelToken.toString(), true); // Richart.ChannelToken
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			logger.info("headers : {}", headers);
			
			// initialize request body
			JSONObject requestBody = new JSONObject();
			String url = CoreConfigReader.getString(CONFIG_STR.LINE_POINT_MESSAGE_PUSH_URL.toString(), true); // https://api.line.me/pointConnect/v1/issue
			logger.info("url : {}", url);
			
			String clientId = CoreConfigReader.getString(CONFIG_STR.LINE_POINT_API_CLIENT_ID.toString(), true); // 10052
			logger.info("clientId : {}", clientId);
			
		    requestBody.put("clientId", clientId);
			requestBody.put("amount", pushApiModel.getAmount());
			logger.info("1-1 requestBody : {}", requestBody);
			
			for(Integer i = 0; i < uids.length(); i++) {
				// count examine
				LinePointMain linePointMain = linePointMainRepository.findOne(eventId);

				// initialize detail
				LinePointDetail detail = new LinePointDetail();
				detail.setLinePointMainId(eventId);
				detail.setAmount(pushApiModel.getAmount());
				detail.setTriggerTime(pushApiModel.getTriggerTime());
				detail.setSource(pushApiModel.getSource());
				
				if(linePointMain.getSuccessfulCount() >= linePointMain.getTotalCount()) {
					linePointMain.setFailedCount(linePointMain.getFailedCount() + 1);
					linePointMain.setStatus(LinePointMain.STATUS_COMPLETE);
					linePointMainRepository.save(linePointMain);
				
					detail.setUid(uids.get(i).toString());
					detail.setSendTime(new Date());
					detail.setDescription(LinePointDetail.DESCRIPTION_OVERFLOW);
					detail.setStatus(LinePointDetail.STATUS_FAIL);
					linePointDetailService.save(detail);
					continue;
				}
				
				// memberId
				requestBody.put("memberId", uids.get(i));
				logger.info("1-2 requestBody : {}", requestBody);
				
				// orderKey
				MessageDigest salt = MessageDigest.getInstance("SHA-256");
				String hashStr = "" + uids.get(i) + (new Date()).getTime() + pushApiModel.getEventId();
				logger.info("hashStr : {}", hashStr);
				
				String hash = DigestUtils.md5Hex(hashStr);
				logger.info("hash : {}", hash);
				
			    salt.update(hash.toString().getBytes("UTF-8"));
			    
			    String orderKey = bytesToHex(salt.digest()).substring(0, 48);
				logger.info("orderKey : {}", orderKey);
			    
			    requestBody.put("orderKey", orderKey);
				logger.info("1-3 requestBody : {}", requestBody);

			    // applicationTime
			    Long applicationTime = System.currentTimeMillis();
				logger.info("applicationTime : {}", applicationTime);
			    
			    requestBody.put("applicationTime", applicationTime);
				logger.info("1-4 requestBody : {}", requestBody);
			    
				// HttpEntity by header and body
				HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody.toString(), headers);
				logger.info("httpEntity : {}", httpEntity);
				
				RestfulUtil restfulUtil = new RestfulUtil(HttpMethod.POST, url, httpEntity);
				logger.info("restfulUtil.getStatusCode() : {}", restfulUtil.getStatusCode());
				
				// set detail
				try {
					JSONObject responseObject = restfulUtil.execute();
					logger.info("responseObject = {}", responseObject.toString());
					
					String Id = responseObject.getString("transactionId");
					Long Time = responseObject.getLong("transactionTime");
					String Type = responseObject.getString("transactionType");
					Integer Amount = responseObject.getInt("transactionAmount");					
					Integer Balance = responseObject.getInt("balance");
					
					linePointMain.setSuccessfulCount(linePointMain.getSuccessfulCount() + 1);
					if(linePointMain.getSuccessfulCount() >= linePointMain.getTotalCount()) {
						linePointMain.setStatus(LinePointMain.STATUS_COMPLETE);
					}
					linePointMainRepository.save(linePointMain);

					detail.setTranscationId(Id);
					detail.setTranscationTime(Time);
					detail.setTranscationType(Type);
					detail.setTransactionAmount(Amount);
					detail.setTransactionBalance(Balance);
					detail.setDescription("");
					detail.setStatus(LinePointDetail.STATUS_SUCCESS);
				} catch (HttpClientErrorException e) {
					linePointMain.setFailedCount(linePointMain.getFailedCount() + 1);
					linePointMainRepository.save(linePointMain);
					detail.setDescription(e.getResponseBodyAsString());
					detail.setStatus(LinePointDetail.STATUS_FAIL);
				}
				
				detail.setUid(uids.get(i).toString());
				detail.setOrderKey(orderKey);
				detail.setApplicationTime(applicationTime);
				detail.setSendTime(new Date());

				logger.info("detail1: {}", detail.toString());
				linePointDetailService.save(detail);
			}
		}
	}

	private String bytesToHex(byte[] hash) {
	  StringBuffer hexString = new StringBuffer();
	  for (int i = 0; i < hash.length; i++) {
	    String hex = Integer.toHexString(0xFF & hash[i]);
	    if (hex.length() == 1) hexString.append('0');
	    hexString.append(hex);
	  }
	  return hexString.toString();
	}
}