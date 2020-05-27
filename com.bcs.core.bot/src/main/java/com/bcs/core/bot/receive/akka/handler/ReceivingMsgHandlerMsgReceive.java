package com.bcs.core.bot.receive.akka.handler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bcs.core.api.service.LineProfileService;
import com.bcs.core.api.service.model.LocationModel;
import com.bcs.core.bot.db.entity.MsgBotReceive;
import com.bcs.core.bot.db.service.MsgBotReceiveService;
import com.bcs.core.bot.get.service.GettingMsgContentService;
import com.bcs.core.bot.receive.service.LiveChatProcessService;
import com.bcs.core.bot.receive.service.MessageTransmitService;
import com.bcs.core.bot.record.service.CatchRecordReceive;
import com.bcs.core.bot.send.service.SendingMsgService;
import com.bcs.core.db.entity.ContentResource;
import com.bcs.core.db.entity.LineUser;
import com.bcs.core.db.entity.MsgDetail;
import com.bcs.core.db.entity.MsgInteractiveMain;
import com.bcs.core.db.entity.UserLiveChat;
import com.bcs.core.db.service.LineUserService;
import com.bcs.core.db.service.MsgInteractiveMainService;
import com.bcs.core.db.service.RichMenuService;
import com.bcs.core.db.service.UserLiveChatService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.interactive.model.CampaignFlowData;
import com.bcs.core.interactive.service.InteractiveService;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.ErrorRecord;

import akka.actor.UntypedActor;

public class ReceivingMsgHandlerMsgReceive extends UntypedActor {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(ReceivingMsgHandlerMsgReceive.class);
	
	@Override
	public void onReceive(Object message){
		logger.debug("-------Get Message Save-------");
		InteractiveService interactiveService = ApplicationContextProvider.getApplicationContext().getBean(InteractiveService.class);
		
		boolean recordText = CoreConfigReader.getBoolean(CONFIG_STR.RECORD_RECEIVE_AUTORESPONSE_TEXT, true);
		logger.debug("recordText = " + recordText);

		if (message instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) message;
			MsgBotReceive receive = (MsgBotReceive) map.get("Content");
			Long iMsgId = (Long) map.get("iMsgId");
			
			receive.setReferenceId(iMsgId.toString());

			LineUserService lineUserService = ApplicationContextProvider.getApplicationContext().getBean(LineUserService.class);
			
			String MID = receive.getSourceId();
			logger.debug("MID:" + MID);
			
			LineUser lineUser = lineUserService.findByMidAndCreateUnbind(MID);
			
			// incrementCount CatchRecord Receive
			ApplicationContextProvider.getApplicationContext().getBean(CatchRecordReceive.class).incrementCount();
			
			try {
				String userStatus = lineUser.getStatus();
				
				// Set User Status
				receive.setUserStatus(userStatus);
				
				if(!recordText){
					
					if(interactiveService.checkIsAutoResponse(iMsgId, userStatus)){
						receive.setText("-RemoveBySystemForSecurity-");
					}
				}
				
		    	// Save Record 
				ApplicationContextProvider.getApplicationContext().getBean(MsgBotReceiveService.class).bulkPersist(receive);
			} catch (Exception e) {
				logger.error(ErrorRecord.recordError(e));
			}
		}
		
