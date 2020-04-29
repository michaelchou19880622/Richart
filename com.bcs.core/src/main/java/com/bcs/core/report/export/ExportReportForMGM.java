package com.bcs.core.report.export;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.ShareCampaign;
import com.bcs.core.db.service.ShareCampaignService;
import com.bcs.core.db.service.ShareUserRecordService;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.SQLDateFormatUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExportReportForMGM {

	public static final String REPORT_TYPE_COMPLETED = "COMPLETED";
	public static final String REPORT_TYPE_UNCOMPLETED = "UNCOMPLETED";

	public static final String REPORT_TYPE_FOLLOW = "FOLLOW";
	public static final String REPORT_TYPE_DISABLE = "DISABLE";
	public static final String REPORT_TYPE_BINDED = "BINDED";

	/** Logger */
	private static Logger logger = Logger.getLogger(ExportReportForMGM.class);

	@Autowired
	private ShareUserRecordService shareUserRecordService;
	@Autowired
	private ShareCampaignService shareCampaignService;

	/**
	 * 匯出
	 */
	public void exportToExcel(String exportPath, String fileName, String startDate, String endDate, String campaignId, String reportType) throws Exception {
		try {
			log.info("[exportToExcel] exportPath = {}", exportPath);
			log.info("[exportToExcel] fileName = {}", fileName);
			log.info("[exportToExcel] startDate = {}", startDate);
			log.info("[exportToExcel] endDate = {}", endDate);
			log.info("[exportToExcel] campaignId = {}", campaignId);
			log.info("[exportToExcel] reportType = {}", reportType);

			Workbook wb = new XSSFWorkbook(); // →xls // new XSSFWorkbook()→xlsx

			ShareCampaign shareCampaign = shareCampaignService.findOne(campaignId);
			log.info("[exportToExcel] shareCampaign = {}", shareCampaign);

			Sheet sheetLink = wb.createSheet(shareCampaign.getCampaignName()); // create a new sheet

			if (REPORT_TYPE_COMPLETED.equals(reportType)) {
				this.exportCompleted(wb, sheetLink, startDate, endDate, campaignId, reportType);
			} else if (REPORT_TYPE_UNCOMPLETED.equals(reportType)) {
				this.exportUncompleted(wb, sheetLink, startDate, endDate, campaignId, reportType);
			} else {
				this.exportDetail(wb, sheetLink, startDate, endDate, campaignId);
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

	public void exportDetail(Workbook wb, Sheet sheet, String startDate, String endDate, String campaignId) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date start = sdf.parse(startDate);
		Date end = sdf.parse(endDate);
		Calendar c = Calendar.getInstance();
		c.setTime(end);
		c.add(Calendar.DATE, 1); // 增加一天，因為轉換的date其分秒是0，因此查詢時，今天新增的發送報告有設定時與分時，可能會撈不到
		c.add(Calendar.SECOND, -1); // 減一秒，因為可能今天新增的發送報告時間是隔天且無設定時與分，會與增加一天的時間重疊，導致可能撈到隔天的資料
		end = c.getTime();
		
		ShareCampaign shareCampaign = shareCampaignService.findOne(campaignId);
		log.info("shareCampaign = {}", shareCampaign);

		String JUDGEMENT = shareCampaign.getJudgement();
		log.info("JUDGEMENT = {}", JUDGEMENT);
//		if (resultGet.size() != 0) {
//			Object[] o = resultGet.get(0);
//			JUDGEMENT = o[10].toString();
//		}
		
		List<Object[]> resultGet = null;
		
		if (JUDGEMENT.equals(REPORT_TYPE_DISABLE)){
			resultGet = shareUserRecordService.findByModifyTimeAndCampaignId_for_disable(start, end, campaignId);
		} else {
			resultGet = shareUserRecordService.findByModifyTimeAndCampaignId_for_follow_binded(start, end, campaignId);
		}

		Row row = sheet.createRow(0); // declare a row object reference
		if (JUDGEMENT.equals("")) {
			row.createCell(0).setCellValue("分享者UID");
			row.createCell(1).setCellValue("分享時間");
			row.createCell(2).setCellValue("被分享者UID");
			row.createCell(3).setCellValue("被分享者好友狀態");
			row.createCell(4).setCellValue("被分享者是否完成活動");
			row.createCell(5).setCellValue("被分享者點擊時間");
		} else if (JUDGEMENT.equals(REPORT_TYPE_DISABLE)) {
			row.createCell(0).setCellValue("分享者UID");
			row.createCell(1).setCellValue("分享時間");
			row.createCell(2).setCellValue("被分享者UID");
			row.createCell(3).setCellValue("被分享者好友狀態");
			row.createCell(4).setCellValue("被分享者是否完成活動");
			row.createCell(5).setCellValue("被分享者點擊時間");

			int seqNo = 1; // 序號

			String lastShareUserRecord = "";

			int donatorCount = 0;

			for (int i = 0; i < resultGet.size(); i++) {
				Object[] o = resultGet.get(i);

				String currentShareUserRecord = o[11].toString();

				long shareTimes = Long.valueOf(o[12].toString());

				Row row1 = sheet.createRow(seqNo);
				row1.createCell(0).setCellValue(o[0] == null ? "" : o[0].toString());
				row1.createCell(1).setCellValue(o[1] == null ? "" : sdf2.format(SQLDateFormatUtil.formatSqlStringToDate(o[1], sdf2)));
				row1.createCell(2).setCellValue(o[2] == null ? "" : o[2].toString());
				row1.createCell(3).setCellValue(o[2] == null ? "" : (o[3].toString().equals("1") ? "NEW" : "OLD"));

				if (currentShareUserRecord.equals(lastShareUserRecord)) {
					// 如果分享紀錄ID一樣，且點擊紀錄時間不為空，則判斷是否有貢獻過的紀錄? 有則寫N
					// 若沒有貢獻過的紀錄，則再判斷是否已超過完成任務人數上限? 超過則寫"超過上限"，未超過則寫Y
					row1.createCell(4).setCellValue(o[2] == null ? "" : (o[6].toString().equals("0") ? ((donatorCount + 1 > shareTimes) ? "超過上限" : "Y") : "N"));

					if (o[6].toString().equals("1")) { //表示當前被分享者已經協助達成任務，貢獻者 += 1
						donatorCount++;
					}
					
				} else {
					donatorCount = 0;
					
					// 如果分享紀錄ID不一樣，且點擊紀錄時間不為空，則判斷是否有貢獻過的紀錄，沒有則寫Y，有則寫N。
					row1.createCell(4).setCellValue(o[2] == null ? "" : (o[6].toString().equals("0") ? "Y" : "N"));
				}

				row1.createCell(5).setCellValue(o[7] == null ? "" : sdf2.format(SQLDateFormatUtil.formatSqlStringToDate(o[7], sdf2)));
				seqNo++;

				lastShareUserRecord = currentShareUserRecord;
			}

		} else if (JUDGEMENT.equals(REPORT_TYPE_FOLLOW)) {
			row.createCell(0).setCellValue("分享者UID");
			row.createCell(1).setCellValue("分享時間");
			row.createCell(2).setCellValue("被分享者UID");
			row.createCell(3).setCellValue("被分享者好友狀態");
			row.createCell(4).setCellValue("被分享者是否完成活動");
			row.createCell(5).setCellValue("被分享者加好友時間");

			int seqNo = 1; // 序號

			String lastShareUserRecord = "";

			int donatorCount = 0;

			for (int i = 0; i < resultGet.size(); i++) {
				Object[] o = resultGet.get(i);

				String currentShareUserRecord = o[11].toString();

				// 完成任務上限數/人
				long shareTimes = Long.valueOf(o[12].toString());

				Row row1 = sheet.createRow(seqNo);
				row1.createCell(0).setCellValue(o[0] == null ? "" : o[0].toString());
				row1.createCell(1).setCellValue(o[1] == null ? "" : sdf2.format(SQLDateFormatUtil.formatSqlStringToDate(o[1], sdf2)));
				row1.createCell(2).setCellValue(o[2] == null ? "" : o[2].toString());
				row1.createCell(3).setCellValue(o[2] == null ? "" : (o[3].toString().equals("1") ? "NEW" : "OLD"));

				if (currentShareUserRecord.equals(lastShareUserRecord)) {
					// 如果分享紀錄ID一樣，且點擊紀錄時間不為空，則判斷是否有貢獻過的紀錄? 沒有則寫N
					// 若有貢獻過的紀錄，則再判斷是否已超過完成任務人數上限? 超過則寫"超過上限"，未超過則寫Y
					
					row1.createCell(4).setCellValue(o[2] == null ? "" : (o[6].toString().equals("1") ? ((donatorCount + 1 > shareTimes) ? "超過上限" : "Y") : "N"));

					if (o[6].toString().equals("1")) { //表示當前被分享者已經協助達成任務，貢獻者 += 1
						donatorCount++;
					}
					
				} else {
					donatorCount = 0;
					
					// 如果分享紀錄ID不一樣，且點擊紀錄時間不為空，則判斷是否有貢獻過的紀錄，有則寫Y，沒有則寫N。
					row1.createCell(4).setCellValue(o[2] == null ? "" : (o[6].toString().equals("1") ? "Y" : "N"));
				}

				row1.createCell(5).setCellValue(o[8] == null ? "" : sdf2.format(SQLDateFormatUtil.formatSqlStringToDate(o[8], sdf2)));
				seqNo++;

				lastShareUserRecord = currentShareUserRecord;
			}
		} else if (JUDGEMENT.equals(REPORT_TYPE_BINDED)) {
			row.createCell(0).setCellValue("分享者UID");
			row.createCell(1).setCellValue("分享時間");
			row.createCell(2).setCellValue("被分享者UID");
			row.createCell(3).setCellValue("被分享者綁定狀態");
			row.createCell(4).setCellValue("被分享者是否完成活動");
			row.createCell(5).setCellValue("被分享者綁定時間");

			int seqNo = 1; // 序號

			String lastShareUserRecord = "";

			int donatorCount = 0;

			for (int i = 0; i < resultGet.size(); i++) {
				Object[] o = resultGet.get(i);

				String currentShareUserRecord = o[11].toString();
				
				long shareTimes = Long.valueOf(o[12].toString());

				Row row1 = sheet.createRow(seqNo);
				row1.createCell(0).setCellValue(o[0] == null ? "" : o[0].toString());
				row1.createCell(1).setCellValue(o[1] == null ? "" : sdf2.format(SQLDateFormatUtil.formatSqlStringToDate(o[1], sdf2)));
				row1.createCell(2).setCellValue(o[2] == null ? "" : o[2].toString());
				if (o[2] == null) {
					row1.createCell(3).setCellValue("");
				} else if (o[4].toString().equals("1")) {
					row1.createCell(3).setCellValue("NEW");
				} else if (o[4].toString().equals("0") && o[5].toString().equals("0")) {
					row1.createCell(3).setCellValue("未綁定");
				} else if (o[4].toString().equals("0") && o[5].toString().equals("1")) {
					row1.createCell(3).setCellValue("old");
				}
				
				if (currentShareUserRecord.equals(lastShareUserRecord)) {
					// 如果分享紀錄ID一樣，且點擊紀錄時間不為空，則判斷是否有貢獻過的紀錄? 沒有則寫N
					// 若有貢獻過的紀錄，則再判斷是否已超過完成任務人數上限? 超過則寫"超過上限"，未超過則寫Y
					row1.createCell(4).setCellValue(o[2] == null ? "" : (o[6].toString().equals("1") ? ((donatorCount + 1 > shareTimes) ? "超過上限" : "Y") : "N"));

					if (o[6].toString().equals("1")) { //表示當前被分享者已經協助達成任務，貢獻者 += 1
						donatorCount++;
					}
					
				} else {
					donatorCount = 0;
					
					// 如果分享紀錄ID不一樣，且點擊紀錄時間不為空，則判斷是否有貢獻過的紀錄，有則寫Y，沒有則寫N。
					row1.createCell(4).setCellValue(o[2] == null ? "" : (o[6].toString().equals("1") ? "Y" : "N"));
				}
				
				row1.createCell(5).setCellValue(o[9] == null ? "" : sdf2.format(SQLDateFormatUtil.formatSqlStringToDate(o[9], sdf2)));
				seqNo++;

				lastShareUserRecord = currentShareUserRecord;
			}
		}
	}

	public void exportCompleted(Workbook wb, Sheet sheet, String startDate, String endDate, String campaignId, String reportType) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date start = sdf.parse(startDate);
		Date end = sdf.parse(endDate);
		Calendar c = Calendar.getInstance();
		c.setTime(end);
		c.add(Calendar.DATE, 1); // 增加一天，因為轉換的date其分秒是0，因此查詢時，今天新增的發送報告有設定時與分時，可能會撈不到
		c.add(Calendar.SECOND, -1); // 減一秒，因為可能今天新增的發送報告時間是隔天且無設定時與分，會與增加一天的時間重疊，導致可能撈到隔天的資料
		end = c.getTime();
		
		ShareCampaign shareCampaign = shareCampaignService.findOne(campaignId);
		log.info("shareCampaign = {}", shareCampaign);

		String JUDGEMENT = shareCampaign.getJudgement();
		log.info("JUDGEMENT = {}", JUDGEMENT);
		
		List<Object[]> resultGet = null;
		
		if (JUDGEMENT.equals(REPORT_TYPE_DISABLE)){
			resultGet = shareUserRecordService.findCompletedByModifyTimeAndCampaignId_for_disable(start, end, campaignId);
		} else {
			resultGet = shareUserRecordService.findCompletedByModifyTimeAndCampaignId_for_follow_binded(start, end, campaignId);
		}

//		List<Object[]> resultGet = shareUserRecordService.findCompletedByModifyTimeAndCampaignId(start, end, campaignId);
		
//		findCompletedByModifyTimeAndCampaignId_for_disable

		Row row = sheet.createRow(0); // declare a row object reference
		row.createCell(0).setCellValue("分享者UID");
		row.createCell(1).setCellValue("分享時間");
		row.createCell(2).setCellValue("被分享者點擊活動人數");
		row.createCell(3).setCellValue("被分享者完成活動人數");

		if (resultGet != null && resultGet.size() != 0) {
			int seqNo = 1; // 序號

			for (Object[] o : resultGet) {

				Row row1 = sheet.createRow(seqNo);
				row1.createCell(0).setCellValue(o[0] == null ? "" : o[0].toString());
				row1.createCell(1).setCellValue(o[1] == null ? "" : sdf2.format(SQLDateFormatUtil.formatSqlStringToDate(o[1], sdf2)));
				row1.createCell(2).setCellValue(o[2] == null ? "" : o[2].toString());
				row1.createCell(3).setCellValue(o[3] == null ? "" : o[3].toString());
				seqNo++;
			}
		}
	}

	public void exportUncompleted(Workbook wb, Sheet sheet, String startDate, String endDate, String campaignId, String reportType) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date start = sdf.parse(startDate);
		Date end = sdf.parse(endDate);
		Calendar c = Calendar.getInstance();
		c.setTime(end);
		c.add(Calendar.DATE, 1); // 增加一天，因為轉換的date其分秒是0，因此查詢時，今天新增的發送報告有設定時與分時，可能會撈不到
		c.add(Calendar.SECOND, -1); // 減一秒，因為可能今天新增的發送報告時間是隔天且無設定時與分，會與增加一天的時間重疊，導致可能撈到隔天的資料
		end = c.getTime();

		List<Object[]> resultGet = null;

		resultGet = shareUserRecordService.findUncompletedByModifyTimeAndCampaignId(start, end, campaignId);

		Row row = sheet.createRow(0); // declare a row object reference
		row.createCell(0).setCellValue("分享者UID");
		row.createCell(1).setCellValue("分享時間");
		row.createCell(2).setCellValue("被分享者點擊活動人數");
		row.createCell(3).setCellValue("被分享者完成活動人數");

		if (resultGet != null && resultGet.size() != 0) {
			int seqNo = 1; // 序號

			for (Object[] o : resultGet) {

				Row row1 = sheet.createRow(seqNo);
				row1.createCell(0).setCellValue(o[0] == null ? "" : o[0].toString());
				row1.createCell(1).setCellValue(o[1] == null ? "" : sdf2.format(SQLDateFormatUtil.formatSqlStringToDate(o[1], sdf2)));
				row1.createCell(2).setCellValue(o[2] == null ? "" : o[2].toString());
				row1.createCell(3).setCellValue(o[3] == null ? "" : o[3].toString());
				seqNo++;
			}
		}
	}
}
