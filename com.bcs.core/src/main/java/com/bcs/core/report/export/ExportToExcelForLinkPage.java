package com.bcs.core.report.export;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.ContentLink;
import com.bcs.core.db.service.ContentLinkService;
import com.bcs.core.enums.RECORD_REPORT_TYPE;
import com.bcs.core.model.LinkClickReportModel;
import com.bcs.core.report.service.ContentLinkReportService;
import com.bcs.core.utils.ErrorRecord;

@Service
public class ExportToExcelForLinkPage {
	@Autowired
	private ContentLinkService contentLinkService;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(ExportToExcelForLinkPage.class);

	/**
	 * 匯出 Link Click Report EXCEL
	 */
	public void exportToExcelforSummary(String exportPath, String fileName, Map<String, LinkClickReportModel> linkResult) throws Exception {
		try {
			Workbook wb = new XSSFWorkbook(); //→xls // new XSSFWorkbook()→xlsx
			
			Sheet sheetLink = wb.createSheet("Link Click Report"); // create a new sheet
			
			
			// Save
			FileOutputStream out = new FileOutputStream(exportPath + System.getProperty("file.separator") + fileName);
			this.exportToExcelForSummary(wb, sheetLink, linkResult);
			
			wb.write(out);
			out.close();
			wb.close();
		} catch (Exception e) {
    		logger.error(ErrorRecord.recordError(e));
		}
	}
	public void exportToExcelForSummary(Workbook wb ,Sheet sheetLink , Map<String, LinkClickReportModel> linkResult) {
		
		Row row = sheetLink.createRow(0); // declare a row object reference
		row.createCell(0).setCellValue("連結名稱");
		row.createCell(1).setCellValue("連結");
		row.createCell(2).setCellValue("時間");
		row.createCell(3).setCellValue("註記");
		row.createCell(4).setCellValue("點擊UID");
		int seqNo = 1;
		for(LinkClickReportModel model : linkResult.values()){
			List<Object[]> resultUID = contentLinkService.findAllLinkUrlForallUID(model.getLinkUrl());
			for(Object[] UID : resultUID) {
				System.out.println(UID[0].toString());
				Row row1 = sheetLink.createRow(seqNo);
				row1.createCell(0).setCellValue(model.getLinkTitle());
				row1.createCell(1).setCellValue(model.getLinkUrl());
				row1.createCell(2).setCellValue(model.getLinkTime().substring(0, 10));//只顯示年月日
			    row1.createCell(3).setCellValue(model.getLinkFlag());
				row1.createCell(4).setCellValue(UID[0].toString());
				seqNo++;
			}
		}
	}
	
	public void exportExcelForInterface(String exportPath, String fileName, Map<String, LinkClickReportModel> linkResult) throws Exception {
		try {
			Workbook wb = new XSSFWorkbook(); //→xls // new XSSFWorkbook()→xlsx
			Sheet sheetLink = wb.createSheet("interface Report"); // create a new sheet
			FileOutputStream out = new FileOutputStream(exportPath + System.getProperty("file.separator") + fileName);

			this.exportExcelForInterface(wb, sheetLink, linkResult);
			
			wb.write(out);
			out.close();
			wb.close();
		} catch (Exception e) {
    		logger.error(ErrorRecord.recordError(e));
		}
	}
	
	public void exportExcelForInterface(Workbook wb ,Sheet sheetLink , Map<String, LinkClickReportModel> linkResult) {
			
		Row row = sheetLink.createRow(0); // declare a row object reference
		row.createCell(0).setCellValue("連結名稱");
		row.createCell(1).setCellValue("連結");
		row.createCell(2).setCellValue("時間/註記");
		row.createCell(3).setCellValue("點擊次數");
		row.createCell(4).setCellValue("點擊人數");
		int seqNo = 1;
		for(LinkClickReportModel model : linkResult.values()) {
			
			Row row1 = sheetLink.createRow(seqNo);
			row1.createCell(0).setCellValue(model.getLinkTitle());
			row1.createCell(1).setCellValue(model.getLinkUrl());
			if(StringUtils.isBlank(model.getLinkFlag())) {
				row1.createCell(2).setCellValue(model.getLinkTime().substring(0, 10));
			}else {
				row1.createCell(2).setCellValue(model.getLinkTime().substring(0, 10)+model.getLinkFlag());
			}
			row1.createCell(3).setCellValue(model.getTotalCount().toString());
			row1.createCell(4).setCellValue(model.getUserCount().toString());
			seqNo++;
		}
	}
}
	