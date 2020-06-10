package com.bcs.web.ui.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.report.export.ExportToExcelForLinkPage;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.model.LinkClickReportModel;

@Service
public class ExportExcelForLinkPageSrevice {
	@Autowired
	private  ExportToExcelForLinkPage ExportToExcelForLinkPage;
	
	/** Logger */
	private static Logger logger = LogManager.getLogger(ExportExcelForLinkPageSrevice.class);
	
	public void exportExcelForSummary(HttpServletRequest request, HttpServletResponse response,Map<String, LinkClickReportModel> linkResult) throws IOException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator");
		Date date = new Date();
		String fileName = "ExcelForSummaryUid" + sdf.format(date) + ".xlsx";
		
		try {
			ExportToExcelForLinkPage.exportToExcelforSummary(filePath,fileName,linkResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("Exception = " + e.getMessage());
			e.printStackTrace();
		}
		//exportToExcelFromDB.exportToExcel(filePath, fileName, excelName, titles, data);

		LoadFileUIService.loadFileToResponse(filePath, fileName, response);	
	}

	public void exportExcelForInterface(HttpServletRequest request, HttpServletResponse response, Map<String, LinkClickReportModel> linkResult) throws IOException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator");
		Date date = new Date();
		String fileName = "ExcelForInterface" + sdf.format(date) + ".xlsx";
		
		try {
			ExportToExcelForLinkPage.exportExcelForInterface(filePath,fileName,linkResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("e = " + e.getMessage());
			e.printStackTrace();
		}
		
		LoadFileUIService.loadFileToResponse(filePath, fileName, response);
		
	}
}
