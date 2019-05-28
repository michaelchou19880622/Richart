package com.bcs.core.richmenu.web.ui.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


// Core Original
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.log.util.SystemLogUtil;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.LOG_TARGET_ACTION_TYPE;

// Core Added
import com.bcs.core.richmenu.core.db.entity.RichMenuContent;
import com.bcs.core.richmenu.core.db.entity.RichMenuContentDetail;
import com.bcs.core.richmenu.core.api.msg.model.RichMenuAction;
import com.bcs.core.richmenu.core.db.service.RichMenuContentService;
import com.bcs.core.richmenu.core.db.entity.RichMenuContentLink;
import com.bcs.core.richmenu.core.db.service.RichMenuContentFlagService;

// Web Original
import com.bcs.core.web.security.CurrentUser;
import com.bcs.core.web.security.CustomUser;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.BcsPageEnum;

// Web Added
import com.bcs.core.richmenu.web.ui.model.CreateRichMenuModel;
import com.bcs.core.richmenu.web.ui.service.RichMenuContentUIService;


@Controller
@RequestMapping("/bcs")
public class BCSRichMenuController extends BCSBaseController {
	
	@Autowired
	RichMenuContentService contentRichMenuService;
	@Autowired
	RichMenuContentFlagService contentFlagService;
	@Autowired
	RichMenuContentUIService contentRichMenuUIService;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(BCSRichMenuController.class);
	
