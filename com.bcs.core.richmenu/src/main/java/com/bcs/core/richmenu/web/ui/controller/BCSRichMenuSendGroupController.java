package com.bcs.core.richmenu.web.ui.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bcs.core.db.repository.GroupGenerateRepository;
import com.bcs.core.db.service.UserFieldSetService;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.richmenu.core.db.entity.RichMenuGroup;
import com.bcs.core.richmenu.core.db.entity.RichMenuSendGroup;
import com.bcs.core.richmenu.core.db.service.RichMenuGroupGenerateService;
import com.bcs.core.richmenu.core.db.service.RichMenuGroupService;
import com.bcs.core.richmenu.core.db.service.RichMenuSendGroupService;
import com.bcs.core.richmenu.web.ui.service.RichMenuExportExcelUIService;
import com.bcs.core.richmenu.web.ui.service.RichMenuSendGroupUIService;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.ObjectUtil;
import com.bcs.core.web.security.CurrentUser;
import com.bcs.core.web.security.CustomUser;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.BcsPageEnum;
//import com.bcs.web.ui.service.ExportExcelUIService;
//import com.bcs.web.ui.service.SendGroupUIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bcs.core.richmenu.enums.RICH_MENU_DEFAULT_SEND_GROUP;

@Controller
@RequestMapping("/bcs")
public class BCSRichMenuSendGroupController extends BCSBaseController {

	@Autowired
	private RichMenuSendGroupService sendGroupService;
	@Autowired
	private RichMenuSendGroupUIService sendGroupUIService;
	@Autowired
	private UserFieldSetService userFieldSetService;
	@Autowired
	private RichMenuGroupGenerateService groupGenerateService;
	@Autowired
	private RichMenuExportExcelUIService exportExcelUIService;
	@Autowired
	private RichMenuGroupService richMenuGroupService;
	/** Logger */
	private static Logger logger = Logger.getLogger(BCSRichMenuSendGroupController.class);

	// 建立RichMenu發送群組頁面
	@RequestMapping(method = RequestMethod.GET, value = "/market/richMenuSendGroupCreatePage")
	public String richMenuSendGroupCreatePage(HttpServletRequest request, HttpServletResponse response) {
		logger.info("richMenuSendGroupCreatePage");
		return BcsPageEnum.RichMenuSendGroupCreatePage.toString();
	}
	// RichMenu發送群組列表頁面
	@RequestMapping(method = RequestMethod.GET, value = "/market/richMenuSendGroupListPage")
	public String richMenuSendGroupListPage(HttpServletRequest request, HttpServletResponse response) {
		logger.info("richMenuSendGroupListPage");
		return BcsPageEnum.RichMenuSendGroupListPage.toString();
	}

