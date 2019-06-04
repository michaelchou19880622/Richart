package com.bcs.core.utils;

import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class ChtCDNUtil{
	private static Logger logger = Logger.getLogger(ChtCDNUtil.class);
	
	public static void clearCacheData(String path){
		logger.info("Clear CDN cache");
		
		String url = CoreConfigReader.getString(CONFIG_STR.CHT_CDN_DIRECTORYPURGE_API_URL);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap();
		map.add("token", CoreConfigReader.getString(CONFIG_STR.CHT_CDN_API_TOKEN.toString(), true));
		map.add("serviceID", "1");
		map.add("serviceName", "hpi");
		map.add("useDeviceType", "0");
		map.add("dropQueryString", "1");
		map.add("url", CoreConfigReader.getString(CONFIG_STR.BaseUrlHTTPS) + path);
		
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(map, headers);
		
		RestfulUtil restfulUtil = null;
		try{
			restfulUtil = new RestfulUtil(HttpMethod.POST, url, httpEntity);
			
			JSONObject response = restfulUtil.execute();
			
			logger.info("Response: " + response);
			logger.info("Finish clear CDN cache");
		}catch (KeyManagementException|NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
