package com.bcs.web.m.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.richart.service.WinningLetterService;
import com.bcs.core.web.ui.page.enums.MobilePageEnum;
 
@Controller
@RequestMapping("/m")
public class MobileWinningLetterReplyController {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(MobileWinningLetterReplyController.class);
	
	@Autowired
	private WinningLetterService winningLetterService;

	/** WinningLetter Reply Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/wl/winningLetterReplyPage")
	public String winningLetterReplyPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterReplyPage");

		return MobilePageEnum.WinningLetterReplyPage.toString();
	}


	/** Get winning letter data **/
	@RequestMapping(method = RequestMethod.GET, value = "/wl/getWinningLetter")
	@ResponseBody
	public ResponseEntity<?> getWinningLetter(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam String winningLetterId) throws IOException {
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

	@RequestMapping(method = RequestMethod.GET, value = "wl/{winningLetterId}")
	public RedirectView winningLetterUrlLinkWithId(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String winningLetterId) throws Exception {
		logger.info("winningLetterUrlLinkWithId");
		logger.info("winningLetterId = {}", winningLetterId);
		
		String liffAppId = CoreConfigReader.getString("winningLetter.LiffAppId");
		logger.info("liffAppId = {}", liffAppId);
		
		String liffUrl = liffAppId.replace("{winningLetterId}", winningLetterId);
		logger.info("liffUrl = {}", liffUrl);
		
		model.addAttribute("liffUrl", liffUrl);
		
		RedirectView redirectTarget = new RedirectView();
        redirectTarget.setContextRelative(true);
        redirectTarget.setUrl(liffUrl);
        
        return redirectTarget;
	}
}
