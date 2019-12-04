package com.bcs.web.ui.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.richart.api.model.WinningLetterModel;
import com.bcs.core.richart.service.WinningLetterService;
import com.bcs.core.utils.ObjectUtil;
import com.bcs.core.web.security.CurrentUser;
import com.bcs.core.web.security.CustomUser;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.BcsPageEnum;

@Controller
@RequestMapping("/bcs")
public class BCSWinningLetterController extends BCSBaseController {

	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(BCSWinningLetterController.class);
	
	@Autowired
	private WinningLetterService winningLetterService;

	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterPage")
	public String winningLetterPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterPage");

		return BcsPageEnum.WinningLetterPage.toString();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/createWinningLetter", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> createWinningLetter(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String winningLetterContent, @CurrentUser CustomUser customUser) throws Exception {
		
		logger.info("createWinningLetter");
		
		String currentUser = customUser.getAccount();
		logger.info("customUser = {}", currentUser);
		
		logger.info("RequestBody : winningLetterContent = {}", winningLetterContent);
		
		WinningLetterModel winningLetterModel = ObjectUtil.jsonStrToObject(winningLetterContent, WinningLetterModel.class);
		logger.info("winningLetterModel.toString() = {}", winningLetterModel.toString());
		
		// 檢查是否中獎名稱已經存在?
		WinningLetter winningLetter = winningLetterService.findByName(winningLetterModel.getName());
		logger.info("winningLetter = {}", winningLetter);
		
		Date currentDateTime = new Date();
		logger.info("currentDateTime = {}", currentDateTime);
		
		if (winningLetter != null) {
			return new ResponseEntity<>("中獎回函名稱已重複，請重新輸入。", HttpStatus.BAD_REQUEST);
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

		return new ResponseEntity<>(String.format("The winningletter (id : %d) is created", winningLetterId), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/editWinningLetter", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> editWinningLetter(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String winningLetterContent, @CurrentUser CustomUser customUser) throws Exception {
		
		logger.info("editWinningLetter");
		
		String currentUser = customUser.getAccount();
		logger.info("customUser = {}", currentUser);
		
		logger.info("RequestBody : winningLetterContent = {}", winningLetterContent);

		WinningLetterModel winningLetterModel = ObjectUtil.jsonStrToObject(winningLetterContent, WinningLetterModel.class);
		logger.info("winningLetterModel = {}", winningLetterModel.toString());
		
		// 檢查是否中獎名稱已經存在?
		WinningLetter winningLetter = winningLetterService.findByName(winningLetterModel.getName());
		logger.info("winningLetter = {}", winningLetter);
		
		Date currentDateTime = new Date();
		logger.info("currentDateTime = {}", currentDateTime);
		
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

		return new ResponseEntity<>(String.format("The winningletter (id : %d) is updated", winningLetterId), HttpStatus.OK);
	}
}
