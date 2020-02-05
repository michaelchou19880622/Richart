package com.bcs.web.ui.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import com.bcs.core.richart.service.ServiceExportWinnerInfoToPDF;
import com.bcs.core.richart.service.WinningLetterRecordService;
import com.bcs.core.richart.service.WinningLetterService;
import com.bcs.core.utils.ObjectUtil;
import com.bcs.core.web.security.CurrentUser;
import com.bcs.core.web.security.CustomUser;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.BcsPageEnum;
import com.bcs.web.ui.service.LoadFileUIService;
import com.google.common.io.Files;

@Controller
@RequestMapping("/bcs")
public class BCSWinningLetterController extends BCSBaseController {

	private static final String DEFAULT_PAGE_SIZE = "10";
	
	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(BCSWinningLetterController.class);

	@Autowired
	private WinningLetterService winningLetterService;

	@Autowired
	private WinningLetterRecordService winningLetterRecordService;
	
	@Autowired
	private ExportToExcelForWinningLetterService exportToExcelForWinningLetterService;
	
	@Autowired
	private ServiceExportWinnerInfoToPDF serviceExportWinnerInfoToPDF;

	/** WinningLetter Main Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterMainPage")
	public String winningLetterMainPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("winningLetterMainPage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		return BcsPageEnum.WinningLetterMainPage.toString();
	}

	/** WinningLetter List Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterListPage")
	public String winningLetterListPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("winningLetterListPage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);
		model.addAttribute("winninLetterTracingUrlPre", UriHelper.getWinningLetterTracingUrl());

		return BcsPageEnum.WinningLetterListPage.toString();
	}

	/** WinningLetter Reply List Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterReplyListPage")
	public String winningLetterReplyListPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("winningLetterReplyListPage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);
		
		String pdfExportPath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
		
		model.addAttribute("pdfExportPath", pdfExportPath.replace("/", System.getProperty("file.separator")));

		return BcsPageEnum.WinningLetterReplyListPage.toString();
	}

	/** WinningLetter Signature Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterSignaturePage")
	public String winningLetterSignaturePage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("winningLetterSignaturePage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		return BcsPageEnum.WinningLetterSignaturePage.toString();
	}

	/** Get winning letter list data **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getWinningLetterList")
	@ResponseBody
	public ResponseEntity<?> getWinningLetterList(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam String name, @RequestParam String status,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) Integer size) throws Exception {
		logger.info("getWinningLetterList");

		logger.info("page = {}", page);
		logger.info("size = {}", size);

	    Sort sort = new Sort(Direction.ASC, "id");
	    Pageable pageable = new PageRequest(page, size, sort);

		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		logger.info("name = {}", name);
		logger.info("status = {}", status);
		
		try {
			Page<WinningLetter> page_WinningLetter = null;
			
			if (StringUtils.isBlank(name)) {
//				list_WinningLetter = winningLetterService.findAllByStatusOrderByCreatetimeDesc(status);
				page_WinningLetter = winningLetterService.findAllByStatus(status, pageable);
			}
			else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(status)) {
//				list_WinningLetter = winningLetterService.findAllByNameContainingAndStatusOrderByCreateTimeDesc(name, status);
				page_WinningLetter = winningLetterService.findAllByNameContainingAndStatus(name, status, pageable);
			}
			else {
//				list_WinningLetter = winningLetterService.findAllByStatusOrderByCreatetimeDesc(WinningLetter.STATUS_ACTIVE);
				page_WinningLetter = winningLetterService.findAllByStatus(WinningLetter.STATUS_ACTIVE, pageable);
			}
			
			return new ResponseEntity<>(page_WinningLetter, HttpStatus.OK);
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
	public ResponseEntity<?> getWinningLetter(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam String winningLetterId) throws IOException {
		logger.info("getWinningLetter");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

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
	public ResponseEntity<?> countWinningLetterReplyPeople(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam String winningLetterId) throws IOException {
		logger.info("countWinningLetterReplyPeople");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

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
	public ResponseEntity<?> createWinningLetter(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestBody String winningLetterContent, @CurrentUser CustomUser customUser)
			throws Exception {
		logger.info("createWinningLetter");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

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
	public ResponseEntity<?> editWinningLetter(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestBody String winningLetterContent, @CurrentUser CustomUser customUser)
			throws Exception {

		logger.info("editWinningLetter");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

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
	public ResponseEntity<?> deleteWinningLetter(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam String winningLetterId, @CurrentUser CustomUser customUser)
			throws IOException {
		logger.info("deleteWinningLetter");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

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
	public ResponseEntity<?> activeWinningLetter(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam String winningLetterId, @CurrentUser CustomUser customUser)
			throws IOException {
		logger.info("activeWinningLetter");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

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
	public ResponseEntity<?> inactiveWinningLetter(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam String winningLetterId, @CurrentUser CustomUser customUser)
			throws IOException {
		logger.info("inactiveWinningLetter");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

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
	public void exportToExcelForWinningLetter(HttpServletRequest request, HttpServletResponse response, Model model, 
			@CurrentUser CustomUser customUser, @RequestParam String name, @RequestParam String status)
			throws IOException {
		logger.info("exportToExcelForWinningLetter");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

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
			logger.error("Exception : {}", e);
		}

		LoadFileUIService.loadFileToResponse(filePath, fileName, response);
	}
	
	/** Get winning letter reply list data by winning letter id **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getWinningLetterReplyList")
	@ResponseBody
	public ResponseEntity<?> getWinningLetterReplyList(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam String winningLetterId, @RequestParam String winnerName,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) Integer size) throws Exception {
		logger.info("getWinningLetterReplyList");
		
		logger.info("page = {}", page);
		logger.info("size = {}", size);

	    Sort sort = new Sort(Direction.ASC, "id");
	    Pageable pageable = new PageRequest(page, size, sort);
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);
		
		logger.info("winningLetterId = {}", winningLetterId);
		logger.info("winnerName = {}", winnerName);
		
		try {
			Page<WinningLetterRecord> page_WinningLetterRecords = null;

			if (StringUtils.isNotBlank(winningLetterId) && StringUtils.isNotBlank(winnerName)) {
				page_WinningLetterRecords = winningLetterRecordService.findAllByNameContainingAndWinningLetterId(winnerName, Long.valueOf(winningLetterId), pageable);
			}
			else {
				page_WinningLetterRecords = winningLetterRecordService.findAllByWinningLetterId(Long.valueOf(winningLetterId), pageable);
			}

			return new ResponseEntity<>(page_WinningLetterRecords, HttpStatus.OK);
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
	public void exportToExcelForWinnerReplyList(HttpServletRequest request, HttpServletResponse response, Model model, 
			@CurrentUser CustomUser customUser, @RequestParam String winningLetterId)
			throws IOException {
		logger.info("exportToExcelForWinnerReplyList");
		
		String contextPath = request.getContextPath(); 
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath + "/";
		logger.info("basePath = {}", basePath);
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

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
			
			exportToExcelForWinningLetterService.exportToExcelForWinnerReplyListByWinningLetterId(basePath, filePath, fileName, winningLetterId);

		} catch (Exception e) {
			logger.error("Exception : {}", e);
		}

		LoadFileUIService.loadFileToResponse(filePath, fileName, response);
	}
	
	/** Export winner reply list to pdf **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/exportWinnerInfoToPDF")
	@ResponseBody
	public void exportWinnerInfoToPDF(HttpServletRequest request, HttpServletResponse response, Model model, 
			@CurrentUser CustomUser customUser, @RequestParam String wlrId)
			throws IOException {
		logger.info("exportWinnerInfoToPDF");
		
		logger.info("winningLetterRecordId = {}", wlrId);
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
		logger.info("filePath = {}", filePath);
		
		String fileName = null;
		
		try {
			File folder = new File(filePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			fileName = serviceExportWinnerInfoToPDF.exportWinnerInfoToPDF(filePath, wlrId);
			logger.info("fileName = {}", fileName);
			
		} catch (Exception e) {
			logger.info("Exception : ", e);
		}

		LoadFileUIService.loadFileToResponse(filePath, fileName, response);
	}

	/** Export winner reply list to pdf **/
	@RequestMapping(method = RequestMethod.POST, value = "/edit/exportWinnerInfoListToPDF")
	@ResponseBody
	public ResponseEntity<?> exportWinnerInfoListToPDF(HttpServletRequest request, HttpServletResponse response, Model model, 
			@CurrentUser CustomUser customUser, @RequestBody List<String> list_wlrId)
			throws IOException {
		logger.info("exportWinnerInfoListToPDF");
		
		logger.info("list_winningLetterRecordId = {}", list_wlrId);
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
		logger.info("filePath = {}", filePath);
		
		File tempFolder = Files.createTempDir();
		logger.info("tempFolderName.getAbsolutePath() = {}", tempFolder.getAbsolutePath());
		
		List<String> list_fileName = new ArrayList<String>();

		List<String> zipfiles = new ArrayList<String>();
		
		try {
			if (!tempFolder.exists()) {
				tempFolder.mkdirs();
			}
			
			for (String wlrId : list_wlrId)
			{
				String fileName = serviceExportWinnerInfoToPDF.exportWinnerInfoToPDF(tempFolder.getAbsolutePath(), wlrId);
				
				list_fileName.add(fileName);
				
				String finalSrcZipFile = tempFolder + System.getProperty("file.separator") + fileName;
				logger.info("finalSrcZipFile = {}", finalSrcZipFile);

				zipfiles.add(finalSrcZipFile);
			}
			logger.info("list_fileName = {}", list_fileName);
			
			String outputZipFile = tempFolder.getAbsolutePath();
			logger.info("outputZipFile = {}", outputZipFile);
			
			String zippedFileName = zipFiles(zipfiles, outputZipFile);
			logger.info("zippedFileName = {}", zippedFileName);
			
			if (tempFolder.exists()) {
				logger.info("tempFolder deleted = {}", deleteDirectory(tempFolder));
			}
			
			List<String> list_zipFileName = new ArrayList<String>();
			list_zipFileName.add(zippedFileName);
			
			return new ResponseEntity<>(list_zipFileName, HttpStatus.OK);
			
		} catch (Exception e) {
			logger.info("Exception : ", e);

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/** Download winner reply list to pdf **/
	@RequestMapping(method = RequestMethod.GET, value = "/edit/downloadExportedFileAsZip")
	@ResponseBody
	public void downloadExportedFileAsZip(HttpServletRequest request, HttpServletResponse response, Model model, 
			@CurrentUser CustomUser customUser, @RequestParam String zipFileName)
			throws IOException {
		logger.info("downloadExportedFileAsZip");
		
		logger.info("zipFileName = {}", zipFileName);

		String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
		logger.info("filePath = {}", filePath);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		
		Date date = new Date();
		
		String copyZipfileName = "WinnerReplyList_" + sdf.format(date) + ".zip";
		logger.info("copyZipfileName = {}", copyZipfileName);

		LoadFileUIService.askDownloadFileToResponse(zipFileName, copyZipfileName, response);
	}
	
	public String zipFiles(List<String> files, String outputZipFile) {

		synchronized (this) {
			logger.info("zipFiles --- START");
			
			FileOutputStream fos = null;
			ZipOutputStream zipOut = null;
			FileInputStream fis = null;
			
			String outputFile = outputZipFile + ".zip";
			
			try {
				fos = new FileOutputStream(outputFile);
				zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
				for (String filePath : files) {
					File input = new File(filePath);
					
					fis = new FileInputStream(input);
					
					ZipEntry ze = new ZipEntry(input.getName());
					
					logger.info("Zipping the file : {}", input.getName());
					
					zipOut.putNextEntry(ze);
					
					byte[] tmp = new byte[4 * 1024];
					int size = 0;
					
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					
					zipOut.flush();
					fis.close();
				}
				
				zipOut.close();
			} catch (FileNotFoundException e) {
				logger.info("FileNotFoundException = {}", e);
			} catch (IOException e) {
				logger.info("IOException = {}", e);
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
					
				} catch (Exception ex) {
					logger.info("Exception = {}", ex);
				}

				logger.info("zipFiles --- FINISH");
			}
			
			return outputFile;
		}
	}
	
	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(children[i]);
				if (!success) {
					return false;
				}
			}
		}
		
		return dir.delete();
	}
}
