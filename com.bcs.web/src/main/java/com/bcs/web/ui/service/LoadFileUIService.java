package com.bcs.web.ui.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bcs.core.utils.ErrorRecord;

@Service
public class LoadFileUIService {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(LoadFileUIService.class);
	
	public static void loadFileToResponse(String filePath, String fileName, HttpServletResponse response) throws IOException {

		InputStream inp = new FileInputStream(filePath + System.getProperty("file.separator") + fileName);
		response.setContentType("application/download; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
		response.setCharacterEncoding("UTF-8");
		OutputStream outp = response.getOutputStream();
		try {
			IOUtils.copy(inp, outp);
			response.flushBuffer();
		} catch (IOException e) {
			logger.error(ErrorRecord.recordError(e));
			throw e;
		} finally {
			if (outp != null) {
				outp.close();
			}
		}
	}

	public static void askDownloadFileToResponse(String srcFile, String destFile, HttpServletResponse response) throws IOException {

		logger.info("srcFile = {}", srcFile);
		logger.info("destFile = {}", destFile);
		
		InputStream inp = new FileInputStream(srcFile);
		response.setContentType("application/download; charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(destFile, StandardCharsets.UTF_8.toString()));
		response.setCharacterEncoding("UTF-8");
		
		OutputStream outp = response.getOutputStream();
		
		try {
			IOUtils.copy(inp, outp);
			response.flushBuffer();
		} catch (IOException e) {
			logger.error("IOException = {}", e);
			throw e;
		} finally {
			if (outp != null) {
				outp.close();
			}

			if (inp != null) {
				inp.close();
			}
			
			File srcZipFile = new File(srcFile);
			logger.info("srcZipFile = {}", srcZipFile);
			
			logger.info("srcZipFile.exists() = {}", srcZipFile.exists());
			
			if (srcZipFile.exists()) {
				FileUtils.forceDelete(srcZipFile);
				
				logger.info("srcZipFile.exists() after delete = {}", srcZipFile.exists());
			}
		}
	}
}