	// 查詢RichMenu發送群組列表
	@RequestMapping(method = RequestMethod.GET, value = "/market/getRichMenuSendGroupList")
	@ResponseBody
	public ResponseEntity<?> getRichMenuSendGroupList(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws IOException {
		logger.info("getRichMenuSendGroupList");		
		//List<RichMenuSendGroup> result = sendGroupService.generateDefaultGroup(
		List<RichMenuSendGroup> result = sendGroupService.findByGroupType(RichMenuSendGroup.GROUP_TYPE_DEFAULT);
		List<RichMenuSendGroup> list = sendGroupService.findByGroupType(RichMenuSendGroup.GROUP_TYPE_NORMAL);
		result.addAll(list);
		logger.info("result:" + ObjectUtil.objectToJsonStr(result));
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	// Get GroupId Title Map
	@RequestMapping(method = RequestMethod.GET, value = "/market/getRichMenuSendGroupTitleList")
	@ResponseBody
	public ResponseEntity<?> getRichMenuSendGroupTitleList(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws IOException {
		logger.info("getRichMenuSendGroupTitleList");		
		Map<Long, String> map = sendGroupService.findGroupTitleMap();
		logger.debug("map:" + ObjectUtil.objectToJsonStr(map));
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	// 取得發送群組
	@RequestMapping(method = RequestMethod.GET, value = "/market/getRichMenuSendGroup")
	@ResponseBody
	public ResponseEntity<?> getRichMenuSendGroup(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser, @RequestParam(required=false) String groupId) throws IOException {
		logger.info("getRichMenuSendGroup");		
		try{
			if(StringUtils.isNotBlank(groupId)){
				logger.info("groupId:" + groupId);
				RichMenuSendGroup sendGroup = sendGroupService.findOne(Long.parseLong(groupId));
				if(sendGroup != null){
					return new ResponseEntity<>(sendGroup, HttpStatus.OK);
				}
			}
			throw new Exception("Group Id Null");
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException)
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 刪除發送群組
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/deleteRichMenuSendGroup")
	@ResponseBody
	public ResponseEntity<?> deleteRichMenuSendGroup(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser, @RequestParam(required=false) String groupId) throws IOException {
		logger.info("deleteRichMenuSendGroup");
		try{
			if(StringUtils.isNotBlank(groupId)){
				logger.info("groupId:" + groupId);
				sendGroupUIService.deleteFromUI(Long.parseLong(groupId), customUser.getAccount());	
				return new ResponseEntity<>("Delete Success", HttpStatus.OK);
			}else{
				throw new Exception("Group Id Null");
			}
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException)
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 新增或修改發送群組
	@RequestMapping(method = RequestMethod.POST, value = "/market/createRichMenuSendGroup", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> createRichMenuSendGroup(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser, @RequestBody RichMenuSendGroup sendGroup) throws IOException {
		logger.info("[createRichMenuSendGroup]");
		try{
			if(sendGroup != null){
				if(StringUtils.isBlank(sendGroup.getGroupTitle())){
					throw new Exception("GroupTitle Null");
				}
				if(StringUtils.isBlank(sendGroup.getGroupDescription())){
					throw new Exception("GroupDescription Null");
				}
				
				//RichMenuGroup richMenuGroup = richMenuGroupService.findOne(richMenuGroupId);
				
				String adminUserAccount = customUser.getAccount();
				RichMenuSendGroup result = sendGroupUIService.saveFromUI(sendGroup, adminUserAccount);
				return new ResponseEntity<>(result, HttpStatus.OK);
			}else
				throw new Exception("SendGroup Null");
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException)
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 取得群組條件各個下拉選項值
	@RequestMapping(method = RequestMethod.GET, value = "/market/getRichMenuSendGroupCondition")
	@ResponseBody
	public ResponseEntity<?> getRichMenuSendGroupCondition(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws IOException {
		logger.info("getRichMenuSendGroupCondition");
		try{
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode sendGroupCondition = objectMapper.createObjectNode();
			List<Object[]> sendGroupQueryList = userFieldSetService.getFieldKeyAndNameAndType();
			
			for (Object[] sendGroupQuery : sendGroupQueryList) {
				
				if(StringUtils.isBlank((String)sendGroupQuery[0])){
					continue;
				}
				if(StringUtils.isBlank((String)sendGroupQuery[1])){
					continue;
				}
				if(StringUtils.isBlank((String)sendGroupQuery[2])){
					continue;
				}

				ObjectNode sendGroupQueryProperty = (new ObjectMapper()).createObjectNode();
				String queryFieldId = (String) sendGroupQuery[0];
				String queryFieldName = (String) sendGroupQuery[1];
				String queryFieldFormat = (String) sendGroupQuery[2];
				sendGroupQueryProperty
					.putPOJO("queryFieldOp", 
							(ArrayNode) objectMapper.valueToTree(GroupGenerateRepository.validQueryOp));
				sendGroupQueryProperty.put("queryFieldId", queryFieldId);
				sendGroupQueryProperty.put("queryFieldName", queryFieldName);
				sendGroupQueryProperty.put("queryFieldFormat", queryFieldFormat);
				if("Date".equals(queryFieldFormat)){
					sendGroupQueryProperty.put("queryFieldSet", "DatePicker");
				}
				else{
					sendGroupQueryProperty.put("queryFieldSet", "Input");
				}
				sendGroupCondition.putPOJO(queryFieldId, sendGroupQueryProperty);
			}
			
			return new ResponseEntity<>(sendGroupCondition, HttpStatus.OK);
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException)
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 取得條件結果
	@RequestMapping(method = RequestMethod.POST, value = "/market/getRichMenuSendGroupConditionResult", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getRichMenuSendGroupConditionResult(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser, @RequestBody RichMenuSendGroup sendGroup,
			@RequestParam(required=false) String startDate, @RequestParam(required=false) String endDate) throws IOException {
		logger.info("getRichMenuSendGroupConditionResult");
		try{
			Long groupId = sendGroup.getGroupId() ;
			if(groupId == null){
				BigInteger result = groupGenerateService.findMIDCountBySendGroupDetail(sendGroup.getSendGroupDetail());
				
				logger.info("getRichMenuSendGroupConditionResult Success");
				return new ResponseEntity<>(result, HttpStatus.OK);
			}else{
				Long result= 0L;
				if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date time = sdf.parse(endDate);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(time);
					calendar.add(Calendar.DATE, 1);

					endDate = sdf.format(calendar.getTime());
					logger.info("startDate:" + startDate);
					logger.info("endDate:" + endDate);
					
					result= sendGroupService.countDefaultGroupSize(groupId, startDate, endDate);
				}else{
					result= sendGroupService.countDefaultGroupSize(groupId);
				}
				
				if(result != null){
					logger.info("getSendGroupConditionResult Success");
					return new ResponseEntity<>(result, HttpStatus.OK);
				}else
					throw new Exception("SendGroup Send Error");
			}
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException)
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	// 取得條件結果
	@RequestMapping(method = RequestMethod.GET, value = "/market/getRichMenuSendGroupQueryResult")
	@ResponseBody
	public ResponseEntity<?> getRichMenuSendGroupQueryResult(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser, @RequestParam Long groupId) throws IOException {
		logger.info("getRichMenuSendGroupQueryResult");
		try {
			RichMenuSendGroup sendGroup = sendGroupService.findOne(groupId);
			if(sendGroup == null){
				throw new Exception("SendGroup Error");
			}

			// 行銷人員設定 群組
			if(groupId > 0){
				try{
					List<String> mids = groupGenerateService.findMIDBySendGroupDetailGroupId(groupId);
					if(mids != null && mids.size() >0){
	
						return new ResponseEntity<>(mids.size(), HttpStatus.OK);
					}
				}
				catch(Exception e){
					logger.error(ErrorRecord.recordError(e));
					throw new Exception("SendGroup Send Error");
				}
			}
			// 預設群祖
			else if(groupId < 0){
				Long result= sendGroupService.countDefaultGroupSize(groupId);
				if(result != null){
					return new ResponseEntity<>(result, HttpStatus.OK);
				}
				else{
					throw new Exception("SendGroup Send Error");
				}
			}
			return new ResponseEntity<>(0, HttpStatus.OK);
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException)
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// uploadMidSendGroup
	@RequestMapping(method = RequestMethod.POST, value = "/market/uploadMidRichMenuSendGroup")
	@ResponseBody
	public ResponseEntity<?> uploadMidRichMenuSendGroup(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser, @RequestPart MultipartFile filePart,@RequestParam String event) throws IOException {
		logger.info("uploadMidRichMenuSendGroup");
		try{	    
			if(filePart != null){
				String modifyUser = customUser.getAccount();
				logger.info("modifyUser:" + modifyUser);
				Map<String, Object> result = sendGroupUIService.uploadMidSendGroup(event ,filePart, modifyUser, new Date());
				return new ResponseEntity<>(result, HttpStatus.OK);
			}else{
				throw new Exception("Upload Mid SendGroup Null");
			}
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException)
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Map<String, RichMenuSendGroup> tempSendGroupMap = new HashMap<String, RichMenuSendGroup>();


	// createSendGroupMidExcelTemp
	@RequestMapping(method = RequestMethod.POST, value = "/market/createRichMenuSendGroupMidExcelTemp", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> createRichMenuSendGroupMidExcelTemp(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser, @RequestBody RichMenuSendGroup sendGroup) throws IOException {
		logger.info("createRichMenuSendGroupMidExcelTemp");
		try{
			Long groupId = sendGroup.getGroupId() ;
			if(groupId == null){
				BigInteger count = groupGenerateService.findMIDCountBySendGroupDetail(sendGroup.getSendGroupDetail());
				
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("count", count);
				
				String tempId = UUID.randomUUID().toString().toLowerCase();
				tempSendGroupMap.put(tempId, sendGroup);
				result.put("tempId", tempId);
				
				logger.info("createSendGroupMidExcelTemp Success");
				return new ResponseEntity<>(result, HttpStatus.OK);
			}else{
				Long count = 0L;
				count= sendGroupService.countDefaultGroupSize(groupId);
				if(count != null){
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("count", count);
					
					logger.info("createSendGroupMidExcelTemp Success");
					
					tempSendGroupMap.put(groupId + "", sendGroup);
					result.put("tempId", groupId + "");
					
					return new ResponseEntity<>(result, HttpStatus.OK);
				}else{
					throw new Exception("SendGroup Send Error");
				}
			}
		}catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException)
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			else
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// exportToExcelForSendGroup
	@RequestMapping(method = RequestMethod.GET, value = "/market/exportToExcelForRichMenuSendGroup")
	@ResponseBody
	public void exportToExcelForRichMenuSendGroup(HttpServletRequest request, HttpServletResponse response,
			@CurrentUser CustomUser customUser, @RequestParam String tempId) throws Exception {
		logger.info("exportToExcelForSendGroup");
		RichMenuSendGroup sendGroup = tempSendGroupMap.get(tempId);

		if(sendGroup == null){

			throw new Exception("SendGroup Error");
		}
		
		Long groupId = sendGroup.getGroupId();

		// 行銷人員設定 群組
		if(groupId == null){
			try{
				List<String> mids = groupGenerateService.findMIDBySendGroupDetail(sendGroup.getSendGroupDetail());
				logger.info("tempId1:"+tempId);
				logger.info("mids1:"+mids.toString());
				
				if(mids != null && mids.size() >0){

					List<String> titles = new ArrayList<String>();
					titles.add("MID");
					List<List<String>> data = new ArrayList<List<String>>();
					data.add(mids);
					
					String title = "SendGroup";
					if(StringUtils.isNotBlank(sendGroup.getGroupTitle())){
						title += ":" + sendGroup.getGroupTitle();
					}
					
					exportExcelUIService.exportMidResultToExcel(request, response, "SendGroup", title , null, titles, data);
				}
			}
			catch(Exception e){
				logger.error(ErrorRecord.recordError(e));
				throw new Exception("SendGroup Send Error");
			}
		}
		// 預設群祖
		else if(groupId < 0){
			List<String> mids = new ArrayList<String>();
			
			int page = 0;
			while(true){
				List<String> list = sendGroupService.queryDefaultGroup(groupId, page);
				if(list != null && list.size() > 0){
					mids.addAll(list);
					logger.debug("queryDefaultGroup:" + list.size());
				}
				else{
					break;
				}
				page++;
			}
			
			if(mids != null && mids.size() >0){
				List<String> titles = new ArrayList<String>();
				titles.add("MID");
				List<List<String>> data = new ArrayList<List<String>>();
				data.add(mids);
				exportExcelUIService.exportMidResultToExcel(request, response, "SendGroup", 
						RICH_MENU_DEFAULT_SEND_GROUP.getGroupByGroupId(groupId).getTitle(), null, titles, data);
			}
		}
	}
}
