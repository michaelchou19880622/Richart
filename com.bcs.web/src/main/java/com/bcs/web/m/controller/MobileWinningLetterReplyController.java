package com.bcs.web.m.controller;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.resource.UriHelper;
import com.bcs.core.web.ui.page.enums.MobilePageEnum;

@Controller
@RequestMapping("/m")
public class MobileWinningLetterReplyController {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(MobileWinningLetterReplyController.class);

	/** WinningLetter Reply Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/wl/winningLetterReplyPage")
	public String winningLetterReplyPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterReplyPage");

		return MobilePageEnum.WinningLetterReplyPage.toString();
	}

	@RequestMapping(method = RequestMethod.GET, value = "wl/{winningLetterId}")
	public RedirectView winningLetterUrlLinkWithId(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String winningLetterId) throws Exception {
		logger.info("winningLetterUrlLinkWithId");
		logger.info("winningLetterId = {}", winningLetterId);
		
		String liffAppId = CoreConfigReader.getString("winningLetter.LiffAppId");
		logger.info("liffAppId = {}", liffAppId);
		
		String lineoauthLink = liffAppId.replace("{winningLetterId}", winningLetterId);
		logger.info("lineoauthLink = {}", lineoauthLink);
		
		model.addAttribute("lineoauthLink", lineoauthLink);
		
		RedirectView redirectTarget = new RedirectView();
        redirectTarget.setContextRelative(true);
        redirectTarget.setUrl(lineoauthLink);
        
        return redirectTarget;
	}
}
