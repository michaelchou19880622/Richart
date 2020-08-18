package com.bcs.web.ui.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bcs.core.db.entity.Tag;
import com.bcs.core.richart.service.TagService;
import com.bcs.core.web.security.CurrentUser;
import com.bcs.core.web.security.CustomUser;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.BcsPageEnum;
import com.bcs.web.ui.model.ResponseModel;
import com.bcs.web.ui.model.TagModel;

@Controller
@RequestMapping("/bcs")
public class BCSTagManagementController extends BCSBaseController {

	/** Logger **/
	private static Logger logger = LogManager.getLogger(BCSTagManagementController.class);
	
	@Autowired
	private TagService tagService;

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

	/** Create tag **/
	@RequestMapping(method = RequestMethod.POST, value = "/edit/tag")
	@ResponseBody
	public ResponseEntity<?> createTag(HttpServletRequest request, HttpServletResponse response, Model model,
			@CurrentUser CustomUser customUser, 
			@RequestBody TagModel tagModel) throws Exception {
		logger.info("createTag - tagModel = {}", tagModel);
		
		if (tagModel == null) {
            return new ResponseEntity<>(new ResponseModel("The request body is null."), HttpStatus.UNPROCESSABLE_ENTITY);
		} else if (StringUtils.isBlank(tagModel.getTagName())) {
            return new ResponseEntity<>(new ResponseModel("The parameter tagName parse error"), HttpStatus.UNPROCESSABLE_ENTITY);
		} 
		
		// Check is the tag name already exist?
		Tag tag = tagService.findByTagName(tagModel.getTagName());
		logger.info("tag = {}", tag);

		if (tag != null) {
			return new ResponseEntity<>(new ResponseModel("標籤名稱重複，請重新輸入。"), HttpStatus.BAD_REQUEST);
		}
		
		Date currentDateTime = new Date();
		
		tag = new Tag();
		tag.setTagName(tagModel.getTagName());
		tag.setTagDescription(tagModel.getTagDescription());
		tag.setCreateTime(currentDateTime);
		tag.setCreateUser(customUser.getAccount());
		tag.setModifyTime(currentDateTime);
		tag.setModifyUser(customUser.getAccount());
		tag.setValidDay(tagModel.getValidDay());
		logger.info("new Tag = {}", tag);
		
		Long id = tagService.save(tag);
		logger.info("id = {}", id);
		
		return new ResponseEntity<>(new ResponseModel("The tag was created successfully."), HttpStatus.CREATED);
	}

	/** Edit tag **/
	@RequestMapping(method = RequestMethod.POST, value = "/edit/tag/{tagId}")
	@ResponseBody
	public ResponseEntity<?> editTag(HttpServletRequest request, HttpServletResponse response, Model model,
			@CurrentUser CustomUser customUser, 
			@PathVariable Long tagId, @RequestBody TagModel tagModel) throws Exception {
		logger.info("editTag - tagId = {}", tagId);
		logger.info("editTag - tagModel = {}", tagModel);
		
		if (tagModel == null) {
            return new ResponseEntity<>(new ResponseModel("The request body is null."), HttpStatus.UNPROCESSABLE_ENTITY);
		} else if (StringUtils.isBlank(tagModel.getTagName())) {
            return new ResponseEntity<>(new ResponseModel("The parameter tagName parse error"), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		// Check is the tag name already exist?
		Tag tag = tagService.findById(tagId);
		logger.info("tag = {}", tag);

		if (tag == null) {
			return new ResponseEntity<>(new ResponseModel(String.format("ID: %d, 查無此標籤。", tagId)), HttpStatus.BAD_REQUEST);
		}
		
		Date currentDateTime = new Date();
		
		tag.setTagName(tagModel.getTagName());
		tag.setTagDescription(tagModel.getTagDescription());
		tag.setModifyTime(currentDateTime);
		tag.setModifyUser(customUser.getAccount());
		tag.setValidDay(tagModel.getValidDay());
		logger.info("new Tag = {}", tag);
		
		Long id = tagService.save(tag);
		logger.info("id = {}", id);

		return new ResponseEntity<>(new ResponseModel("The tag was updated successfully."), HttpStatus.OK);
	}
	


}
