package com.bcs.core.richart.scheduler.service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.bcs.core.api.service.model.LiveChatStartResponse;
import com.bcs.core.bot.scheduler.handler.CheckUserLiveChatStatus;
import com.bcs.core.db.entity.AdminUser;
import com.bcs.core.db.entity.UserLiveChat;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.richart.db.repository.LinePointMainRepository;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.RestfulUtil;

@Service
public class MGMService{
	/** Logger */
	//private static Logger logger = Logger.getLogger(MGMService.class);
	
//	try {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//logger.info("[Schedule] MGMService start at " + sdf.format(new Date()));
		
//		String autoReplyChannelName = CoreConfigReader.getString(CONFIG_STR.AUTOREPLY_CHANNEL_NAME.toString(), true);
//		List<UserLiveChat> userList = userLiveChatService.findWaitingAndInProgressUser();	// 找出「等待中」以及「在客服流程中」的使用者
//		
//		
//		
//		for(UserLiveChat user : userList) {				
//			Long chatId = user.getChatId();
//			String hash = user.getHash();
//			String url = CoreConfigReader.getString(CONFIG_STR.LIVECHAT_CHECK_API_URL.toString()) + chatId.toString() + "/" + hash;
//			
//			HttpHeaders headers = new HttpHeaders();
//			headers.set("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
//			
//			HttpEntity<?> httpEntity = new HttpEntity<>(headers);
//			
//			RestfulUtil restfulUtil = new RestfulUtil(HttpMethod.GET, url, httpEntity, CoreConfigReader.getBoolean(CONFIG_STR.SYSTEM_USE_PROXY.toString()));
//			
//			JSONObject responseObject = restfulUtil.execute();
//			
//			if(!Boolean.valueOf(responseObject.getString("error"))) {
//				if(user.getStatus().equals(UserLiveChat.IN_PROGRESS)) {
//					if(responseObject.getString("chat_status").equals(String.valueOf(LiveChatStartResponse.NON_OFFICE_HOUR))) {
//						lineSwitchApiService.executeSwitch(CoreConfigReader.getString(autoReplyChannelName, "DestinationId", true), user.getUID(), "");
//						
//						user.setStatus(UserLiveChat.FINISH);
//						user.setModifyTime(new Date());
//						
//						userLiveChatService.save(user);
//					}
//				} else if(user.getStatus().equals(UserLiveChat.WAITING)) {
//					if(responseObject.getString("chat_status").equals(String.valueOf(LiveChatStartResponse.NON_OFFICE_HOUR))) {
//						user.setStatus(UserLiveChat.DISCARD);
//						user.setModifyTime(new Date());
//						
//						userLiveChatService.save(user);
//					}
//				}
//			} else {
//				throw new Exception("[Schedule] Encounter problems when checking user's live chat status.");
//			}
//		}
//	} catch(Exception e) {
//		String error = ErrorRecord.recordError(e, false);
//		//logger.error(error);
//	}
}
