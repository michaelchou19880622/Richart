package com.bcs.web.ui.controller;

import com.bcs.core.bot.report.export.ExportToExcelForContentPushReport;
import com.bcs.core.bot.report.export.ExportToExcelForKeywordReport;
import com.bcs.core.db.service.ActionUserCouponService;
import com.bcs.core.report.builder.ExportExcelBuilder;
import com.bcs.core.report.export.ExportToExcelForCampaignUserList;
import com.bcs.core.report.export.ExportToExcelForCouponReport;
import com.bcs.core.report.export.ExportToExcelForLinkClickReport;
import com.bcs.core.report.export.ExportToExcelForPageVisitReport;
import com.bcs.core.report.export.ExportToExcelForPushApiEffects;
import com.bcs.core.report.export.ExportToExcelForRewardCardReport;
import com.bcs.core.report.export.ExportToExcelForWinnerList;
import com.bcs.core.report.service.ExportService;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.DataUtils;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.web.security.CurrentUser;
import com.bcs.core.web.security.CustomUser;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.MobilePageEnum;
import com.bcs.web.aop.ControllerLog;
import com.bcs.web.ui.service.LoadFileUIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ???, Alan
 */
@Slf4j
@Controller
@RequestMapping("/bcs")
public class BCSExportReportController extends BCSBaseController {
    private static final String DATE_PATTERN = "yyyy-MM-dd-HHmmss";
    private ExportToExcelForContentPushReport exportToExcelForContentPushReport;
    private ExportToExcelForKeywordReport exportToExcelForKeywordReport;
    private ExportToExcelForLinkClickReport exportToExcelForLinkClickReport;
    private ExportToExcelForPageVisitReport exportToExcelForPageVisitReport;
    private ExportToExcelForCouponReport exportToExcelForCouponReport;
    private ExportToExcelForWinnerList exportToExcelForWinnerList;
    private ExportToExcelForCampaignUserList exportToExcelForCampaignUserList;
    private ExportToExcelForRewardCardReport exportToExcelForRewardCardReport;
    private ExportToExcelForPushApiEffects exportToExcelForPushApiEffects;
    private ActionUserCouponService actionUserCouponService;


    @Autowired
    public BCSExportReportController(ExportToExcelForContentPushReport exportToExcelForContentPushReport,
                                     ExportToExcelForKeywordReport exportToExcelForKeywordReport,
                                     ExportToExcelForLinkClickReport exportToExcelForLinkClickReport,
                                     ExportToExcelForPageVisitReport exportToExcelForPageVisitReport,
                                     ExportToExcelForCouponReport exportToExcelForCouponReport,
                                     ExportToExcelForWinnerList exportToExcelForWinnerList,
                                     ExportToExcelForCampaignUserList exportToExcelForCampaignUserList,
                                     ExportToExcelForRewardCardReport exportToExcelForRewardCardReport,
                                     ExportToExcelForPushApiEffects exportToExcelForPushApiEffects,
                                     ActionUserCouponService actionUserCouponService
    ) {
        this.exportToExcelForContentPushReport = exportToExcelForContentPushReport;
        this.exportToExcelForKeywordReport = exportToExcelForKeywordReport;
        this.exportToExcelForLinkClickReport = exportToExcelForLinkClickReport;
        this.exportToExcelForPageVisitReport = exportToExcelForPageVisitReport;
        this.exportToExcelForCouponReport = exportToExcelForCouponReport;
        this.exportToExcelForWinnerList = exportToExcelForWinnerList;
        this.exportToExcelForCampaignUserList = exportToExcelForCampaignUserList;
        this.exportToExcelForRewardCardReport = exportToExcelForRewardCardReport;
        this.exportToExcelForPushApiEffects = exportToExcelForPushApiEffects;
        this.actionUserCouponService = actionUserCouponService;
    }

