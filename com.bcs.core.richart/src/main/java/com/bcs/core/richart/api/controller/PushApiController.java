package com.bcs.core.richart.api.controller;

import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bcs.core.api.service.model.PushApiModel;
import com.bcs.core.bot.akka.service.PNPService;
import com.bcs.core.db.entity.PushMessageRecord;
import com.bcs.core.db.service.PushMessageRecordService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.record.akke.handler.PushMessageRecordActor;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.richart.api.validator.PushApiRequestValidator;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.AkkaRouterFactory;
import com.bcs.core.utils.CryptUtil;

import akka.actor.ActorRef;

@Controller
@RequestMapping("/api")
public class PushApiController {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(PushApiController.class);

	@Autowired
	private PNPService PNPService;
	
	private PushApiModel pushApiModel;
	
	@RequestMapping(method = RequestMethod.POST, value = "/message/push", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> pushMessage(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBodyString) {
		try {
			logger.info("-------------------- pushMessage --------------------");

			logger.info("[pushMessage] Request body: {}", requestBodyString);

			pushApiModel = new PushApiModel();

			PushApiRequestValidator.validate(requestBodyString, pushApiModel);

			logger.info("[pushMessage] pushApiModel = {}", pushApiModel);

			if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
				return new ResponseEntity<>("{\"result\": 0, \"msg\": \"Missing 'Authorization' header.\"}", HttpStatus.BAD_REQUEST);
			} else {
				String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
				logger.info("[pushMessage] authorization = {}", authorization);

				if (authorization.split("key=").length != 2) {
					return new ResponseEntity<>("{\"error\": \"true\", \"message\": \"Invalid 'Authorization' format.\"}", HttpStatus.UNAUTHORIZED);
				}

				String token = authorization.split("key=")[1];
				logger.info("[pushMessage] token = {}", token);

				String secret = CoreConfigReader.getString(CONFIG_STR.AES_SECRET_KEY, true);
				logger.info("[pushMessage] secret = {}", secret);

				String iv = CoreConfigReader.getString(CONFIG_STR.AES_INITIALIZATION_VECTOR, true);
				logger.info("[pushMessage] iv = {}", iv);

				String originalToken = CoreConfigReader.getString(CONFIG_STR.API_ORIGINAL_TOKEN, true);
				logger.info("[pushMessage] originalToken = {}", originalToken);
				
				String descryptedToken = CryptUtil.Decrypt(CryptUtil.AES, token, secret, iv);
				logger.info("[pushMessage] descryptedToken = {}", descryptedToken);
				
				String encryptedToken = CryptUtil.Encrypt(CryptUtil.AES, "ThisIsAPushMessageApi", secret, iv);
				logger.info("[pushMessage] DEBUG CHECK : ThisIsAPushMessageApi >> encryptedToken = {}", encryptedToken);

				if (!descryptedToken.equals(originalToken)) {
					return new ResponseEntity<>("{\"result\": 0, \"msg\": \"Invalid token.\"}", HttpStatus.UNAUTHORIZED);
				}
			}

			PNPService.tell(pushApiModel);

			return new ResponseEntity<>("{\"result\": 1, \"msg\": \"Success.\"}", HttpStatus.OK);
		} catch (Exception e) {
			JSONArray uids = pushApiModel.getUid();
			logger.info("uids = {}", uids);
			logger.info("uids.length() = {}", uids.length());
			
			for (Integer i = 0; i < uids.length(); i++) {
				String sendMessage = pushApiModel.getMessages().toString();
				logger.info("sendMessage = {}", sendMessage);
				logger.info("sendMessage.length() = {}", sendMessage.length());
				
				if (sendMessage.length() >= 200) {  // 因為DB欄位設定的關係(nvarchar 255)，暫時修改最多只取200長度的data。
					sendMessage = sendMessage.substring(0, 200);
				}
				
				// save record
				PushMessageRecord record = new PushMessageRecord();
				record.setProduct(pushApiModel.getDepartment());
				record.setUID(uids.get(i).toString());
				record.setSourceType(PushMessageRecord.SOURCE_TYPE_API);
				record.setSendType(pushApiModel.getSendTimeType());
				
				Date dateSendTime = new Date();
				if (pushApiModel.getSendTimeType().equals(PushApiModel.SEND_TYPE_DELAY)) {
					dateSendTime = pushApiModel.getSendTimeSet();
				}
				
				record.setSendTime(dateSendTime);
				record.setCreateTime(pushApiModel.getTriggerTime());
				record.setMainMessage("Error");
				record.setSendMessage(sendMessage);
				record.setDetailMessage(e.getClass().getName() + " occurred, please check the log.");
				
				PushMessageRecordService pushMessageRecordService = ApplicationContextProvider.getApplicationContext().getBean(PushMessageRecordService.class);
				pushMessageRecordService.save(record);
			}

			logger.error("[pushMessage] Exception = {}", e);
			
			if (e instanceof IllegalArgumentException) {
				return new ResponseEntity<>("{\"result\": 0, \"msg\": \"" + e.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
			}
			else if (e instanceof BadPaddingException || e instanceof IllegalBlockSizeException) {
				return new ResponseEntity<>("{\"error\": \"true\", \"message\": \"invalid token\"}", HttpStatus.UNAUTHORIZED);
			}

			return new ResponseEntity<>("{\"result\": 0, \"msg\": \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}