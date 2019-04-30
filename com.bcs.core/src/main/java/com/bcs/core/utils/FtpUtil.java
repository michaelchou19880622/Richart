package com.bcs.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.jcodec.common.StringUtils;

public class FtpUtil {
	private FTPClient ftpClient = null;
	private String hostname = null;
	private String username = null;
	private String password = null;
	
	public FtpUtil() {
		ftpClient = new FTPClient();
	}
	
	public FtpUtil(String hostname, String username, String password) {
		this.ftpClient = new FTPClient();
		
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}
	
	/* 建立連線 */
	public void connect() throws IllegalArgumentException, IOException {
		if(StringUtils.isEmpty(this.hostname) || StringUtils.isEmpty(this.username) || StringUtils.isEmpty(this.password))
			throw new IllegalArgumentException("Missing parameters.");
		else {
			this.ftpClient.connect(this.hostname); 
			this.ftpClient.login(this.username, this.password);
		}
	}
	
	/* 中斷連線 */
	public void disconnect() throws IOException {
		if(this.ftpClient == null || !this.ftpClient.isConnected())
			throw new IOException("Ftp client isn't connected.");
		else {
			this.ftpClient.logout();
			this.ftpClient.disconnect();
		}
	}
	
	/* 下載檔案 */
	public void retrieveFile(String remoteFilePath, String localFilePath) throws IOException {
		if(this.ftpClient == null || !this.ftpClient.isConnected())
			throw new IOException("Ftp client is disconnected.");
		else {
			FileOutputStream fileOutputStream = new FileOutputStream(localFilePath);
			
			this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			this.ftpClient.retrieveFile(remoteFilePath, fileOutputStream);
			
			fileOutputStream.close();
		}
	}
	
	/* 上傳檔案 */
	public void uploadFile(String localFilePath, String remoteFileName, String remoteDirectoryPath) throws IOException {
		if(this.ftpClient == null || !this.ftpClient.isConnected())
			throw new IOException("Ftp client is disconnected.");
		else {
			File sourceFile = new File(localFilePath);
			FileInputStream fileInputStream =  new FileInputStream(sourceFile);
			
			this.ftpClient.changeWorkingDirectory(remoteDirectoryPath);
			this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.storeFile(remoteFileName, fileInputStream);
			
			fileInputStream.close();
		}
	}
	
	/* 刪除檔案 */
	public void deleteFile(String remoteFilePath) throws IOException {
		if(this.ftpClient == null || !this.ftpClient.isConnected())
			throw new IOException("Ftp client is disconnected.");
		else {
			Boolean flag = ftpClient.deleteFile(remoteFilePath);
			
			if(!flag)
				throw new FileNotFoundException("Failed to delete file: \'" + remoteFilePath + "\'");
		}
	}
	
	/* 列出檔案清單 */
	public List<Map<String, String>> getFileList(String remoteDirectoryPath) throws IOException {
		if(this.ftpClient == null || !this.ftpClient.isConnected())
			throw new IOException("Ftp client is disconnected.");
		else {
			List<Map<String, String>> fileList = new ArrayList<Map<String, String>>();
			Map<String, String> fileInfo = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			remoteDirectoryPath = new String(remoteDirectoryPath.getBytes("UTF-8"), "ISO-8859-1");
			
			this.ftpClient.changeWorkingDirectory(remoteDirectoryPath);
			FTPFile[] files = this.ftpClient.listFiles();
			
			for(Integer i = 0; i < files.length; i++) {
				fileInfo = new HashMap<String, String>();
				
				fileInfo.put("name", new String(files[i].getName().getBytes("ISO-8859-1"), "UTF-8"));
				fileInfo.put("size", String.valueOf(files[i].getSize()) + " Bytes");
				fileInfo.put("timestamp", sdf.format(files[i].getTimestamp().getTime()));
				fileInfo.put("type", files[i].isFile() ? "file" : "directory");
				
				fileList.add(fileInfo);
			}
			
			return fileList;
		}
	}
	
	/* Setters */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPasswrod(String password) {
		this.password = password;
	}
}
