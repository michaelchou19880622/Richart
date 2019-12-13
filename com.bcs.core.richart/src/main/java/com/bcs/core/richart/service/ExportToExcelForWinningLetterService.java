package com.bcs.core.richart.service;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.repository.WinningLetterReportRepository;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.ErrorRecord;

@Service
public class ExportToExcelForWinningLetterService {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(ExportToExcelForWinningLetterService.class);

	@Autowired
	private WinningLetterReportRepository winningLetterReportRepository;
	
	/** Export winning letter list to excel **/
	public void exportToExcelForWinningListByLikeNameAndStatus(String exportPath, String fileName, String wlName, String wlStatus) throws Exception {
		try {
			long startTime = System.nanoTime();
			logger.info("[ findByNameAndStatus ] Start Time : {}", startTime);
			
			List<Object[]> lst_winningLetterSummaryReport = winningLetterReportRepository.findSummaryReportByLikeNameAndStatus("%" + wlName + "%", wlStatus);
			logger.info("lst_winningLetterSummaryReport = {}", lst_winningLetterSummaryReport);
			
			long endTime = System.nanoTime();
			logger.info("[ findByNameAndStatus ] End Time : {}", endTime);
			logger.info("[ findByNameAndStatus ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

			logger.info("[ findByNameAndStatus ] lst_winningLetterSummaryReport = {}", lst_winningLetterSummaryReport);
			
			String baseUrl = CoreConfigReader.getString(CONFIG_STR.BaseUrlHTTPS.toString());
			logger.info("baseUrl = {}", baseUrl);
			
			XSSFWorkbook wb = new XSSFWorkbook(); // HSSFWorkbook → xls, XSSFWorkbook → xlsx

			XSSFSheet sheetWinningLetter = wb.createSheet("中獎回函列表"); // create a new sheet
			
			XSSFRow row = sheetWinningLetter.createRow(0); // Declare header name
			
			XSSFCell cell;
			
			XSSFFont font = wb.createFont();
			font.setFontName("Arial");
			
			XSSFCellStyle style = wb.createCellStyle();
			style.setFont(font);

			DataFormat fmt = wb.createDataFormat();
			style.setDataFormat(fmt.getFormat("@"));
			
			row.createCell(0).setCellValue("名稱");
			row.createCell(1).setCellValue("狀態");
			row.createCell(2).setCellValue("填寫人數");
			row.createCell(3).setCellValue("中獎回函網址");
			row.createCell(4).setCellValue("回覆到期時間");
			row.createCell(5).setCellValue("建立時間");
			row.createCell(6).setCellValue("建立人員");
			row.createCell(7).setCellValue("修改時間");
			row.createCell(8).setCellValue("修改人員");

			for (int i = 0; i < sheetWinningLetter.getRow(0).getPhysicalNumberOfCells(); i++)
			{
				cell = row.getCell(i);
				cell.setCellStyle(style);
			}
			
			if (lst_winningLetterSummaryReport.size() != 0) {
				int seqNo = 1;
				
				for (Object[] object : lst_winningLetterSummaryReport) {

					logger.info("object = {}", Arrays.toString(object));
					
					row = sheetWinningLetter.createRow(seqNo);
					
					for (int i = 0; i < sheetWinningLetter.getRow(0).getPhysicalNumberOfCells(); i++)
					{
						cell = row.createCell(i);
						cell.setCellStyle(style);
					}
					
					row.getCell(0).setCellValue(object[0].toString()); // 名稱
					row.getCell(1).setCellValue(object[1].toString().equals("Active") ? "生效" : "取消"); // 狀態
					row.getCell(2).setCellValue(object[2].toString()); // 填寫人數
					row.getCell(3).setCellValue(baseUrl + object[3].toString()); // 中獎回函網址
					row.getCell(4).setCellValue(object[4].toString()); // 回覆到期時間
					row.getCell(5).setCellValue(object[5].toString()); // 建立時間
					row.getCell(6).setCellValue(object[6].toString()); // 建立人員
					row.getCell(7).setCellValue(object[7] == null ? "" : object[7].toString()); // 修改時間
					row.getCell(8).setCellValue(object[8] == null ? "" : object[8].toString()); // 修改人員
					
					seqNo++;
				}
				
				/* Automatically adjust column width and adapt to possible Chinese issues in cells. */
				for (Integer col_index = 0; col_index < sheetWinningLetter.getRow(0).getPhysicalNumberOfCells(); col_index++) {
					
					sheetWinningLetter.autoSizeColumn(col_index);
					sheetWinningLetter.setColumnWidth(col_index, sheetWinningLetter.getColumnWidth(col_index) * 12 / 10);
				}
			}
			
			// Save
			FileOutputStream out = new FileOutputStream(exportPath + System.getProperty("file.separator") + fileName);
			wb.write(out);
			out.close();
			wb.close();
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
		}
	}
}
