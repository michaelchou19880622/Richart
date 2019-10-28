package com.bcs.core.db.service;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bcs.core.db.entity.RichMenuList;
import com.bcs.core.db.repository.RichMenuListRepository;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;

@Service
public class RichMenuService {

	/** Logger */
	private static Logger logger = Logger.getLogger(RichMenuService.class);

	private static final String AUTHORIZATION = "Authorization";
	private static final String BEARER = "Bearer ";

	@Autowired
	private RichMenuListRepository richMenuRepository;
	
	private RestTemplate restTemplate;
	
	
	/**
	 * 判斷是否為圖文選單的更換 
	 */
	public Boolean onCustomIdReceiving(String ChannelId, String uid, String text) {

		logger.info("ChannelId = " + ChannelId);
		logger.info("uid = " + uid);
		logger.info("text = " + text);
		
		RichMenuList richMenuList = null;
		
		// Step 1. 依據 customId 查詢是否有對應的圖文選單
		if (isNumber(text)) {
			richMenuList = this.richMenuRepository.findByCustomeId(Long.parseLong(text));
			logger.info("richMenuList = " + richMenuList);
		}
		
		// Step 2. 如果有的話，才進行更換動作
		if (null != richMenuList) {
			restTemplate = new RestTemplate();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			headers.set(AUTHORIZATION, BEARER + this.getLineToken(ChannelId));
			HttpEntity<Object> request = new HttpEntity<>(headers);
			this.restTemplate.postForObject(this.getLinkRichMenuUrl(uid, richMenuList.getRichMenuId()), request, String.class);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * 判斷是否為數字 
	 */
	private boolean isNumber(String msg) {
		Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
		return pattern.matcher(msg).matches();
	}
	
	/**
	 * 取得 link user richmenu url 
	 */
	private String getLinkRichMenuUrl(String lineUserId, String richMenuId) {
		
		logger.info(String.format("lineUserId = %s, richMenuId = %s", lineUserId, richMenuId));
		
		String richMenuLinkUser = CoreConfigReader.getString(CONFIG_STR.LINE_RICH_MENU_LINK_API);
		logger.info("richMenuLinkUser = " + richMenuLinkUser);
		richMenuLinkUser = richMenuLinkUser.replace("{userId}", lineUserId);
		richMenuLinkUser = richMenuLinkUser.replace("{richMenuId}", richMenuId);
		
		logger.info(String.format("richMenuLinkUser = %s", richMenuLinkUser));
		
		return richMenuLinkUser;
		
//		return LineURL.LINK_RICH_MENU.toString() + lineUserId + LineURL.RICHMENU.toString() + richMenuId;
	}
	
	/**
	 * 取得 Line 的 Token
	 * @return
	 */
	private String getLineToken(String ChannelId) {
		
		String access_token = CoreConfigReader.getString(ChannelId, CONFIG_STR.ChannelToken.toString(), true);
		
		logger.info(String.format("access_token = %s", access_token));
		
		return access_token;

//		SystemConfigEntity systemConfig = this.systemConfigRepository.findOneByKey(this.properties.getChannelTokenKey());
//		return systemConfig.getValue();
	}
	
}