		ReceivingMsgHandlerMaster.taskCount.addAndGet(-1L);
		ReceivingMsgHandlerMaster.updateDate = Calendar.getInstance().getTime();
		logger.debug("-------Get Message Save End-------");
	}
	
	/**
	 * Handle Msg Receive
	 * 
	 * @param content
	 * @param ChannelId
	 * @param ApiType
	 * @return iMsgId
	 */
	public static Long handleMsgReceive(MsgBotReceive content, String ChannelId, String ChannelName, String ApiType){
		logger.debug("handleMsgReceive");
		
		LineUserService lineUserService = ApplicationContextProvider.getApplicationContext().getBean(LineUserService.class);
		InteractiveService interactiveService = ApplicationContextProvider.getApplicationContext().getBean(InteractiveService.class);
		LiveChatProcessService liveChatService = ApplicationContextProvider.getApplicationContext().getBean(LiveChatProcessService.class);

		boolean recordText = CoreConfigReader.getBoolean(CONFIG_STR.RECORD_RECEIVE_AUTORESPONSE_TEXT, true);
		logger.debug("recordText:" + recordText);

		logger.debug("content.getChannel():" + content.getChannel());
		logger.debug("content.getEventType():" + content.getEventType());
		logger.debug("content.getMsgId():" + content.getMsgId());
		logger.debug("content.getReferenceId():" + content.getReferenceId());
		logger.debug("content.getUserStatus():" + content.getUserStatus());
		logger.debug("content.getPostbackData():" + content.getPostbackData());
		
		logger.debug("ChannelId:" + ChannelId);
		logger.debug("ApiType:" + ApiType);
		
		String MID = content.getSourceId();
		logger.debug("MID:" + MID);
		
		String text = content.getText();
		logger.debug("text:" + text);
		
		String replyToken = content.getReplyToken();
		logger.debug("replyToken:" + replyToken);
		
		try {
			logger.debug("=== Check is line user exist? ===");
			LineUser lineUser = lineUserService.findByMidAndCreateUnbind(MID);
			logger.info("lineUser = " + ((lineUser == null)? "null" : lineUser));
			
			String userStatus = LineUser.STATUS_UNBIND;
			
			if (lineUser != null) {

				logger.info("BEFORE CHECK STATUS : lineUser = {}", lineUser);
				
				/* Check is lineUser's status equal to BLOCK?  */
				if (lineUser.getStatus().equals(LineUser.STATUS_BLOCK)) {
					String isBindedStatus = lineUser.getIsBinded();
					logger.info("isBindedStatus = {}", isBindedStatus);
					
					lineUser.setStatus(isBindedStatus);
					lineUserService.save(lineUser);
					
					lineUser = lineUserService.findByMid(MID);
				}
				
				logger.info("AFTER CHECK STATUS : lineUser = {}", lineUser);
				
				userStatus = lineUser.getStatus();
				
				LineProfileService lineProfileService = ApplicationContextProvider.getApplicationContext().getBean(LineProfileService.class);

				String displayName = lineProfileService.getUserNickName(MID);
				logger.debug("displayName = " + displayName);
			}

			logger.info("userStatus = {}", userStatus);
			
			logger.debug("=== Check content's event type and postback data. ===");
			logger.debug("content.getEventType() = {}", content.getEventType());
			
			if (MsgBotReceive.EVENT_TYPE_POSTBACK.equals(content.getEventType())) {
				text = content.getPostbackData();
				
				logger.info("=== Check is received text match to richmenu custom id? ===");
				logger.info("1-1 text = {}", text);
				
				// 判斷是否為Richmenu關鍵字 (long格式, 去 richmenu_list table 比對) 
				RichMenuService richMenuService = ApplicationContextProvider.getApplicationContext().getBean(RichMenuService.class);
				Boolean isRichmenuCustomId = richMenuService.onCustomIdReceiving(ChannelId, MID, text);
				logger.info("isRichmenuCustomId = {}", isRichmenuCustomId);
				
				if (isRichmenuCustomId) {
					return -2L;
				}

				if (text.contains("action=")) {
					String switchAction = text.split("action=")[1];
					logger.info(">>> switchAction 使用者選擇：{}", switchAction);

					liveChatService.handleSwitchAction(switchAction, ChannelId, MID, replyToken);

					return -3L;
				} else if (text.contains("category=")) {
					String category = text.split("category=")[1];
					logger.info(">>> category 使用者選擇：{}", category);

					liveChatService.startProcess(ChannelId, replyToken, MID, category);

					return -3L;
				} else if (text.contains("waitingAction=")) {
					String waitingAction = text.split("waitingAction=")[1];
					logger.info(">>> waitingAction 使用者選擇：{}", waitingAction);

					liveChatService.handleWaitingAction(waitingAction, MID);

					return -3L;
				} else if (text.contains("leaveMessageAction=")) {
					String leaveMessageAction = text.split("leaveMessageAction=")[1];
					logger.info(">>> leaveMessageAction 使用者選擇：{}", leaveMessageAction);

					liveChatService.handleLeaveMessageAction(leaveMessageAction, ChannelId, MID, replyToken);

					return -3L;
				} else if (text.contains("leaveMsgCategory=")) {
					String leaveMsgCategory = text.split("leaveMsgCategory=")[1];
					logger.info(">>> leaveMsgCategory 使用者選擇：{}", leaveMsgCategory);

					liveChatService.leaveMessage(ChannelId, replyToken, leaveMsgCategory, MID);

					return -3L;
				} else {
					/* 將其餘的 Postback Event Data 丟給 Gateway */
				}
			}
			
			logger.debug("=== Check content's msg type ===");
			logger.debug("content.getMsgType() = {}", content.getMsgType());
			// 問泰咪段邏輯保留
			if(MsgBotReceive.MESSAGE_TYPE_LOCATION.equals(content.getMsgType())) {
				String address = content.getLocationAddress();		// 地址
				String longitude = content.getLocationLongitude();	// 經度
				String latitude = content.getLocationLatitude();	// 緯度
				
				ApplicationContextProvider.getApplicationContext().getBean(MessageTransmitService.class).transmitToBOT(ChannelId, MID, replyToken, new LocationModel(address, longitude, latitude));
				
				return -2L;
			}

			logger.debug("=== Check is in the campaign flow? ===");
			logger.debug("1-2 text = {}", text);
			
            //處理活動流程(如果先前有觸發過，否則一律回傳空值)
			Map<Long, List<MsgDetail>> result = new HashMap<>();
			
            result = handleCampaignFlow(MID, text, content.getReplyToken(), ChannelId, ApiType, content.getMsgId(), content.getMsgType());
            
			if (StringUtils.isNotBlank(text) || (result != null && result.size() > 0)) {

				logger.debug("check 1-1 text = {}", text);
				logger.debug("check 1-1 result = {}", result);
				logger.debug("check 1-1 result.size() = {}", result.size());
				
				if (recordText) {
					logger.info("Get Keyword:{}", text);
				}

				// 判斷是否不在活動處理流程中
				if (result == null || result.size() == 0) {
					// 取得關鍵字回應 設定

					logger.info("check 1-1 MID = {}", MID);
					logger.info("check 1-1 userStatus = {}", userStatus);
					logger.info("check 1-1 text = {}", text);
					
					result = interactiveService.getMatchKeyword(MID, userStatus, text);
					logger.info("result = {}", result);
				}

				if (result != null && result.size() == 1) {
					for (Long iMsgId : result.keySet()) {
						
						List<MsgDetail> details = result.get(iMsgId);

						if (recordText) {
							logger.info("Match Keyword:{}, iMsgId:{}", text, iMsgId);
						}
						
						// 傳送 關鍵字回應
						ApplicationContextProvider.getApplicationContext().getBean(SendingMsgService.class).sendMatchMessage(replyToken, iMsgId, details, ChannelId, MID, ApiType, content.getMsgId());

						// 記錄自動回應 iMsgId
						return iMsgId;
					}
				} else {
					// 紀錄 是否 黑名單選擇
					Long iMsgIdBlack = interactiveService.getMatchBlackKeywordMsgId(userStatus, text);
					
					if (iMsgIdBlack != null) {
						logger.info("iMsgIdBlack = {}", iMsgIdBlack);
						
						// Update 關鍵字回應 記數
						ApplicationContextProvider.getApplicationContext().getBean(MsgInteractiveMainService.class).increaseSendCountByMsgInteractiveId(iMsgIdBlack);
						logger.info("Match BlackKeyword:{}, iMsgIdBlack:{}", text, iMsgIdBlack);
						return iMsgIdBlack;
					} else {
						// 未設定 預設回應
						logger.info("◎ 不符合任何關鍵字，把訊息丟給碩網處理: {}", text);

						UserLiveChat userLiveChat = ApplicationContextProvider.getApplicationContext().getBean(UserLiveChatService.class).findLeaveMsgUserByUIDAndState(MID,
								UserLiveChat.LEAVE_MESSAGE);

						if (userLiveChat == null) {
							logger.info("userLiveChat == null");
							// 20190126 新增參數content.getMsgType()供碩網判斷。
							// 20190126機器人應答狀況下，如使用者傳圖片，須判斷type後在qa-ajax多帶一個參數給碩網判斷
							ApplicationContextProvider.getApplicationContext().getBean(MessageTransmitService.class).transmitToBOT(ChannelId, MID, replyToken, text, content.getMsgId(),
									content.getMsgType());
							return -2L;
						} else {
							logger.info("◎ 使用者 {} 留言，留言內容為：{}", MID, text);

							liveChatService.leaveMessage(ChannelId, replyToken, userLiveChat, text);

							return -2L;
						}
					}
				}
			} else {
				
				logger.debug("MID = {}", MID);
				logger.debug("userStatus = {}", userStatus);
				
				MsgInteractiveMain main = interactiveService.getAutoResponse(MID, userStatus);

				if (main != null) {
					logger.debug("main = {}", main.toString());
					
					Long iMsgId = main.getiMsgId();
					List<MsgDetail> details = interactiveService.getMsgDetails(iMsgId);

					// 傳送 關鍵字回應
					ApplicationContextProvider.getApplicationContext().getBean(SendingMsgService.class).sendMatchMessage(replyToken, iMsgId, details, ChannelId, MID, ApiType, content.getMsgId());

					// 記錄自動回應 iMsgId
					return iMsgId;
				} else {

					logger.debug("ChannelId = {}", ChannelId);
					logger.debug("MID = {}", MID);
					logger.debug("replyToken = {}", replyToken);
					logger.debug("text = {}", text);
					logger.debug("content.getMsgId() = {}", content.getMsgId());
					logger.debug("content.getMsgType() = {}", content.getMsgType());
					
//					ChannelId, MID, replyToken, text, content.getMsgId(), content.getMsgType()
					
					// 20190126 新增參數content.getMsgType()供碩網判斷。
					// 20190126機器人應答狀況下，如使用者傳圖片，須判斷type後在qa-ajax多帶一個參數給碩網判斷
					ApplicationContextProvider.getApplicationContext().getBean(MessageTransmitService.class).transmitToBOT(ChannelId, MID, replyToken, text, content.getMsgId(), content.getMsgType());

					return -2L;
				}
			}
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
		}
		
		return -1L;
	}
	
	private static Map<Long, List<MsgDetail>> handleCampaignFlow(String MID, Object msg, String replyToken, String ChannelId, String ApiType, String msgId, String msgType) throws Exception {
	    logger.debug(("MID=" + MID + ", msg=" + msg + ", replyToken=" + replyToken + ", ChannelId=" + ChannelId + ", ApiType=" + ApiType + ", msgId=" + msgId + ", msgType=" + msgType));
	    Map<Long, List<MsgDetail>> iMsgIdAndMsgDetails = new HashMap<Long, List<MsgDetail>>();
	    InteractiveService interactiveService = ApplicationContextProvider.getApplicationContext().getBean(InteractiveService.class);

	    //是否為Image Message
	    if (MsgBotReceive.MESSAGE_TYPE_IMAGE.equals(msgType)) {
            ContentResource resource = ApplicationContextProvider.getApplicationContext().getBean(GettingMsgContentService.class).getImageMessage(ChannelId, MID, ApiType, msgId);
            logger.debug("receiveImageId:" + resource.getResourceId());
            msg = resource;
	    }
	    
	    CampaignFlowData flowResponse = interactiveService.handleCampaignFlow(MID, msg);
	    
	    if (flowResponse != null) {
	        MsgInteractiveMain main = flowResponse.getMsgInteractiveMain();
	        List<MsgDetail> currentResp = flowResponse.getCurrentResponse();
	        
	        if (CollectionUtils.isNotEmpty(currentResp)) {
	            iMsgIdAndMsgDetails.put(main.getiMsgId(), currentResp);
	        }
	    }
	    
	    return iMsgIdAndMsgDetails;
	}

	public static void trasmitToCustomerService(MsgBotReceive msg, String ChannelId, String ChannelName,String ApiType) throws Exception {
		MessageTransmitService messageTransmitService = ApplicationContextProvider.getApplicationContext().getBean(MessageTransmitService.class);
		
		logger.info(">>> 要傳送給真人客服的訊息：{}", msg);
		Map<Long, List<MsgDetail>> errResult = messageTransmitService.transmitToLiveChat(msg);
		
		// error message response
		if (errResult != null) {
			logger.info("errResult = {}", errResult);
			
			String replyToken = msg.getReplyToken();
			logger.info("replyToken = {}", replyToken);
			
			String MID = msg.getSourceId();
			logger.info("MID = {}", MID);
			
			for (Long iMsgId : errResult.keySet()) {
				List<MsgDetail> details = errResult.get(iMsgId);
				logger.info("details = {}", details);
				
				// 傳送關鍵字回應
				ApplicationContextProvider.getApplicationContext().getBean(SendingMsgService.class).sendMatchMessage(replyToken, iMsgId, details, ChannelId, MID, ApiType, msg.getMsgId());
			}
		}
	}
}
