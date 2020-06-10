package com.bcs.core.bot.scheduler.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.bcs.core.bot.akka.service.PNPService;
import com.bcs.core.bot.pnp.model.FileHeaderModel;
import com.bcs.core.bot.pnp.model.FtpTaskModel;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.FtpUtil;
import com.bcs.core.utils.RestfulUtil;

public class FileParseTask implements Job {
	/** Logger */
	private static Logger logger = LogManager.getLogger(FileParseTask.class);
	
	PNPService PNPService = ApplicationContextProvider.getApplicationContext().getBean(PNPService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String hostname = CoreConfigReader.getString(CONFIG_STR.PNP_FTP_HOSTNAME, true),
				username = CoreConfigReader.getString(CONFIG_STR.PNP_FTP_USERNAME, true),
				password = CoreConfigReader.getString(CONFIG_STR.PNP_FTP_PASSWORD, true);
		String remoteDirPath = CoreConfigReader.getString(CONFIG_STR.PNP_FTP_REMOTE_DIR_PATH),
				localDirPath = CoreConfigReader.getString(CONFIG_STR.PNP_FTP_LOCAL_DIR_PATH);
		String completePattern = CoreConfigReader.getString(CONFIG_STR.PNP_FTP_FILE_COMPLETE_PATTERN);
		FtpUtil ftpClient = new FtpUtil(hostname, username, password);
		List<String> newTaskList = new ArrayList<String>();
		
		try {
			ftpClient.connect();	// 連線至 FTP 伺服器		
			
			List<Map<String, String>> fileList = ftpClient.getFileList(remoteDirPath);	// 取得目錄下的檔案列表
			
			/* 查找是否有新的檔案 */
			for(Map<String, String> file : fileList) {
				if(!file.get("name").contains(completePattern))
					newTaskList.add(file.get("name"));
			}
			
			if(newTaskList.size() > 0) {
				logger.debug(">> New task file: " + newTaskList);
				
				/* 將新的檔案下載至本機資料夾中，並將新檔案更名回寫至 FTP 上 */
				for(String fileName : newTaskList) {
					StringBuffer stringBuffer = new StringBuffer(fileName);
					String completeFileName = stringBuffer.insert(fileName.lastIndexOf('.'), completePattern).toString();
					
					File DirPath = new File(localDirPath);
					
					if(!DirPath.exists())
						DirPath.mkdirs();
					
					ftpClient.retrieveFile(remoteDirPath + System.getProperty("file.separator") + fileName, localDirPath + System.getProperty("file.separator") + fileName);
					ftpClient.deleteFile(remoteDirPath + System.getProperty("file.separator") + fileName);
					ftpClient.uploadFile(localDirPath + System.getProperty("file.separator") + fileName, completeFileName, remoteDirPath);
				}
				
				this.parseFile(newTaskList);
			}
			
			ftpClient.disconnect();	// 中斷與 FTP 伺服器的連線
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch(ParseException e) {
			logger.error(e.getMessage());
		} catch(KeyManagementException e) {
			logger.error(e.getMessage());
		} catch(NoSuchAlgorithmException e) {
			logger.error(e.getMessage());
		}
	}
	
	private void parseFile(List<String> fileList) throws FileNotFoundException, IOException, ParseException, KeyManagementException, NoSuchAlgorithmException {
		FileReader fileReader = null;
		BufferedReader bufferReader = null;
		String line = null;
		Integer rowNum = 0;
		
		for(String fileName : fileList) {
			FileHeaderModel fileHeader = new FileHeaderModel();
			List<String> requestObjects = new ArrayList<String>();
			
			fileReader = new FileReader(CoreConfigReader.getString(CONFIG_STR.PNP_FTP_LOCAL_DIR_PATH) + System.getProperty("file.separator") + fileName);
	        bufferReader = new BufferedReader(fileReader);
	        
	        Integer rowCount = 1;
	        while((line = bufferReader.readLine()) != null) {
	        	/* 解析檔案 head */
	        	if(rowNum == 0) {
	        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        		String[] partition = line.split("&");
	        		
	        		if(partition.length == 3) {
		        		fileHeader.setProduct(partition[0]);
		        		fileHeader.setMessageType(partition[1]);
		        		
		        		if(fileHeader.getMessageSendType().equals("DELAY")) {
		        			fileHeader.setScheduledTime(sdf.parse(partition[2]));
		        		}
	        		} else {
	        			bufferReader.close();
	        			
	        			throw new IOException("File \'" + fileName + "\' with invalid head format.");
	        		}
	        	} else {
		        	String[] partition = line.split("&");
		        	
		        	logger.debug(">>> Line " + rowCount + ": " + line);
		        	rowCount++;
		        	
		        	if(partition.length == 2) {
		        		JSONObject requestObject = new JSONObject();
		        		JSONObject messageObject = new JSONObject();
		        		JSONArray messageArray = new JSONArray();
		        		
		        		messageObject.put("type", "text");
		        		messageObject.put("text", partition[1].replace("\\n", "\n"));
		        		
		        		messageArray.put(messageObject);
		        		
		        		requestObject.put("to", partition[0]);
		        		requestObject.put("messages", messageArray);
		        		
		        		requestObjects.add(requestObject.toString());
		        	} else {
		        		bufferReader.close();
		        		
		        		throw new IOException("File \'" + fileName + "\' with invalid body format.");
		        	}
	        	}
	        	
	        	rowNum++;
	        }
	        
	        logger.debug(">>> File name: " + fileName);
	        logger.debug(">>> File head: " + fileHeader);
	        logger.debug(">>> File body: " + requestObjects);
	        
	        FtpTaskModel ftpTaskModel = new FtpTaskModel();
	        
	        ftpTaskModel.setFileName(fileName);
	        ftpTaskModel.setFileHead(fileHeader);
	        ftpTaskModel.setLineMessageObjects(requestObjects);
	        ftpTaskModel.setTimestamp(new Date());
	        
	        /* 判斷是否需要用 cluster mode 去發送訊息 */
	        if(CoreConfigReader.getBoolean(CONFIG_STR.BCS_API_CLUSTER_SEND_THIS)) {
	        	PNPService.tell(ftpTaskModel);	// 將物件丟給 Akka 的 PNP Master Actor 處理
	        } else {
	        	this.sendToCluster(ftpTaskModel);	// 丟給 Cluster 處理
	        }
		}
	}
	
	private void sendToCluster(FtpTaskModel ftpTaskModel) throws KeyManagementException, NoSuchAlgorithmException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		/* 將 headers 跟 body 塞進 HttpEntity 中 */
		HttpEntity<FtpTaskModel> httpEntity = new HttpEntity<FtpTaskModel>(ftpTaskModel, headers);
		
		RestfulUtil httpClient = new RestfulUtil(HttpMethod.POST, CoreConfigReader.getString(CONFIG_STR.PNP_CLUSTER_SEND_URL), httpEntity);
		
		try {
			httpClient.execute();
		} catch(Exception e) {
			logger.error(">>> Send to cluster error: " + e.getMessage());
		}
	}
}