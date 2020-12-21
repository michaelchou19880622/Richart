package com.bcs.web.tracing.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bcs.core.db.entity.LineUser;
import com.bcs.core.db.entity.ShareCampaign;
import com.bcs.core.db.entity.ShareCampaignClickTracing;
import com.bcs.core.db.entity.ShareUserRecord;
import com.bcs.core.db.service.LineUserService;
import com.bcs.core.db.service.ShareCampaignClickTracingService;
import com.bcs.core.db.service.ShareCampaignService;
import com.bcs.core.db.service.ShareUserRecordService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.resource.UriHelper;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.LineLoginUtil;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.MobilePageEnum;
import com.bcs.web.m.controller.MobileUserController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/c")
public class MobileMgmClickTracingController extends BCSBaseController {

	@Autowired
	private MobileUserController mobileUserController;
	@Autowired
	private ShareUserRecordService shareUserRecordService;
	@Autowired
	private ShareCampaignClickTracingService shareCampaignClickTracingService;
	@Autowired
	private ShareCampaignService shareCampaignService;
	@Autowired
	private LineUserService lineUserService;

	@RequestMapping(method = RequestMethod.GET, value = "/m/{tracingIdStr}")
	public String startMgmTracing(@PathVariable String tracingIdStr, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		log.info("startMgmTracing:" + tracingIdStr);
		try {
			LineLoginUtil.addLineoauthLinkInModel(model, UriHelper.getMgmOauth(), tracingIdStr);
			return MobilePageEnum.MgmTracingStartPage.toString();
		} catch (Exception e) {
			log.error(ErrorRecord.recordError(e));
			return mobileUserController.indexPage(request, response, model);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{tracingIdStr}")
	public String startMgmClickTracing(@PathVariable String tracingIdStr, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		log.info("startMgmClickTracing:" + tracingIdStr);
		
		try {
//			LineLoginUtil.addLineoauthLinkInModel(model, UriHelper.getMgmClickOauth(), tracingIdStr);
			LineLoginUtil.addLineoauthLinkInModelForMGMClickTracing(model, UriHelper.getMgmClickOauth(), tracingIdStr);
			
			return MobilePageEnum.MgmTracingStartPage.toString();
		} catch (Exception e) {
			log.error(ErrorRecord.recordError(e));
			return mobileUserController.indexPage(request, response, model);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{tracingIdStr}/{sharedTime}")
	public String startMgmClickTracingWithSharedTime(@PathVariable String tracingIdStr, @PathVariable String sharedTime, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		log.info("startMgmClickTracingWithSharedTime tracingIdStr = {}", tracingIdStr);
		log.info("startMgmClickTracingWithSharedTime sharedTime = {}", sharedTime);
		
		model.addAttribute("sharedTime", sharedTime);
		
		try {
//			LineLoginUtil.addLineoauthLinkInModel(model, UriHelper.getMgmClickOauth(), tracingIdStr);
			LineLoginUtil.addLineoauthLinkInModelForMGMClickTracing(model, UriHelper.getMgmClickOauth(), tracingIdStr);
			
			return MobilePageEnum.MgmTracingStartPage.toString();
		} catch (Exception e) {
			log.error(ErrorRecord.recordError(e));
			return mobileUserController.indexPage(request, response, model);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/validate")
	public void validateMgmClickTracing(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		log.info("validateMgmClickTracing");
		try {
			
			String code = request.getParameter("code");
			log.info("validateMgmClickTracing code:" + code);
			String error = request.getParameter("error");
			log.info("validateMgmClickTracing error:" + error);
			String errorCode = request.getParameter("errorCode");
			log.info("validateMgmClickTracing errorCode:" + errorCode);
			String errorMessage = request.getParameter("errorMessage");
			log.info("validateMgmClickTracing errorMessage:" + errorMessage);
			
			String state = request.getParameter("state");
			log.info("validateMgmClickTracing state:" + state);

			if (StringUtils.isBlank(state)) {
				throw new Exception("TracingId Error:" + state);
			}

			String shareUserRecordId = state.split("_")[0];
			log.info("validateMgmClickTracing shareUserRecordId:" + shareUserRecordId);

			String sharedTime = state.split("_")[1];
			log.info("validateMgmClickTracing sharedTime:" + sharedTime);
			
			SimpleDateFormat sdf_shareTime = new SimpleDateFormat("yyyyMMddHHmmss");
			
			Date date_SharedTime = sdf_shareTime.parse(sharedTime);
			log.info("validateMgmClickTracing date_SharedTime = {}", date_SharedTime);

			ShareUserRecord shareUserRecord = shareUserRecordService.findOne(shareUserRecordId);
			log.info("shareUserRecord = {}", shareUserRecord);
			
			if (shareUserRecord == null) {
				throw new Exception("TracingId Error:" + state);
			}

//            if (StringUtils.isNotBlank(error) || StringUtils.isNotBlank(errorCode)) {
//                String linkUrl = UriHelper.getMgmClickTracingUrl() + state;
//                response.sendRedirect(linkUrl);
//                return;
//            } 

			String campaignId = shareUserRecord.getCampaignId();
			log.info("campaignId = {}", campaignId);
			
			ShareCampaign shareCampaign = shareCampaignService.findOne(campaignId);
			log.info("shareCampaign = {}", shareCampaign);

			// 活動是否存在
			if (shareCampaign == null || !ShareCampaign.STATUS_ACTIVE.equals(shareCampaign.getStatus())) {
				response.sendRedirect(UriHelper.getMgmRedirectPage(null, "查無此活動"));
				return;
			}

			// 活動是否過期
			Date now = new Date();
			if (!now.after(shareCampaign.getStartTime()) || !now.before(shareCampaign.getEndTime())) {
				response.sendRedirect(UriHelper.getMgmRedirectPage(null, "活動已結束，感謝您的參與"));
				return;
//            	response.sendRedirect(UriHelper.getGoMgmPage(campaignId));
//            	return;
			}

			// 取得UID、好友狀態
			Long loginStartTime = System.currentTimeMillis();
			
			Map<String, String> resultMap = LineLoginUtil.callRetrievingAPIfromMGM(code, UriHelper.getMgmClickOauth(), shareUserRecordId);
			log.info("resultMap = {}", resultMap);
			
			Long loginEndTime = System.currentTimeMillis();
			log.info("login Start Time = {}", loginStartTime);
			log.info("login End Time = {}", loginEndTime);
			log.info("login Elapsed Time = {}秒", ((loginEndTime - loginStartTime) / 1000));
			
			String uid = resultMap.get("UID");
			Boolean friendFlag = Boolean.valueOf(resultMap.get("friendFlag"));
			log.info("uid = {}, friendFlag = {}", uid, friendFlag);

			if (StringUtils.isBlank(uid)) {
			    log.error("UID is blank");
	            String linkUrl = UriHelper.bcsMPage;
	            response.sendRedirect(linkUrl);
	            return;
			}
            LineUser lineUser = lineUserService.findByMidAndCreateUnbind(uid);
            log.info("findByMidAndCreateUnbind lineUser = {}", lineUser);
			
			ShareCampaignClickTracing clickTracing = shareCampaignClickTracingService.findByUidAndShareUserRecordId(uid, shareUserRecord.getShareUserRecordId());
			log.info("clickTracing = {}", clickTracing);
			
			if (clickTracing == null && !shareUserRecord.getUid().equals(uid)) { // 此連結未被點過、非本人
				clickTracing = new ShareCampaignClickTracing();
				clickTracing.setUid(uid);
				clickTracing.setShareUserRecordId(shareUserRecord.getShareUserRecordId());
				clickTracing.setModifyTime(new Date());
				clickTracing.setSharedTime(date_SharedTime);
				
				log.info("friendFlag = {}, clickTracing = {}", friendFlag, clickTracing);
				
				shareCampaignClickTracingService.save(clickTracing);
			}
			
			if (friendFlag) { // 好友
				HttpSession session = request.getSession();
				session.setAttribute("MID", uid);
				session.setAttribute("campaignId", campaignId);
				
				log.info("session.setAttribute MID = {}", uid);
				log.info("session.setAttribute campaignId = {}", campaignId);

				response.sendRedirect(UriHelper.getMgmPage());
				return;
			} else { // 非好友
				response.sendRedirect(CoreConfigReader.getString(CONFIG_STR.ADD_LINE_FRIEND_LINK, true));
				return;
			}
		} catch (Exception e) {
			log.error(ErrorRecord.recordError(e));
			String linkUrl = UriHelper.bcsMPage;
			response.sendRedirect(linkUrl);
			return;
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/m/validate")
	public void validateMgmTracing(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		log.info("validateMgmTracing");
		try {
			String code = request.getParameter("code");
			log.info("validateMgmTracing code:" + code);

			String state = request.getParameter("state");
			log.info("validateMgmTracing state:" + state);

			String error = request.getParameter("error");
			log.info("validateMgmTracing error:" + error);

			String errorCode = request.getParameter("errorCode");
			log.info("validateMgmTracing errorCode:" + errorCode);

			String errorMessage = request.getParameter("errorMessage");
			log.info("validateMgmTracing errorMessage:" + errorMessage);

			if (StringUtils.isBlank(state)) {
				throw new Exception("TracingId Error:" + state);
			}

			// 取得UID、好友狀態
			Long loginStartTime = System.currentTimeMillis();

			log.info("UriHelper.getMgmOauth() = {}", UriHelper.getMgmOauth());
			Map<String, String> resultMap = LineLoginUtil.callRetrievingAPIfromMGM(code, UriHelper.getMgmOauth(), state);
			log.info("resultMap = {}", resultMap);

			Long loginEndTime = System.currentTimeMillis();
			log.info("login Time :" + (loginEndTime - loginStartTime) / 1000 + "秒");

			String uid = resultMap.get("UID");
			log.info("uid = {}", uid);

			Boolean friendFlag = Boolean.valueOf(resultMap.get("friendFlag"));
			log.info("friendFlag = {}", friendFlag);
			
			if (friendFlag) { // 好友
				log.info("好友");

				LineUser lineUser = lineUserService.findByMidAndCreateUnbind(uid);
				log.info("lineUser = {}", lineUser);

				HttpSession session = request.getSession();
				log.info("session : {}", session);

				session.setAttribute("MID", uid);
				log.info("session.setAttribute MID : {}", uid);

				session.setAttribute("campaignId", state);
				log.info("session.setAttribute campaignId : {}", state);

				response.sendRedirect(UriHelper.getMgmPage());
				log.info("response.sendRedirect : {}", UriHelper.getMgmPage());

				return;
			} else { // 非好友
				log.info("非好友");

				response.sendRedirect(CoreConfigReader.getString(CONFIG_STR.ADD_LINE_FRIEND_LINK, true));
				log.info("response.sendRedirect : {}", CoreConfigReader.getString(CONFIG_STR.ADD_LINE_FRIEND_LINK, true));

				return;
			}
		} catch (Exception e) {
			log.error("Exception = {}", ErrorRecord.recordError(e));

			String linkUrl = UriHelper.bcsMPage;
			log.info("linkUrl : {}", linkUrl);

			response.sendRedirect(linkUrl);
			log.info("response.sendRedirect : {}", linkUrl);

			return;
		}
	}
}
