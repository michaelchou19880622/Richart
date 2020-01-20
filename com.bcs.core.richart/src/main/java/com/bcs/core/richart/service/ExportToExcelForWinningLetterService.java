package com.bcs.core.richart.service;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

import com.bcs.core.db.entity.ContentResource;
import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.db.repository.WinningLetterRecordRepository;
import com.bcs.core.db.repository.WinningLetterReportRepository;
import com.bcs.core.db.repository.WinningLetterRepository;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;

@Service
public class ExportToExcelForWinningLetterService {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(ExportToExcelForWinningLetterService.class);

	@Autowired
	private WinningLetterRepository winningLetterRepository;

	@Autowired
	private WinningLetterReportRepository winningLetterReportRepository;
	
	@Autowired
	private WinningLetterRecordRepository winningLetterRecordRepository;
	
	/** Export winning letter list to excel **/
	public void exportToExcelForWinningListByLikeNameAndStatus(String exportPath, String fileName, String wlName, String wlStatus) throws Exception {
		try {
			long startTime = System.nanoTime();
			logger.info("[ findByNameAndStatus ] Start Time : {}", startTime);
			
			List<Object[]> lst_winningLetterSummaryReport = new ArrayList<Object[]>();
			
			if (StringUtils.isBlank(wlName)) {
				lst_winningLetterSummaryReport = winningLetterReportRepository.findSummaryReportByStatus(wlStatus);
			} else {
				lst_winningLetterSummaryReport = winningLetterReportRepository.findSummaryReportByLikeNameAndStatus(wlName, wlStatus);
			}
			
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
			logger.error("Exception : ", e);
		}
	}
	
	/** Export winner reply list to excel **/
	public void exportToExcelForWinnerReplyListByWinningLetterId(String basePath, String exportPath, String fileName, String wlId) throws Exception {
		
		logger.info("basePath = {}", basePath);
//		logger.info("exportPath = {}", exportPath);
//		logger.info("fileName = {}", fileName);
//		logger.info("wlId = {}", wlId);
		
		try {
			long startTime = System.nanoTime();
			logger.info("[ exportToExcelForWinnerReplyListByWinningLetterId ] Start Time : {}", startTime);
			
			WinningLetter winningLetter = winningLetterRepository.findById(Long.valueOf(wlId));
			
			List<WinningLetterRecord> lst_winnerReplyList = winningLetterRecordRepository.findAllByWinningLetterIdOrderByIdAsc(Long.valueOf(wlId));
			
			long endTime = System.nanoTime();
			logger.info("[ exportToExcelForWinnerReplyListByWinningLetterId ] End Time : {}", endTime);
			logger.info("[ exportToExcelForWinnerReplyListByWinningLetterId ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

			logger.info("[ exportToExcelForWinnerReplyListByWinningLetterId ] lst_winnerReplyList = {}", lst_winnerReplyList);
			
			String baseUrl = CoreConfigReader.getString(CONFIG_STR.BaseUrlHTTPS.toString());
			logger.info("baseUrl = {}", baseUrl);
			
			String getResouceImageUrl = basePath + CoreConfigReader.getString(CONFIG_STR.PageBCS.toString()) + CoreConfigReader.getString("rest.api.path.resource") + ContentResource.RESOURCE_TYPE_IMAGE;
			logger.info("getResouceImageUrl = {}", getResouceImageUrl);
			
			XSSFWorkbook wb = new XSSFWorkbook(); // HSSFWorkbook → xls, XSSFWorkbook → xlsx

			XSSFSheet sheetWinningLetter = wb.createSheet("中獎回函名單列表"); // create a new sheet
			
			XSSFRow row = sheetWinningLetter.createRow(0); // Declare header name
			
			XSSFCell cell;
			
			XSSFFont font = wb.createFont();
			font.setFontName("Arial");
			
			XSSFCellStyle style = wb.createCellStyle();
			style.setFont(font);

			DataFormat fmt = wb.createDataFormat();
			style.setDataFormat(fmt.getFormat("@"));
			
			row.createCell(0).setCellValue("中獎回函名稱");
			row.createCell(1).setCellValue("UID");
			row.createCell(2).setCellValue("中獎者姓名");
			row.createCell(3).setCellValue("連絡電話");
			row.createCell(4).setCellValue("身分證字號");
			row.createCell(5).setCellValue("中獎贈品");
			row.createCell(6).setCellValue("戶籍地址");
			row.createCell(7).setCellValue("通訊地址");
			row.createCell(8).setCellValue("身分證反面");
			row.createCell(9).setCellValue("身分證正面");
			row.createCell(10).setCellValue("簽名檔");
			row.createCell(11).setCellValue("客戶回覆時間");

			for (int i = 0; i < sheetWinningLetter.getRow(0).getPhysicalNumberOfCells(); i++)
			{
				cell = row.getCell(i);
				cell.setCellStyle(style);
			}
			
			if (lst_winnerReplyList.size() != 0) {
				int seqNo = 1;
				
				for (WinningLetterRecord winningLetterRecord : lst_winnerReplyList) {

					logger.info("winningLetterRecord = {}", winningLetterRecord);
					
					row = sheetWinningLetter.createRow(seqNo);
					
					for (int i = 0; i < sheetWinningLetter.getRow(0).getPhysicalNumberOfCells(); i++)
					{
						cell = row.createCell(i);
						cell.setCellStyle(style);
					}

					row.getCell(0).setCellValue(winningLetter.getName()); // 中獎回函名稱
					row.getCell(1).setCellValue(winningLetterRecord.getUid()); // UID
					row.getCell(2).setCellValue(winningLetterRecord.getName()); // 中獎者姓名
					row.getCell(3).setCellValue(winningLetterRecord.getPhoneNumber()); // 連絡電話
					row.getCell(4).setCellValue(winningLetterRecord.getIdCardNumber()); // 身分證字號
					row.getCell(5).setCellValue(winningLetter.getGift()); // 中獎贈品
					row.getCell(6).setCellValue(winningLetterRecord.getResidentAddress()); // 戶籍地址
					row.getCell(7).setCellValue(winningLetterRecord.getMailingAddress()); // 通訊地址
					row.getCell(8).setCellValue((winningLetterRecord.getIdCardCopyFront() == null)? "" : getResouceImageUrl + "/" + winningLetterRecord.getIdCardCopyFront()); // 身分證反面
					row.getCell(9).setCellValue((winningLetterRecord.getIdCardCopyBack() == null)? "" : getResouceImageUrl + "/" + winningLetterRecord.getIdCardCopyBack()); // 身分證正面
					row.getCell(10).setCellValue((winningLetterRecord.geteSignature() == null)? "" : getResouceImageUrl + "/" + winningLetterRecord.geteSignature()); // 簽名檔
					row.getCell(11).setCellValue(winningLetterRecord.getRecordTime().toString()); // 客戶回覆時間
					
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
			logger.error("Exception : ", e);
		}
	}
}
