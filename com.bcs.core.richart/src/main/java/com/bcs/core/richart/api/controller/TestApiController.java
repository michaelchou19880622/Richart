package com.bcs.core.richart.api.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.FtpUtil;

@Controller
@RequestMapping("/api")
public class TestApiController {
	private static Logger logger = Logger.getLogger(TestApiController.class);
	
	@RequestMapping(method = RequestMethod.POST, value = "/ftp/list", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> getFileList(@RequestBody String requestBodyString){
		try {
			JSONObject requestBody = new JSONObject(requestBodyString);
			String hostname = requestBody.has("hostname") ? requestBody.getString("hostname") : null;
			String username = requestBody.has("username") ? requestBody.getString("username") : null;
			String password = requestBody.has("password") ? requestBody.getString("password") : null;
			String remoteDirectoryPath = requestBody.has("remoteDirectoryPath") ? requestBody.getString("remoteDirectoryPath") : null;
			
			if(StringUtils.isEmpty(hostname) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(remoteDirectoryPath))
				return new ResponseEntity<>("{\"error\": \"true\", \"msg\": \"Missing parameters.\"}", HttpStatus.BAD_REQUEST);
			
			FtpUtil ftpClient = new FtpUtil(hostname, username, password);
			
			ftpClient.connect();
			
			List<Map<String, String>> list = ftpClient.getFileList(remoteDirectoryPath);
			
			ftpClient.disconnect();
			
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch(IllegalArgumentException e) {
			return new ResponseEntity<>("{\"error\": \"true\", \"msg\": \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(IOException e) {
			return new ResponseEntity<>("{\"error\": \"true\", \"msg\": \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(JSONException e) {
			return new ResponseEntity<>("{\"error\": \"true\", \"msg\": \"" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/file/upload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<?> fileUpload(@RequestPart MultipartFile filePart) {
		try {
			String directoryPath = CoreConfigReader.getString(CONFIG_STR.FilePath) + System.getProperty("file.separator") + "UPLOAD";
			
			File folder = new File(directoryPath);
			if(!folder.exists()){
				folder.mkdirs();
			}
			
			String filePath = directoryPath + System.getProperty("file.separator") + filePart.getOriginalFilename();
			
			filePart.transferTo(new File(filePath));
			
			return new ResponseEntity<>("{\"error\": false, \"msg\": null}", HttpStatus.OK);
		} catch (IllegalStateException | IOException e) {
			return new ResponseEntity<>("{\"error\": true, \"msg\": " + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
		}
 	}
}
