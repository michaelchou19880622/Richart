package com.bcs.web.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.ObjectUtil;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.web.security.CurrentUser;
import com.bcs.core.web.security.CustomUser;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.BcsPageEnum;
import com.bcs.web.aop.ControllerLog;

import com.bcs.web.ui.service.LinePointUIService;
import com.bcs.core.richart.api.model.LinePointPushModel;
import com.bcs.core.richart.akka.service.LinePointPushAkkaService;
import com.bcs.core.richart.db.entity.LinePointMain;

@Controller
@RequestMapping("/bcs")
public class BCSLinePointController extends BCSBaseController {

	@Autowired
	private LinePointUIService linePointUIService;
	@Autowired
	private LinePointPushAkkaService linePointPushAkkaService;
	/** Logger */
	private static Logger logger = Logger.getLogger(BCSLinePointController.class);

	
	
	@ControllerLog(description = "建立 Line Point 活動")
	@RequestMapping(method = RequestMethod.GET, value = "/market/linePointCreatePage")
	public String linePointCreatePage(HttpServletRequest request, HttpServletResponse response) {
		logger.info("linePointCreatePage");
		return BcsPageEnum.LinePointCreatePage.toString();
	}

	@ControllerLog(description = "Line Point 活動列表")
	@RequestMapping(method = RequestMethod.GET, value = "/market/linePointListPage")
	public String linePointListPage(HttpServletRequest request, HttpServletResponse response) {
		logger.info("linePointListPage");
		return BcsPageEnum.LinePointListPage.toString();
	}

	@ControllerLog(description = "發送 Line Point 活動")
	@RequestMapping(method = RequestMethod.GET, value = "/market/linePointPushPage")
	public String linePointPushPage(HttpServletRequest request, HttpServletResponse response) {
		logger.info("linePointPushPage");
		return BcsPageEnum.LinePointPushPage.toString();
	}

	@ControllerLog(description = "Line Point 活動報表")
	@RequestMapping(method = RequestMethod.GET, value = "/market/linePointReportPage")
	public String linePointReportPage(HttpServletRequest request, HttpServletResponse response) {
		logger.info("linePointReportPage");
		return BcsPageEnum.LinePointReportPage.toString();
	}

	
	@ControllerLog(description = "Add/Edit Line Point Main")
	@RequestMapping(method = RequestMethod.POST, value = "/market/createLinePointMain", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> createLinePointMain(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser, @RequestBody LinePointMain linePointMain) throws IOException {
		logger.info("createLinePointMain");
		try {
			if (linePointMain != null) {
				String adminUserAccount = customUser.getAccount();
				LinePointMain result = linePointUIService.saveLinePointMainFromUI(linePointMain, adminUserAccount);
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else 
				throw new Exception("LinePointMain is Null");
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
			if (e instanceof BcsNoticeException) 
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else 
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ControllerLog(description = "Get All Line Point Main")
	@RequestMapping(method = RequestMethod.GET, value = "/market/getLinePointMainList")
	@ResponseBody
	public ResponseEntity<?> getLinePointMainList(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser ) throws IOException {
		logger.info("getLinePointMainList");
		List<LinePointMain> result = new ArrayList();
		List<LinePointMain> list = linePointUIService.linePointMainFindAll();
		result.addAll(list);
		logger.debug("result:" + ObjectUtil.objectToJsonStr(result));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@ControllerLog(description="Delete Line Point Main")
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/deleteLinePointMain")
	@ResponseBody
	public ResponseEntity<?> deleteLinePointMain( HttpServletRequest request,  HttpServletResponse response, @CurrentUser CustomUser customUser,
			@RequestParam(required=false) String campaignId, @RequestParam(required=false) String listType) throws IOException {
		logger.info("deleteLinePointMain");
		try{
			if(StringUtils.isNotBlank(campaignId)){
				logger.info("campaignId:" + campaignId);
				linePointUIService.deleteFromUI(Long.parseLong(campaignId), customUser.getAccount(), listType);
				return new ResponseEntity<>("Delete Success", HttpStatus.OK);
			} else
				throw new Exception("ID IS NULL");
		} catch(Exception e) {
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException)
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ControllerLog(description = "Push Line Point")
	@RequestMapping(method = RequestMethod.POST, value = "/market/pushLinePoint", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> pushLinePoint(HttpServletRequest request, HttpServletResponse response, @CurrentUser CustomUser customUser,
			@RequestBody List<String> uids, @RequestParam Long eventId) throws IOException {
		try {
				JSONArray uid = new JSONArray();
				for(String u : uids) {
					uid.put(u);
				}
				LinePointMain linePointMain = linePointUIService.linePointMainFindOne(eventId);				
				LinePointPushModel linePointPushModel = new LinePointPushModel();
				linePointPushModel.setAmount(linePointMain.getAmount());
				linePointPushModel.setUid(uid);
				linePointPushModel.setEventId(eventId);
				linePointPushModel.setSource(LinePointPushModel.SOURCE_TYPE_BCS);
				linePointPushModel.setSendTimeType(LinePointPushModel.SEND_TYPE_IMMEDIATE);
				linePointPushModel.setTriggerTime(new Date());
				linePointPushAkkaService.tell(linePointPushModel);
				return new ResponseEntity<>("",HttpStatus.OK);
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
			if (e instanceof BcsNoticeException) 
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else 
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}