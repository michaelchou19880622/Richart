package com.bcs.core.richart.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.bcs.core.db.entity.LineUser;
import com.bcs.core.enums.LOG_TARGET_ACTION_TYPE;
import com.bcs.core.log.util.SystemLogUtil;
import com.bcs.core.richart.api.model.UpdateStatusModel;
import com.bcs.core.richart.service.RichartValidateService;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.LineIdUtil;
import com.bcs.core.utils.ObjectUtil;


@Controller
@RequestMapping("/api")
public class UserStatusUpdateController {
	@Autowired
	private RichartValidateService richartValidateService; 
	
	/** Logger */
	private static Logger logger = Logger.getLogger(UserStatusUpdateController.class);

	@RequestMapping(method = RequestMethod.POST, value = "/userStatusUpdate/{ChannelId}", 
			consumes = MediaType.APPLICATION_JSON_VALUE + "; charset=UTF-8")
	public ResponseEntity<?> userStatusUpdate(@RequestBody String updateModel, @PathVariable String ChannelId, HttpServletRequest request, HttpServletResponse response) {
		logger.info("-------userStatusUpdate-------");
		Date start = new Date();
		logger.info("updateModel:" + updateModel);
		
		String error = "";
		
		try{
			
			UpdateStatusModel model = ObjectUtil.jsonStrToObject(updateModel, UpdateStatusModel.class);
			logger.info("-------userStatusUpdate model-------:" + model);
			
			if(LineIdUtil.isLineUID(model.getUid())){
				// Validate
			}
			else{
				throw new Exception("UidError");
			}
			
			if(LineUser.STATUS_BINDED.equals(model.getStatus()) || LineUser.STATUS_UNBIND.equals(model.getStatus())){
				// Validate
			}
			else{
				throw new Exception("StatusError");
			}
			
			richartValidateService.bindedLineUser(model);

			logger.info("-------userStatusUpdate Success-------");
			response.setStatus(200);
			SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_BcsApi, LOG_TARGET_ACTION_TYPE.ACTION_BcsApi_UpdateStatus, start, 200, updateModel, "200");
			return new ResponseEntity<>(createResult(200, "Success"), HttpStatus.OK);
		}
		catch(Throwable e){
			error = e.getMessage();
			logger.info(ErrorRecord.recordError(e));
		}
		logger.info("-------userStatusUpdate Fail-------");
		response.setStatus(500);
		SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_BcsApi, LOG_TARGET_ACTION_TYPE.ACTION_BcsApi_UpdateStatus, start, 500, updateModel, "500");
		return new ResponseEntity<>(createResult(500, error), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private Map<String, Object> createResult(Integer status, String msg){
		Map<String, Object> result = new HashMap<String, Object>();
		
		result.put("status", status);
		result.put("msg", msg);
		
		return result;
	}
}
