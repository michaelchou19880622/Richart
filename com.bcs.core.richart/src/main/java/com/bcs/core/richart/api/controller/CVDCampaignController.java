package com.bcs.core.richart.api.controller;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;

import com.bcs.core.db.entity.CvdCampaignFlow;
import com.bcs.core.db.service.CvdCampaignFlowService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.CryptUtil;
import com.bcs.core.utils.RestfulUtil;

@Controller
@RequestMapping("/api")
public class CVDCampaignController {

    /** Logger */
    private static Logger logger = LogManager.getLogger(CVDCampaignController.class);
	
	@Autowired
	private CvdCampaignFlowService springTreeCampaignFlowService;
	
	@RequestMapping(method = RequestMethod.POST, value = "/cvd/pushMessage", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> cvdPushMessage(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBodyString) {
		try {
			if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
				return new ResponseEntity<>("{\"result\": \"Missing header 'Authorization'\"}", HttpStatus.BAD_REQUEST);
			}

			String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
			logger.info("authorization : {}", authorization);

			if (StringUtils.isBlank(authorization)) {
				return new ResponseEntity<>("{\"result\": \"Parameter 'authorization' is required\"}", HttpStatus.BAD_REQUEST);
			} else if (authorization.split("key=").length != 2) {
				return new ResponseEntity<>("{\"result\": \"Invalid Authorization format\"}", HttpStatus.BAD_REQUEST);
			}

			String token = authorization.split("key=")[1];
			logger.info("authorization key : {}", token);

			String secret = CoreConfigReader.getString(CONFIG_STR.AES_SECRET_KEY, true);
			logger.info("secret : {}", secret);

			String iv = CoreConfigReader.getString(CONFIG_STR.AES_INITIALIZATION_VECTOR, true);
			logger.info("iv : {}", iv);

			String originalToken = CoreConfigReader.getString(CONFIG_STR.API_CVDCAMPAIGN_TOKEN, true);
			logger.info("originalToken : {}", originalToken);

			String decryptedToken = CryptUtil.Decrypt(CryptUtil.AES, token, secret, iv);
			logger.info("decryptedToken : {}", decryptedToken);

//			String encryptedToken2 = CryptUtil.Encrypt(CryptUtil.AES, "CVDCampaignToken", secret, iv);
//			logger.info("encryptedToken2 : CVDCampaignToken >> encryptedToken = {}", encryptedToken2);

			if (!decryptedToken.equals(originalToken)) {
				return new ResponseEntity<>("{\"result\": \"Invalid Authorization\"}", HttpStatus.BAD_REQUEST);
			}

			if (StringUtils.isBlank(requestBodyString)) {
				return new ResponseEntity<>("{\"result\": \"Request body is required\"}", HttpStatus.BAD_REQUEST);
			}

			JSONObject requestBody = new JSONObject(requestBodyString);
			logger.info("requestBody : {}", requestBody);

			String url = CoreConfigReader.getString(CONFIG_STR.LINE_MESSAGE_PUSH_URL.toString());
			logger.info("url : {}", url);

			String accessToken = CoreConfigReader.getString(CONFIG_STR.Default.toString(), CONFIG_STR.ChannelToken.toString(), true);
			logger.info("accessToken : {}", accessToken);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			logger.info("headers : {}", headers);

			HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody.toString(), headers);

			RestfulUtil restfulUtil = new RestfulUtil(HttpMethod.POST, url, httpEntity);

			JSONObject jsonObjectResult = restfulUtil.execute();
			logger.info("RestfulUtil execute result = {}", jsonObjectResult);

			return new ResponseEntity<>("{\"result\": \"Success\"}", HttpStatus.OK);
		} catch (Exception e) {
			logger.info("Exception = {}", e);

			if (e instanceof BadPaddingException || e instanceof IllegalBlockSizeException || e instanceof IllegalArgumentException) {
				return new ResponseEntity<>("{\"result\": \"Invalid Authorization\"}", HttpStatus.BAD_REQUEST);
			} else if (e instanceof HttpClientErrorException) {

				String responseMessage = ((HttpClientErrorException) e).getResponseBodyAsString();
				logger.info("responseMessage = {}", responseMessage);

				if (responseMessage.contains("{\"message\"")) {
					JSONObject responseMessageObject = new JSONObject(responseMessage);
					String message = responseMessageObject.getString("message");

					return new ResponseEntity<>("{\"result\": \"" + message + "\"}", HttpStatus.BAD_REQUEST);
				} else {
					return new ResponseEntity<>("{\"result\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
				}
			} else if (e instanceof JSONException) {
				return new ResponseEntity<>("{\"result\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
			} else {
				String errorMsg = e.getMessage();
				return new ResponseEntity<>("{\"result\": \"" + errorMsg + "\"}", HttpStatus.BAD_REQUEST);
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/cvd/replyMessage", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> cvdReplyMessage(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBodyString) {
		try {
			if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
				return new ResponseEntity<>("{\"result\": \"Missing header 'Authorization'\"}", HttpStatus.BAD_REQUEST);
			}

			String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
			logger.info("authorization : {}", authorization);

			if (StringUtils.isBlank(authorization)) {
				return new ResponseEntity<>("{\"result\": \"Parameter 'authorization' is required\"}", HttpStatus.BAD_REQUEST);
			} else if (authorization.split("key=").length != 2) {
				return new ResponseEntity<>("{\"result\": \"Invalid Authorization format\"}", HttpStatus.BAD_REQUEST);
			}

			String token = authorization.split("key=")[1];
			logger.info("authorization key : {}", token);

			String secret = CoreConfigReader.getString(CONFIG_STR.AES_SECRET_KEY, true);
			logger.info("secret : {}", secret);

			String iv = CoreConfigReader.getString(CONFIG_STR.AES_INITIALIZATION_VECTOR, true);
			logger.info("iv : {}", iv);

			String originalToken = CoreConfigReader.getString(CONFIG_STR.API_CVDCAMPAIGN_TOKEN, true);
			logger.info("originalToken : {}", originalToken);

			String decryptedToken = CryptUtil.Decrypt(CryptUtil.AES, token, secret, iv);
			logger.info("decryptedToken : {}", decryptedToken);

			if (!decryptedToken.equals(originalToken)) {
				return new ResponseEntity<>("{\"result\": \"Invalid Authorization\"}", HttpStatus.BAD_REQUEST);
			}

			if (StringUtils.isBlank(requestBodyString)) {
				return new ResponseEntity<>("{\"result\": \"Request body is required\"}", HttpStatus.BAD_REQUEST);
			}
			
			JSONObject requestBody = new JSONObject(requestBodyString);
			logger.info("requestBody : {}", requestBody);

			String replyToken = requestBody.getString("replyToken");
			logger.info("replyToken = {}", replyToken);

			if (StringUtils.isBlank(replyToken)) {
				return new ResponseEntity<>("{\"result\": \"Parameter 'replyToken' is required\"}", HttpStatus.BAD_REQUEST);
			}

			String url = CoreConfigReader.getString(CONFIG_STR.LINE_MESSAGE_REPLY_URL.toString());
			logger.info("url : {}", url);

			String accessToken = CoreConfigReader.getString(CONFIG_STR.Default.toString(), CONFIG_STR.ChannelToken.toString(), true);
			logger.info("accessToken : {}", accessToken);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			logger.info("headers : {}", headers);

			HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody.toString(), headers);

			RestfulUtil restfulUtil = new RestfulUtil(HttpMethod.POST, url, httpEntity);

			JSONObject jsonObjectResult = restfulUtil.execute();
			logger.info("RestfulUtil execute result = {}", jsonObjectResult);

			return new ResponseEntity<>("{\"result\": \"Success\"}", HttpStatus.OK);
		} catch (Exception e) {
			logger.info("Exception = {}", e);

			if (e instanceof BadPaddingException || e instanceof IllegalBlockSizeException || e instanceof IllegalArgumentException) {
				return new ResponseEntity<>("{\"result\": \"Invalid Authorization\"}", HttpStatus.BAD_REQUEST);
			} else if (e instanceof HttpClientErrorException) {

				String responseMessage = ((HttpClientErrorException) e).getResponseBodyAsString();
				logger.info("responseMessage = {}", responseMessage);

				if (responseMessage.contains("{\"message\"")) {
					JSONObject responseMessageObject = new JSONObject(responseMessage);
					String message = responseMessageObject.getString("message");

					return new ResponseEntity<>("{\"result\": \"" + message + "\"}", HttpStatus.BAD_REQUEST);
				} else {
					return new ResponseEntity<>("{\"result\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
				}
			} else if (e instanceof JSONException) {
				return new ResponseEntity<>("{\"result\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
			} else {
				String errorMsg = e.getMessage();
				return new ResponseEntity<>("{\"result\": \"" + errorMsg + "\"}", HttpStatus.BAD_REQUEST);
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/cvd/startCampaign", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> cvdStartCampaign(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBodyString) {
		try {
			if (StringUtils.isBlank(requestBodyString)) {
				return new ResponseEntity<>("{\"result\": \"Request body is required\"}", HttpStatus.BAD_REQUEST);
			}
			
			JSONObject requestBody = new JSONObject(requestBodyString);
			logger.info("requestBody : {}", requestBody);

			String uid = requestBody.getString("uid");
			logger.info("uid = {}", uid);

			if (StringUtils.isBlank(uid)) {
				return new ResponseEntity<>("{\"result\": \"Parameter 'uid' is required\"}", HttpStatus.BAD_REQUEST);
			}

			CvdCampaignFlow springTreeCampaignFlow = springTreeCampaignFlowService.findByUid(uid);

			if (springTreeCampaignFlow == null) {
				springTreeCampaignFlow = new CvdCampaignFlow();
				springTreeCampaignFlow.setUid(uid);
			}

			springTreeCampaignFlow.setStatus(CvdCampaignFlow.STATUS_INPROGRESS);
			springTreeCampaignFlow = springTreeCampaignFlowService.save(springTreeCampaignFlow);

			return new ResponseEntity<>("{\"result\": \"success\"}", HttpStatus.OK);
		} catch (Exception e) {
			logger.info("Exception = {}", e);
			return new ResponseEntity<>("{\"result\": \"fail\"}", HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/cvd/finishCampaign", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> cvdFinishCampaign(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBodyString) {
		try {
			if (StringUtils.isBlank(requestBodyString)) {
				return new ResponseEntity<>("{\"result\": \"Request body is required\"}", HttpStatus.BAD_REQUEST);
			}
			
			JSONObject requestBody = new JSONObject(requestBodyString);
			logger.info("requestBody : {}", requestBody);

			String uid = requestBody.getString("uid");
			logger.info("uid = {}", uid);

			if (StringUtils.isBlank(uid)) {
				return new ResponseEntity<>("{\"result\": \"Parameter 'uid' is required\"}", HttpStatus.BAD_REQUEST);
			}

			CvdCampaignFlow springTreeCampaignFlow = springTreeCampaignFlowService.findByUid(uid);

			if (springTreeCampaignFlow == null) {
				logger.info("Can not find the user record.");
				return new ResponseEntity<>("{\"result\": \"fail\"}", HttpStatus.BAD_REQUEST);
			}

			springTreeCampaignFlow.setStatus(CvdCampaignFlow.STATUS_FINISHED);
			springTreeCampaignFlow = springTreeCampaignFlowService.save(springTreeCampaignFlow);

			return new ResponseEntity<>("{\"result\": \"success\"}", HttpStatus.OK);
		} catch (Exception e) {
			logger.info("Exception = {}", e);
			return new ResponseEntity<>("{\"result\": \"fail\"}", HttpStatus.BAD_REQUEST);
		}
	}
}