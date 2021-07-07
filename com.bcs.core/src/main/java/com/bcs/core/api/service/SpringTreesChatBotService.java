package com.bcs.core.api.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.RestfulUtil;

@Service
public class SpringTreesChatBotService {
	
	private static Logger logger = LogManager.getLogger(SpringTreesChatBotService.class);
	
	public JSONObject eventHandler(String ReceivingMsg) throws Exception {
		logger.info("ReceivingMsg = {}", ReceivingMsg);
		
		String eventHandlerURL = CoreConfigReader.getString(CONFIG_STR.SPRINGTREES_API_URL_CHATBOT_EVENTHANDLER.toString(), true);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		Object object = new JSONTokener(ReceivingMsg).nextValue();
		
		String requestBody = "";
		if (object instanceof JSONObject) {
			JSONObject jsonObject = new JSONObject(ReceivingMsg);
			requestBody = jsonObject.toString();
		} else if (object instanceof JSONArray) {
			JSONArray jsonArray = new JSONArray(ReceivingMsg);
			requestBody = jsonArray.toString();
		}
		logger.info("requestBody = {}", requestBody);
		
		if (StringUtils.isBlank(requestBody)) {
			logger.error("requestBody is blank.");
			return null;
		}
		
		HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody, headers);
		
		RestfulUtil restfulUtil = new RestfulUtil(HttpMethod.POST, eventHandlerURL, httpEntity, false);
		
		JSONObject responseObject = restfulUtil.execute();
		logger.info("responseObject = {}", responseObject);
		
		return responseObject;
	}
}
