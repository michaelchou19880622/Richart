package com.bcs.web.ui.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.resource.UriHelper;
import com.bcs.core.richart.api.model.WinningLetterModel;
import com.bcs.core.richart.service.ExportToExcelForWinningLetterService;
import com.bcs.core.richart.service.WinningLetterRecordService;
import com.bcs.core.richart.service.WinningLetterService;
import com.bcs.core.utils.ObjectUtil;
import com.bcs.core.web.security.CurrentUser;
import com.bcs.core.web.security.CustomUser;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.BcsPageEnum;
import com.bcs.web.ui.service.LoadFileUIService;

@Controller
@RequestMapping("/bcs")
public class BCSWinningLetterController extends BCSBaseController {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(BCSWinningLetterController.class);

	@Autowired
	private WinningLetterService winningLetterService;

	@Autowired
	private WinningLetterRecordService winningLetterRecordService;
	
	@Autowired
	private ExportToExcelForWinningLetterService exportToExcelForWinningLetterService;

	/** WinningLetter Main Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterMainPage")
	public String winningLetterMainPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterMainPage");

		return BcsPageEnum.WinningLetterMainPage.toString();
	}

	/** WinningLetter List Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterListPage")
	public String winningLetterListPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("winningLetterListPage");

		model.addAttribute("winninLetterTracingUrlPre", UriHelper.getWinningLetterTracingUrl());

		return BcsPageEnum.WinningLetterListPage.toString();
	}

	/** WinningLetter Reply List Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterReplyListPage")
	public String winningLetterReplyListPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterReplyListPage");

		return BcsPageEnum.WinningLetterReplyListPage.toString();
	}

	/** WinningLetter Signature Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterSignaturePage")
	public String winningLetterSignaturePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterSignaturePage");

		return BcsPageEnum.WinningLetterSignaturePage.toString();
	}

	/** WinningLetter Reply Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/wl/winningLetterReplyPage")
	public String winningLetterReplyPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterReplyPage");

		return BcsPageEnum.WinningLetterReplyPage.toString();
	}

	/** Get winning letter list data **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getWinningLetterList")
	@ResponseBody
	public ResponseEntity<?> getWinningLetterList(HttpServletRequest request, HttpServletResponse response, @RequestParam String name, @RequestParam String status) throws Exception {
		logger.info("getWinningLetterList");

		logger.info("name = {}", name);
		logger.info("status = {}", status);
		
		// 檢查name是否包含'%' 如果有責替換成其他的字符

		try {
			List<WinningLetter> list_WinningLetter = new ArrayList<>();
			
			if (StringUtils.isBlank(name)) {
				list_WinningLetter = winningLetterService.findAllByStatus(status);
			}
			else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(status)) {
				list_WinningLetter = winningLetterService.findAllByNameContainingAndStatus(name, status);
			}
			else {
				list_WinningLetter = winningLetterService.findAllByStatus(WinningLetter.STATUS_ACTIVE);
			}
			
			logger.info("list_WinningLetter = {}", list_WinningLetter);

			return new ResponseEntity<>(list_WinningLetter, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("Exception : ", e);

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/** Get winning letter data **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getWinningLetter")
	@ResponseBody
	public ResponseEntity<?> getWinningLetter(HttpServletRequest request, HttpServletResponse response, @RequestParam String winningLetterId) throws IOException {
		logger.info("getWinningLetter");

		logger.info("winningLetterId = {}", winningLetterId);

		try {
			if (winningLetterId != null) {
				WinningLetter winningLetter = winningLetterService.findById(Long.valueOf(winningLetterId));
				logger.info("winningLetter = {}", winningLetter);

				if (winningLetter != null) {
					return new ResponseEntity<>(winningLetter, HttpStatus.OK);
				}
			}

			throw new Exception("Could not find the winning letter by id : " + winningLetterId);
		} catch (Exception e) {
			logger.info("Exception : ", e);

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/** Count reply people **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/countWinningLetterReplyPeople")
	@ResponseBody
	public ResponseEntity<?> countWinningLetterReplyPeople(HttpServletRequest request, HttpServletResponse response, @RequestParam String winningLetterId) throws IOException {
		logger.info("countWinningLetterReplyPeople");

		logger.info("winningLetterId = {}", winningLetterId);

		try {
			if (winningLetterId != null) {

				Integer result = winningLetterRecordService.countByWinningLetterId(winningLetterId);

				if (result != null) {
					return new ResponseEntity<>(result, HttpStatus.OK);
				}
			}

			throw new Exception("Could not find the winning letter record by WinningLetterId : " + winningLetterId);
		} catch (Exception e) {
			logger.info("Exception : ", e);

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/** Create WinningLetter **/
	@RequestMapping(method = RequestMethod.POST, value = "/api/createWinningLetter", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> createWinningLetter(HttpServletRequest request, HttpServletResponse response, @RequestBody String winningLetterContent, @CurrentUser CustomUser customUser)
			throws Exception {

		logger.info("createWinningLetter");

		logger.info("RequestBody : winningLetterContent = {}", winningLetterContent);

		// Get the currently logged in user.
		String currentUser = customUser.getAccount();
		logger.info("customUser = {}", currentUser);

		WinningLetterModel winningLetterModel = ObjectUtil.jsonStrToObject(winningLetterContent, WinningLetterModel.class);
		logger.info("winningLetterModel.toString() = {}", winningLetterModel.toString());

		Date currentDateTime = new Date();
		logger.info("currentDateTime = {}", currentDateTime);

		// Check is the winning letter name already exist?
		WinningLetter winningLetter = winningLetterService.findByName(winningLetterModel.getName());
		logger.info("winningLetter = {}", winningLetter);

		if (winningLetter != null) {
			return new ResponseEntity<>("中獎回函名稱重複，請重新輸入。", HttpStatus.BAD_REQUEST);
		}

		winningLetter = new WinningLetter();
		winningLetter.setName(winningLetterModel.getName());
		winningLetter.setStartTime(winningLetterModel.getStartTime());
		winningLetter.setEndTime(winningLetterModel.getEndTime());
		winningLetter.setGift(winningLetterModel.getGift());
		winningLetter.setStatus(winningLetterModel.getStatus());
		winningLetter.setCreateTime(currentDateTime);
		winningLetter.setCreateUser(currentUser);

		Long winningLetterId = winningLetterService.save(winningLetter);
		logger.info("winningLetterId = {}", winningLetterId);

		return new ResponseEntity<>(String.format("The winningletter (id : %d) is created", winningLetterId), HttpStatus.OK);
	}

	/** Edit WinningLetter **/
	@RequestMapping(method = RequestMethod.POST, value = "/api/editWinningLetter", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> editWinningLetter(HttpServletRequest request, HttpServletResponse response, @RequestBody String winningLetterContent, @CurrentUser CustomUser customUser)
			throws Exception {

		logger.info("editWinningLetter");

		logger.info("RequestBody : winningLetterContent = {}", winningLetterContent);

		// Get the currently logged in user.
		String currentUser = customUser.getAccount();
		logger.info("customUser = {}", currentUser);

		WinningLetterModel winningLetterModel = ObjectUtil.jsonStrToObject(winningLetterContent, WinningLetterModel.class);
		logger.info("winningLetterModel = {}", winningLetterModel.toString());

		Date currentDateTime = new Date();
		logger.info("currentDateTime = {}", currentDateTime);

		// Check is the winning letter name already exist?
		WinningLetter winningLetter = winningLetterService.findByName(winningLetterModel.getName());
		logger.info("winningLetter = {}", winningLetter);

		if (winningLetter == null) {

			return new ResponseEntity<>("該中獎回函活動資料不存在，可能已被刪除，請返回上一頁重新選擇。", HttpStatus.BAD_REQUEST);
		}

		winningLetter.setName(winningLetterModel.getName());
		winningLetter.setStartTime(winningLetterModel.getStartTime());
		winningLetter.setEndTime(winningLetterModel.getEndTime());
		winningLetter.setGift(winningLetterModel.getGift());
		winningLetter.setStatus(winningLetterModel.getStatus());
		winningLetter.setModifyTime(currentDateTime);
		winningLetter.setModifyUser(currentUser);

		Long winningLetterId = winningLetterService.save(winningLetter);
		logger.info("winningLetterId = {}", winningLetterId);

		return new ResponseEntity<>(String.format("The winningletter (id : %d) is updated", winningLetterId), HttpStatus.OK);
	}

	/** Delete WinningLetter **/
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/deleteWinningLetter")
	@ResponseBody
	public ResponseEntity<?> deleteWinningLetter(HttpServletRequest request, HttpServletResponse response, @RequestParam String winningLetterId, @CurrentUser CustomUser customUser)
			throws IOException {
		logger.info("deleteWinningLetter");

		logger.info("winningLetterId = {}", winningLetterId);

		// Get the currently logged in user.
		String currentUser = customUser.getAccount();
		logger.info("customUser = {}", currentUser);

		Date currentDateTime = new Date();
		logger.info("currentDateTime = {}", currentDateTime);

		try {
			if (winningLetterId != null) {
				WinningLetter winningLetter = winningLetterService.findById(Long.valueOf(winningLetterId));
				logger.info("winningLetter = {}", winningLetter);

				winningLetter.setModifyTime(currentDateTime);
				winningLetter.setModifyUser(currentUser);
				winningLetter.setStatus(WinningLetter.STATUS_DELETED);

				if (winningLetter != null) {
					Long winningLetterEffectRowId = winningLetterService.saveWithUserAccount(winningLetter, customUser.getAccount());
					logger.info("winningLetterEffectRowId = {}", winningLetterEffectRowId);

					return new ResponseEntity<>(winningLetterEffectRowId, HttpStatus.OK);
				}
			}

			throw new Exception("Could not find the winning letter by id : " + winningLetterId);
		} catch (Exception e) {
			logger.info("Exception : ", e);

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/** Active WinningLetter **/
	@RequestMapping(method = RequestMethod.POST, value = "/admin/activeWinningLetter")
	@ResponseBody
	public ResponseEntity<?> activeWinningLetter(HttpServletRequest request, HttpServletResponse response, @RequestParam String winningLetterId, @CurrentUser CustomUser customUser)
			throws IOException {
		logger.info("activeWinningLetter");

		logger.info("winningLetterId = {}", winningLetterId);

		// Get the currently logged in user.
		String currentUser = customUser.getAccount();
		logger.info("customUser = {}", currentUser);

		Date currentDateTime = new Date();
		logger.info("currentDateTime = {}", currentDateTime);

		try {
			if (winningLetterId != null) {
				WinningLetter winningLetter = winningLetterService.findById(Long.valueOf(winningLetterId));
				logger.info("winningLetter = {}", winningLetter);

				winningLetter.setModifyTime(currentDateTime);
				winningLetter.setModifyUser(currentUser);
				winningLetter.setStatus(WinningLetter.STATUS_ACTIVE);

				if (winningLetter != null) {
					Long winningLetterEffectRowId = winningLetterService.saveWithUserAccount(winningLetter, customUser.getAccount());
					logger.info("winningLetterEffectRowId = {}", winningLetterEffectRowId);

					return new ResponseEntity<>(winningLetterEffectRowId, HttpStatus.OK);
				}
			}

			throw new Exception("Could not find the winning letter by id : " + winningLetterId);
		} catch (Exception e) {
			logger.info("Exception : ", e);

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/** Inactive WinningLetter **/
	@RequestMapping(method = RequestMethod.POST, value = "/admin/inactiveWinningLetter")
	@ResponseBody
	public ResponseEntity<?> inactiveWinningLetter(HttpServletRequest request, HttpServletResponse response, @RequestParam String winningLetterId, @CurrentUser CustomUser customUser)
			throws IOException {
		logger.info("inactiveWinningLetter");

		logger.info("winningLetterId = {}", winningLetterId);

		// Get the currently logged in user.
		String currentUser = customUser.getAccount();
		logger.info("customUser = {}", currentUser);

		Date currentDateTime = new Date();
		logger.info("currentDateTime = {}", currentDateTime);

		try {
			if (winningLetterId != null) {
				WinningLetter winningLetter = winningLetterService.findById(Long.valueOf(winningLetterId));
				logger.info("winningLetter = {}", winningLetter);

				winningLetter.setModifyTime(currentDateTime);
				winningLetter.setModifyUser(currentUser);
				winningLetter.setStatus(WinningLetter.STATUS_INACTIVE);

				if (winningLetter != null) {
					Long winningLetterEffectRowId = winningLetterService.saveWithUserAccount(winningLetter, customUser.getAccount());
					logger.info("winningLetterEffectRowId = {}", winningLetterEffectRowId);

					return new ResponseEntity<>(winningLetterEffectRowId, HttpStatus.OK);
				}
			}

			throw new Exception("Could not find the winning letter by id : " + winningLetterId);
		} catch (Exception e) {
			logger.info("Exception : ", e);

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/** Export winning letter list to excel **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/exportToExcelForWinningLetter")
	@ResponseBody
	public void exportToExcelForWinningLetter(HttpServletRequest request, HttpServletResponse response, @CurrentUser CustomUser customUser, @RequestParam String name, @RequestParam String status)
			throws IOException {

		logger.info("name = {}", name);
		logger.info("status = {}", status);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		
		String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
		logger.info("filePath = {}", filePath);
		
		Date date = new Date();
		
		String fileName = "WinningLetterList_" + sdf.format(date) + ".xlsx";
		logger.info("fileName = {}", fileName);
		
		try {
			File folder = new File(filePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			exportToExcelForWinningLetterService.exportToExcelForWinningListByLikeNameAndStatus(filePath, fileName, name, status);

		} catch (Exception e) {
			logger.error("Exception : ", e);
		}

		LoadFileUIService.loadFileToResponse(filePath, fileName, response);
	}

	/** Get winning letter reply list data by winning letter id **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getWinningLetterReplyList")
	@ResponseBody
	public ResponseEntity<?> getWinningLetterReplyList(HttpServletRequest request, HttpServletResponse response, @RequestParam String winningLetterId, @RequestParam String winnerName) throws Exception {
		logger.info("getWinningLetterReplyList");

		logger.info("winningLetterId = {}", winningLetterId);
		logger.info("winnerName = {}", winnerName);
		
		try {
			List<WinningLetterRecord> list_WinningLetterRecords = null;
			
			if (StringUtils.isNotBlank(winningLetterId) && StringUtils.isNotBlank(winnerName)) {
				list_WinningLetterRecords = winningLetterRecordService.findAllByNameContainingAndWinningLetterId(winnerName, Long.valueOf(winningLetterId));
			}
			else {
				list_WinningLetterRecords = winningLetterRecordService.findAllByWinningLetterId(Long.valueOf(winningLetterId));
			}
			
			
			logger.info("list_WinningLetterRecords = {}", list_WinningLetterRecords);

			return new ResponseEntity<>(list_WinningLetterRecords, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("Exception : ", e);

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	/** Export winner reply list to excel **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/exportToExcelForWinnerReplyList")
	@ResponseBody
	public void exportToExcelForWinnerReplyList(HttpServletRequest request, HttpServletResponse response, @CurrentUser CustomUser customUser, @RequestParam String winningLetterId)
			throws IOException {

		logger.info("winningLetterId = {}", winningLetterId);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		
		String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
		logger.info("filePath = {}", filePath);
		
		Date date = new Date();
		
		String fileName = "WinnerReplyDetailList_" + sdf.format(date) + ".xlsx";
		logger.info("fileName = {}", fileName);
		
		try {
			File folder = new File(filePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			exportToExcelForWinningLetterService.exportToExcelForWinnerReplyListByWinningLetterId(filePath, fileName, winningLetterId);

		} catch (Exception e) {
			logger.error("Exception : ", e);
		}

		LoadFileUIService.loadFileToResponse(filePath, fileName, response);
	}
}