    /**
     * 匯出 Push Report EXCEL
     */
    @ControllerLog(description = "匯出 Push Report EXCEL")
    @GetMapping("/edit/exportToExcelForPushReport")
    @ResponseBody
    public void exportToExcelForPushReport(HttpServletResponse response, @RequestParam String startDate, @RequestParam String endDate) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "PushReportList_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            exportToExcelForContentPushReport.exportToExcel(filePath, startDate, endDate, fileName);
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出Keyword Report EXCEL
     */
    @ControllerLog(description = "匯出Keyword Report EXCEL")
    @GetMapping("/edit/exportToExcelForKeywordReport")
    @ResponseBody
    public void exportToExcelForKeywordReport(
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser CustomUser customUser,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam Long iMsgId,
            @RequestParam String userStatus
    ) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "KeywordReportList_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            exportToExcelForKeywordReport.exportToExcelForKeywordReport(filePath, fileName, startDate, endDate, iMsgId, userStatus);
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出 Link Click Report EXCEL
     */
    @ControllerLog(description = "匯出 Link Click Report EXCEL")
    @GetMapping("/edit/exportToExcelForLinkClickReport")
    @ResponseBody
    public void exportToExcelForLinkClickReport(
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser CustomUser customUser,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String linkUrl
    ) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "LinkUrlClickReportList_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            exportToExcelForLinkClickReport.exportToExcelForLinkClickReport(filePath, fileName, startDate, endDate, linkUrl);
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出Page Visit Report EXCEL
     */
    @ControllerLog(description = "匯出Page Visit Report EXCEL")
    @GetMapping("/edit/exportToExcelForPageVisitReport")
    @ResponseBody
    public void exportToExcelForPageVisitReport(HttpServletRequest request, HttpServletResponse response, @CurrentUser CustomUser customUser, @RequestParam String startDate, @RequestParam String endDate, @RequestParam String pageUrl) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "PageVisitReportList_" + sdf.format(date) + ".xlsx";
        try {
            MobilePageEnum page = MobilePageEnum.valueOf(pageUrl);

            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            exportToExcelForPageVisitReport.exportToExcelForPageVisitReport(filePath, fileName, startDate, endDate, pageUrl, page.getTitle());
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出 Coupon Report EXCEL
     */
    @ControllerLog(description = "匯出 Coupon Report EXCEL")
    @GetMapping(value = "/edit/exportToExcelForCouponReport")
    @ResponseBody
    public void exportToExcelForCouponReport(HttpServletRequest request, HttpServletResponse response, @CurrentUser CustomUser customUser, @RequestParam String startDate, @RequestParam String endDate, @RequestParam String couponId) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "CouponReportList_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            exportToExcelForCouponReport.exportToExcelForCouponReport(filePath, fileName, startDate, endDate, couponId);
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出 Winner List
     */
    @ControllerLog(description = "匯出 Winner List")
    @GetMapping("/edit/exportToExcelForWinnerList")
    @ResponseBody
    public void exportToExcelForWinnerList(
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser CustomUser customUser,
            @RequestParam String gameId,
            @RequestParam(required = false) Optional<String> couponPrizeId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Optional<Integer> pageIndex
    ) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "WinnerList_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            exportToExcelForWinnerList.exportToExcelForWinnerList(filePath, fileName, startDate, endDate, gameId, couponPrizeId, pageIndex);

        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出 Winner List By CouponId
     */
    @ControllerLog(description = "匯出 Winner List By CouponId")
    @GetMapping("/edit/exportToExcelForWinnerListByCouponId")
    @ResponseBody
    public void exportToExcelForWinnerListByCouponId(
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser CustomUser customUser,
            @RequestParam String couponId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Optional<Integer> pageIndex) throws IOException {
        log.info("exportToExcelForWinnerListByCouponId");
        log.info("couponId:" + couponId);

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "WinnerListByCouponId_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            exportToExcelForWinnerList.exportToExcelForWinnerListByCouponId(filePath, fileName, startDate, endDate, couponId, pageIndex);

        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出Campaign User List
     */
    @ControllerLog(description = "匯出Campaign User List")
    @GetMapping("/edit/exportToExcelForCampaignUserList")
    @ResponseBody
    public void exportToExcelForCampaignUserList(
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser CustomUser customUser,
            @RequestParam String iMsgId,
            @RequestParam String prizeId,
            @RequestParam String startDate,
            @RequestParam String endDate) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "CampaignUserList_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            exportToExcelForCampaignUserList.exportToExcelForCampaignUserList(filePath, fileName, startDate, endDate, iMsgId, prizeId);

        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出單張 RewardCard Record
     */
    @ControllerLog(description = "匯出單張 RewardCard Record")
    @GetMapping("/edit/exportToExcelForRewardRecord")
    @ResponseBody
    public void exportToExcelForRewardRecord(
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser CustomUser customUser,
            @RequestParam String rewardCardId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Optional<Integer> pageIndex) throws IOException {
        log.info("exportToExcelForRewardRecord");
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "RewardCardRecordList_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            exportToExcelForRewardCardReport.exportToExcelForWinnerList(filePath, fileName, startDate, endDate, rewardCardId, pageIndex);
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出此 Reward Card 的各個優惠券紀錄
     */
    @ControllerLog(description = "匯出此 Reward Card 的各個優惠券紀錄")
    @GetMapping("/edit/exportToExcelForRewardCardCouponRecord2")
    @ResponseBody
    public void exportToExcelForRewardCardCouponRecord2(HttpServletRequest request, HttpServletResponse response, @CurrentUser CustomUser customUser, @RequestParam String rewardCardId) {

        ExportExcelBuilder builder = ExportExcelBuilder.createWorkBook()
                .setSheetName("Winner List")
                .setOutputFileName(String.format("RewardCardCouponRecordList_%s.xlsx", DataUtils.convDateToStr(new Date(), DATE_PATTERN)))
                .setOutputPath(String.format("%s%sREPORT", CoreConfigReader.getString("file.path"), System.getProperty("file.separator")))
                .setAllColumnAutoWidth();

        Map<Integer, String> headerMap = new LinkedHashMap<>(8);
        headerMap.put(0, "UID");
        headerMap.put(1, "優惠券");
        headerMap.put(2, "兌獎時間");
        headerMap.put(3, "姓名");
        headerMap.put(4, "身分證");
        headerMap.put(5, "地址");
        headerMap.put(6, "電話");
        headerMap.put(7, "電子序號");
        builder.createHeaderRow().setRowValue(headerMap);

        List<Map<String, String>> resultGet = actionUserCouponService.getCouponUseRecordListByRewardCardId(rewardCardId);

        int i = 1;
        for (Map<String, String> result : resultGet) {
            Map<Integer, String> map = new LinkedHashMap<>(8);
            map.put(0, result.get("UID"));
            map.put(1, result.get("couponTitle"));
            map.put(2, result.get("couponActionTime"));
            map.put(3, result.get("name"));
            map.put(4, result.get("idCardNumber"));
            map.put(5, result.get("address"));
            map.put(6, result.get("phoneNumber"));
            map.put(7, result.get("couponCode"));
            builder.createRow(i).setRowValue(map);
            i++;
        }

        final ExportService exportService = new ExportService();
        exportService.exportExcel(response, builder);
    }

    /**
     * 匯出此 Reward Card 的各個優惠券紀錄
     */
    @ControllerLog(description = "匯出此 Reward Card 的各個優惠券紀錄")
    @GetMapping("/edit/exportToExcelForRewardCardCouponRecord")
    @ResponseBody
    public void exportToExcelForRewardCardCouponRecord(
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser CustomUser customUser,
            @RequestParam String rewardCardId) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "RewardCardCouponRecordList_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            exportToExcelForRewardCardReport.exportToExcelForRewardCardCouponRecord(filePath, fileName, rewardCardId);
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        LoadFileUIService.loadFileToResponse(filePath, fileName, response);
    }

    /**
     * 匯出 Push API 明細報表
     */
    @ControllerLog(description = "匯出 Push API 明細報表")
    @GetMapping("/edit/exportToExcelForPushApiEffectDetail")
    @ResponseBody
    public void exportToExcelForPushApiEffectDetail(HttpServletRequest request, HttpServletResponse response, @CurrentUser CustomUser customUser, @RequestParam String createTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "PushApiEffectDetail_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            exportToExcelForPushApiEffects.exportExcel(filePath, fileName, createTime);
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        try {
            LoadFileUIService.loadFileToResponse(filePath, fileName, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 匯出 Push API 成效報表
     */
    @ControllerLog(description = "匯出 Push API 成效報表")
    @GetMapping("/edit/exportToExcelForPushApiEffects")
    @ResponseBody
    public void exportToExcelForPushApiEffects(HttpServletRequest request, HttpServletResponse response, @CurrentUser CustomUser customUser, @RequestParam String startDate, @RequestParam String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
        Date date = new Date();
        String fileName = "PushApiEffects_" + sdf.format(date) + ".xlsx";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            exportToExcelForPushApiEffects.exportExcel(filePath, fileName, startDate, endDate);
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }

        try {
            LoadFileUIService.loadFileToResponse(filePath, fileName, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