	@RequestMapping(method = RequestMethod.GET, value = "/edit/richMenuListPage")
	public String richMsgListPage(HttpServletRequest request, HttpServletResponse response) {
		logger.info("richMenuListPage");
		return BcsPageEnum.RichMenuListPage.toString();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/edit/richMenuListDeletePage")
	public String richMsgListDeletePage(HttpServletRequest request, HttpServletResponse response) {
		logger.info("richMenuListDeletePage");
		return BcsPageEnum.RichMenuListDeletePage.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/edit/richMenuCreatePage")
	public String richMsgCreatePage(HttpServletRequest request, HttpServletResponse response) {
		logger.info("richMenuCreatePage");
		return BcsPageEnum.RichMenuCreatePage.toString();
	}
	
	/**
	 * 取得圖文訊息
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getRichMenu/{richId}")
	@ResponseBody
	public ResponseEntity<?> getRichMenu(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String richId) throws IOException {
		logger.info("getRichMenu");

		try{
			Map<String, List<String>> result = contentRichMenuService.getContentRichMenu(richId);

			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 取得圖文訊息列表
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getRichMenuList")
	@ResponseBody
	public ResponseEntity<?> getRichMenuList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("getRichMenuList");

		try{
		    String queryFlag = request.getParameter("queryFlag");
            String pageStr = request.getParameter("page");
            String status = request.getParameter("status");
		    
            boolean isAsc = Boolean.parseBoolean(request.getParameter("isAsc"));
            
			Map<String, List<String>> result = null;

			if(StringUtils.isBlank(queryFlag) && StringUtils.isBlank(pageStr)) {
			    result = contentRichMenuService.getAllContentRichMenuByStatus(status);
			    
			}
			else {
			    int size = CoreConfigReader.getInteger(CONFIG_STR.NUMBER_OF_ITEM_IN_LISTPAGE, true);
                if(size < 0) {
                    size = 20;
                }
                
                int page = Integer.parseInt(pageStr);
                
                result = contentRichMenuService.getAllContentRichMenu(queryFlag, page, size, isAsc, status);
			}
			
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 新增、編輯圖文選單
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/edit/createRichMenu", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> createRichMenu(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser,  
			@RequestBody CreateRichMenuModel createRichMenuModel, @RequestParam String actionType, @RequestParam String richId) throws IOException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if (!validateData(createRichMenuModel)) {
				throw new BcsNoticeException("必填欄位不可為空！");
			}
			
			String adminUserAccount = customUser.getAccount(); //取得登入者的帳號
			
			RichMenuContent contentRichMenu = new RichMenuContent();
			
			List<Map<String, String>> richMenuDetailIdAndLinkIds = new ArrayList<>();
			if (actionType.equals("Edit")) { //變更
				contentRichMenu = contentRichMenuService.getSelectedContentRichMenu(richId);
				
				// 檢查啟用中的RichMenu使用期間
				if(RichMenuContent.STATUS_ACTIVE.equals(contentRichMenu.getStatus())){
					Date newStartUsingTime = sdf.parse(createRichMenuModel.getRichMenuStartUsingTime());
					Date newEndUsingTime = sdf.parse(createRichMenuModel.getRichMenuEndUsingTime());
					List<RichMenuContent> richMenu = contentRichMenuService.findByStatusAndConditionAndUsingTime(RichMenuContent.STATUS_ACTIVE, contentRichMenu.getCondition(), newStartUsingTime, newEndUsingTime);
					if(richMenu != null && richMenu.size() > 0){
						throw new BcsNoticeException("請確認切換條件、使用期間，最多啟用一種圖文選單!");
					}
				}
				
				contentRichMenuService.getPreDetailIdAndLinkId(richId); //取得原先圖文訊息的DetailId與LinkId
			} else { //新增與複製
				richId = checkDuplicateUUID("1");
				contentRichMenu.setRichId(richId);
				contentRichMenu.setStatus(RichMenuContent.STATUS_DISABLE);
			}
			
			contentRichMenu.setRichType(createRichMenuModel.getRichType());
			contentRichMenu.setRichImageId(createRichMenuModel.getRichImageId());
			contentRichMenu.setRichMenuName(createRichMenuModel.getRichMenuName());
			contentRichMenu.setRichMenuTitle(createRichMenuModel.getRichMenuTitle());
			contentRichMenu.setRichMenuShowStatus(createRichMenuModel.getRichMenuShowStatus());
			contentRichMenu.setModifyTime(new Date());
			contentRichMenu.setModifyUser(adminUserAccount);
			contentRichMenu.setCondition(createRichMenuModel.getChangeCondition());
			contentRichMenu.setMenuSize(createRichMenuModel.getMenuSize());
			contentRichMenu.setRichMenuStartUsingTime(sdf.parse(createRichMenuModel.getRichMenuStartUsingTime()));
			contentRichMenu.setRichMenuEndUsingTime(sdf.parse(createRichMenuModel.getRichMenuEndUsingTime()));
			
			List<RichMenuContentDetail> contentRichMenuDetails = new ArrayList<>();
			List<RichMenuContentLink> contentLinks = new ArrayList<>();
			Map<String, List<String>> contentFlagMap = new HashMap<>();
			
			for (int i = 0, max = createRichMenuModel.getRichMenuImgUrls().size(); i < max; i++) {
				CreateRichMenuModel url = createRichMenuModel.getRichMenuImgUrls().get(i);
				String richMenuDetailId = "";
				String linkId = "";
				if (i + 1 > richMenuDetailIdAndLinkIds.size()) { //新增連結
					richMenuDetailId = checkDuplicateUUID("2");
					linkId = checkDuplicateUUID("3");
				} else {
					richMenuDetailId = richMenuDetailIdAndLinkIds.get(i).get("richDetailId");
					linkId = richMenuDetailIdAndLinkIds.get(i).get("linkId");
				}
				
				RichMenuContentDetail contentRichMenuDetail = new RichMenuContentDetail();
				contentRichMenuDetail.setRichDetailId(richMenuDetailId);
				contentRichMenuDetail.setRichId(richId);
				contentRichMenuDetail.setRichDetailLetter(url.getRichDetailLetter());
				contentRichMenuDetail.setStartPointX(url.getStartPointX());
				contentRichMenuDetail.setStartPointY(url.getStartPointY());
				contentRichMenuDetail.setEndPointX(url.getEndPointX());
				contentRichMenuDetail.setEndPointY(url.getEndPointY());
				contentRichMenuDetail.setStatus(RichMenuContentDetail.STATUS_ACTIVE);
				
				contentRichMenuDetail.setActionType(url.getActionType());
				
				contentRichMenuDetails.add(contentRichMenuDetail);
				
				if(RichMenuAction.ACTION_TYPE_SEND_MESSAGE.equals(url.getActionType())){
					contentRichMenuDetail.setLinkId(url.getLinkUrl());
				}
				else if(RichMenuAction.ACTION_TYPE_POSTBACK.equals(url.getActionType())){
					contentRichMenuDetail.setLinkId(url.getLinkUrl());
				}
				else if(RichMenuAction.ACTION_TYPE_WEB.equals(url.getActionType())){

					contentRichMenuDetail.setLinkId(linkId);
					
					RichMenuContentLink contentLink = new RichMenuContentLink();
					contentLink.setLinkId(linkId);
					contentLink.setLinkTag(contentFlagService.concat(url.getLinkTagList(), 50));
					contentLink.setLinkTitle(url.getLinkTitle());
					contentLink.setLinkUrl(url.getLinkUrl());
					contentLink.setModifyTime(new Date());
					contentLink.setModifyUser(adminUserAccount);
					
					contentLinks.add(contentLink);
					
					contentFlagMap.put(contentLink.getLinkId(), url.getLinkTagList());
				}
			}
			
			// 啟用中的圖文選單，編輯流程，先建再刪再更新RichMenuId
			if(RichMenuContent.STATUS_ACTIVE.equals(contentRichMenu.getStatus())){
				String oldRichMenuId = contentRichMenu.getRichMenuId();
				
				String newRichMenuId = contentRichMenuUIService.callCreateRichMenuAPI(CONFIG_STR.Default.toString(), contentRichMenu, contentRichMenuDetails, contentLinks, 0);
				contentRichMenu.setRichMenuId(newRichMenuId);

				SystemLogUtil.saveLogDebug(LOG_TARGET_ACTION_TYPE.TARGET_RichMenuApi.toString(), LOG_TARGET_ACTION_TYPE.ACTION_ActiveRichMenu.toString(), "SYSTEM", newRichMenuId, contentRichMenu.getRichId());
				
				contentRichMenuUIService.callUploadImageAPI(CONFIG_STR.Default.toString(), newRichMenuId, contentRichMenu.getRichImageId());
				
				contentRichMenuUIService.callDeleteRichMenuAPI(CONFIG_STR.Default.toString(), oldRichMenuId);
			}			
			
			contentRichMenuService.createRichMsg(contentRichMenu, contentRichMenuDetails, contentLinks, contentFlagMap);
			
			return new ResponseEntity<>("save success", HttpStatus.OK);
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));

			if(e instanceof BcsNoticeException){
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			}
			else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}	
		}
	}
	
	/** 
	 * 檢查必填欄位不可為空
	 */
	public Boolean validateData(CreateRichMenuModel createRichMenuModel) {
		if (StringUtils.isBlank(createRichMenuModel.getRichType())
				|| StringUtils.isBlank(createRichMenuModel.getRichMenuName())
				|| StringUtils.isBlank(createRichMenuModel.getRichMenuTitle())
				|| StringUtils.isBlank(createRichMenuModel.getRichImageId())) {
			return false;
		}
		for (int i=0, max=createRichMenuModel.getRichMenuImgUrls().size(); i<max; i++) {
			CreateRichMenuModel url = createRichMenuModel.getRichMenuImgUrls().get(i);
			if (StringUtils.isBlank(url.getLinkUrl())) return false;
		}
		return true;
	}
	
	/** 
	 * 回傳一個沒有重覆的uuid
	 */
	public String checkDuplicateUUID(String queryType) {
		String uuid = UUID.randomUUID().toString().toLowerCase();
		Boolean duplicateUUID = contentRichMenuService.checkDuplicateUUID(queryType, uuid);
		while (duplicateUUID) {
			uuid = UUID.randomUUID().toString().toLowerCase();
			duplicateUUID = contentRichMenuService.checkDuplicateUUID(queryType, uuid);
		}
		
		return uuid;
	}
	
	/** 
	 * 停用圖文選單
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/edit/stopRichMenu/{richId}")
	@ResponseBody
	public ResponseEntity<?> stopRichMenu(
			@CurrentUser CustomUser customUser,  
			HttpServletRequest request, 
			HttpServletResponse response,
			@PathVariable String richId) {
		logger.info("deleteRichMenu");

		String richMenuId = request.getParameter("richMenuId");
		try {
			// Check Delete Right
			boolean isAdmin = customUser.isAdmin();
			if(isAdmin) {
				if(StringUtils.isNotBlank(richMenuId)){
					// call RichMenu API
					contentRichMenuUIService.callDeleteRichMenuAPI(CONFIG_STR.Default.toString(), richMenuId);
				}
				contentRichMenuService.disableRichMenu(richId, customUser.getAccount());
								
				return new ResponseEntity<>("Delete Success", HttpStatus.OK);
			} else {
				throw new BcsNoticeException("此帳號沒有刪除權限");
			}
		} catch(Exception e) {
			logger.error(ErrorRecord.recordError(e));

			if(e instanceof BcsNoticeException){
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			}
			else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}	
		}
	}
	
	/** 
	 * 刪除圖文選單
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/deleteRichMenu/{richId}")
	@ResponseBody
	public ResponseEntity<?> deleteRichMenu(
			@CurrentUser CustomUser customUser,  
			HttpServletRequest request, 
			HttpServletResponse response,
			@PathVariable String richId) {
		logger.info("deleteRichMenu");
		
		try {
			// Check Delete Right
			boolean isAdmin = customUser.isAdmin();
			if(isAdmin) {
				
				contentRichMenuService.deleteRichMenu(richId, customUser.getAccount());
				contentRichMenuService.getPreDetailIdAndLinkId(richId);
								
				return new ResponseEntity<>("Delete Success", HttpStatus.OK);
			} else {
				throw new BcsNoticeException("此帳號沒有刪除權限");
			}
		} catch(Exception e) {
			logger.error(ErrorRecord.recordError(e));

			if(e instanceof BcsNoticeException){
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			}
			else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}	
		}
	}
	
	/**
     * 取得RichMenu總分頁數
     */
    @RequestMapping(method = RequestMethod.GET, value = "/edit/getRichMenuPageTotal")
    @ResponseBody
    public ResponseEntity<?> getRichMenuPageTotal(HttpServletRequest request, HttpServletResponse response, @CurrentUser CustomUser customUser) throws IOException {
        logger.info("getRichMenuPageTotal");
        
        Long result = null;
        
        try{
            String queryFlag = request.getParameter("queryFlag");
            
            if(StringUtils.isBlank(queryFlag)){
                result = contentRichMenuService.countTotal();
            }
            else{

                result = contentRichMenuService.countTotalByLikeTitle("%" + queryFlag + "%");
            }
            
            int size = CoreConfigReader.getInteger(CONFIG_STR.NUMBER_OF_ITEM_IN_LISTPAGE, true);
            if(size < 0) {
                size = 20;
            }
            
            Long pageTotal = result/size;
            if(result%size == 0) {
                pageTotal -= 1;
            }
            
            return new ResponseEntity<>(pageTotal, HttpStatus.OK);
        }
        catch(Exception e){
            logger.error(ErrorRecord.recordError(e));

            if(e instanceof BcsNoticeException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
            }
            else{
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
    }
    
    /**
	 * 啟用圖文選單
	 * 
	 * @param customUser
	 * @param request
	 * @param response
	 * @return String
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.DELETE, value ="/edit/activeRichMenuStatus")
	@ResponseBody
	public ResponseEntity<?> activeRichMenuStatus(
			@CurrentUser CustomUser customUser, 
			HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		logger.info("activeRichMenuStatus");
		
		try{
			String richId = request.getParameter("richId");
			if(StringUtils.isNotBlank(richId)){
				RichMenuContent contentRichMenu = contentRichMenuService.getSelectedContentRichMenu(richId);

				List<RichMenuContent> richMenu = contentRichMenuService.findByStatusAndConditionAndUsingTime(RichMenuContent.STATUS_ACTIVE, contentRichMenu.getCondition(), contentRichMenu.getRichMenuStartUsingTime(), contentRichMenu.getRichMenuEndUsingTime());
				if(richMenu != null && richMenu.size() > 0){
					throw new BcsNoticeException("請確認切換條件、使用期間，最多啟用一種圖文選單!");
				}
				
				String richMenuId = contentRichMenuUIService.callCreateRichMenuAPI(CONFIG_STR.Default.toString(), richId);

				SystemLogUtil.saveLogDebug(LOG_TARGET_ACTION_TYPE.TARGET_RichMenuApi.toString(), LOG_TARGET_ACTION_TYPE.ACTION_ActiveRichMenu.toString(), "SYSTEM", richMenuId, contentRichMenu.getRichId());
				
				contentRichMenuUIService.callUploadImageAPI(CONFIG_STR.Default.toString(), richMenuId, contentRichMenu.getRichImageId());
				
				contentRichMenuService.activeRichMenu(richId, richMenuId, customUser.getAccount());
				
				return new ResponseEntity<>("Change Success", HttpStatus.OK);
			}
			else{
				logger.error("iMsgId Null");
				throw new BcsNoticeException("請選擇正確的訊息");
			}
		}
		catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			
			if(e instanceof BcsNoticeException){
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			}
			else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
}
