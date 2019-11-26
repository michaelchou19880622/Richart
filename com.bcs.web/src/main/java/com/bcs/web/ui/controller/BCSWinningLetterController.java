package com.bcs.web.ui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.BcsPageEnum;


@Controller
@RequestMapping("/bcs")
public class BCSWinningLetterController extends BCSBaseController {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(BCSWinningLetterController.class);

	@RequestMapping(method = RequestMethod.GET, value = "/admin/winningLetterCreatePage")
	public String winningLetterCreatePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterCreatePage");

		return BcsPageEnum.WinningLetterCreatePage.toString();
	}
}
