package com.bcs.core.richmenu.core.api.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bcs.core.api.service.model.PostLineResponse;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.LOG_TARGET_ACTION_TYPE;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.log.util.SystemLogUtil;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.HttpClientUtil;
import com.bcs.core.utils.InputStreamUtil;
import com.linecorp.bot.model.richmenu.RichMenu;

@Service
public class RichMenuReceivingApiService {

	/** Logger */
	private static Logger logger = Logger.getLogger(RichMenuReceivingApiService.class);
	@Autowired
	private RichMenuLineApiService lineRichMenuApiService;
	
	// validate
	public boolean richMenuMsgValidate(String receivingMsg) {
		// RichMenuPostBack
		// Rich Menu Post back Validate
		logger.info("REMSG1:"+receivingMsg);
		return false;
//		try {
//			JSONObject recivingObject = new JSONObject(receivingMsg);
//
//			String postbackString = recivingObject.getString("postback");
//			if(null!=postbackString) {
//				JSONObject postbackObject = new JSONObject(postbackString);
//				String data = postbackObject.getString("data");
//				logger.info("front1:"+data.substring(0, 5));
//				logger.info("back1:"+data.substring(6));
//				if(data.substring(0, 5).equals("page=")) {
//					// This is a Rich Menu Post back
//					Integer toPage = Integer.parseInt(data.substring(6));
//					
//					String richMenuId = "23";
//					String uid = "U12";
//					this.callLinkRichMenuToUserAPI(richMenuId, uid);
//					return false;
//				}
//			}
//		}catch(Throwable e){
//			logger.error(ErrorRecord.recordError(e));
//			logger.debug("-------richMenuMsgValidate Fail-------");
//			return false;
//		}
//		return false;
	}
	
	// 設定指定UID的圖文選單API
	public void callLinkRichMenuToUserAPI(String richMenuId, String uid) throws BcsNoticeException  {
		logger.info(" ContentRichMenuUIService callLinkRichMenuToAllUserAPI");
		try{			
			PostLineResponse result = lineRichMenuApiService.callLinkRichMenuToUserAPI(richMenuId, uid, 0);
			if(result.getStatus() != 200){
				throw new Exception(result.getResponseStr());
			}
			logger.debug("callLinkRichMenuToAllUserAPI result:" + result);			
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			throw new BcsNoticeException(e.getMessage());
		}
	}
	
}
