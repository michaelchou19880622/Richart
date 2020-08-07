package com.bcs.web.ui.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.BcsPageEnum;

@Controller
@RequestMapping("/bcs")
public class BCSTagManagementController extends BCSBaseController {

	/** Logger **/
	private static Logger logger = LogManager.getLogger(BCSTagManagementController.class);

	/** Tag List Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/tagListPage")
	public String tagListPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("tagListPage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		return BcsPageEnum.TagListPage.toString();
	}

	/** Tag Create And Edit Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/tagCreateAndEditPage")
	public String tagCreateAndEditPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("TagCreateAndEditPage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		return BcsPageEnum.TagCreateAndEditPage.toString();
	}

	/** Tag Report Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/tagReportPage")
	public String tagReportPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("tagReportPage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		return BcsPageEnum.TagReportPage.toString();
	}

	/** Tag Report Diagram Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/tagReportDiagramPage")
	public String tagReportDiagramPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("tagReportDiagramPage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		return BcsPageEnum.TagReportDiagramPage.toString();
	}

	/** Tag Report Tagged List Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/tagReportTaggedListPage")
	public String tagReportTaggedListPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("tagReportTaggedListPage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		return BcsPageEnum.TagReportTaggedListPage.toString();
	}

	/** Tag User Tagged Info Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/admin/tagUserTaggedInfoPage")
	public String tagUserTaggedInfoPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		logger.info("tagUserTaggedInfoPage");
		
		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		return BcsPageEnum.TagUserTaggedInfoPage.toString();
	}
}
