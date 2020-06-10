package com.bcs.core.richmenu.core.api.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.containers.mxf.MXFDemuxer.Fast;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.api.service.model.PostLineResponse;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.richmenu.core.db.entity.RichMenuContent;
import com.bcs.core.richmenu.core.db.repository.RichMenuContentRepository;
import com.bcs.core.utils.ErrorRecord;

@Service
public class RichMenuReceivingApiService {

	/** Logger */
	private static Logger logger = LogManager.getLogger(RichMenuReceivingApiService.class);
	
	@Autowired
	private RichMenuLineApiService lineRichMenuApiService;
	
	@Autowired
	private RichMenuContentRepository richMenuContentRepository;
	
	// validate
	public boolean richMenuMsgValidate(String receivingMsg) {
		try {
			logger.info("receivingMsg:" + receivingMsg);

			JSONObject recivingObject = new JSONObject(receivingMsg);
			logger.info("recivingObject:" + recivingObject.toString());

			JSONArray eventsArray = recivingObject.getJSONArray("events");
			logger.info("eventsArray:" + eventsArray.toString());

			Object firstEvent = eventsArray.get(0);
			logger.info("firstEvent:" + firstEvent.toString());

			JSONObject firstEventObject = (JSONObject) firstEvent;
			logger.info("firstEventObject:" + firstEventObject.toString());

			// source-uid
			JSONObject sourceObject = firstEventObject.getJSONObject("source");
			logger.info("sourceObject:" + sourceObject.toString());

			String uid = sourceObject.getString("userId");
			logger.info("uid:" + uid);

			// Check is postback message
			if (!firstEventObject.has("postback")) {

				logger.info("Message not match the Postback event");
				
				return false;
			}

			// postback-data
			JSONObject postbackObject = firstEventObject.getJSONObject("postback");
			logger.info("postbackObject" + postbackObject);

			String richId = postbackObject.getString("data");
			logger.info("richId:" + richId);

			RichMenuContent richMenuContent = richMenuContentRepository.findOne(richId);
			logger.info("richMenuContent:" + richMenuContent);

			String richMenuId = richMenuContent.getRichMenuId();
			logger.info("richMenuId:" + richMenuId);
			
			logger.debug("-------richMenuMsgValidate Success-------");
			
			callLinkRichMenuToUserAPI(richMenuId, uid);
			return true;
		}catch(Throwable e){
			logger.error(ErrorRecord.recordError(e));
			logger.debug("------- not a richMenuMsg -------");
			return false;
		}
	}
	
	// 設定指定UID的圖文選單API
	public void callLinkRichMenuToUserAPI(String richMenuId, String uid) throws BcsNoticeException  {
		logger.info(" ContentRichMenuUIService callLinkRichMenuToAllUserAPI");
		try{			
			PostLineResponse result = lineRichMenuApiService.callLinkRichMenuToUserAPI(richMenuId, uid, 0);
			if(result.getStatus() != 200){
				throw new Exception(result.getResponseStr());
			}
			logger.debug("callLinkRichMenuToUserAPI result:" + result);			
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			throw new BcsNoticeException(e.getMessage());
		}
	}
	
}
