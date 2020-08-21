package com.bcs.web.ui.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bcs.core.db.entity.ContentFlag;
import com.bcs.core.db.entity.ContentLink;
import com.bcs.core.db.service.ContentFlagService;
import com.bcs.core.db.service.ContentGameService;
import com.bcs.core.db.service.ContentLinkService;
import com.bcs.core.db.service.UserTraceLogService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.EnumClickReportSortType;
import com.bcs.core.enums.LOG_TARGET_ACTION_TYPE;
import com.bcs.core.enums.RECORD_REPORT_TYPE;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.model.GameModel;
import com.bcs.core.report.export.ExportToExcelForLinkClickReport;
import com.bcs.core.report.service.ContentLinkReportService;
import com.bcs.core.report.service.PageVisitReportService;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.resource.UriHelper;
import com.bcs.core.utils.DBResultUtil;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.web.security.CurrentUser;
import com.bcs.core.web.security.CustomUser;
import com.bcs.core.web.ui.controller.BCSBaseController;
import com.bcs.core.web.ui.page.enums.MobilePageEnum;
import com.bcs.web.aop.ControllerLog;
import com.bcs.core.model.LinkClickReportModel;
import com.bcs.web.ui.model.LinkClickReportSearchModel;
import com.bcs.web.ui.model.LinkPageModel;
import com.bcs.web.ui.model.PageVisitReportModel;
import com.bcs.web.ui.service.ExportExcelForLinkPageSrevice;
import com.bcs.web.ui.service.ExportExcelUIService;
import com.bcs.web.ui.service.LoadFileUIService;

@Controller
@RequestMapping("/bcs")
public class BCSLinkPageController extends BCSBaseController {
	@Autowired
	private ContentLinkService contentLinkService;
	@Autowired
	private ContentLinkReportService contentLinkReportService;
	@Autowired
	private PageVisitReportService pageVisitReportService;
	@Autowired
	private UserTraceLogService userTraceLogService;
	@Autowired
	private ExportExcelUIService exportExcelUIService;
	@Autowired
	private ContentGameService contentGameService;
	@Autowired
	private ExportExcelForLinkPageSrevice ExportExcelForLinkPageSrevice;
	@Autowired
	private ContentFlagService contentFlagService;
	@Autowired
	private ExportToExcelForLinkClickReport exportToExcelForLinkClickReport;
	
	/** Logger */
	private static Logger logger = LogManager.getLogger(BCSLinkPageController.class);
	Map<String, LinkClickReportModel> linkResult = new LinkedHashMap<String, LinkClickReportModel>();
	/**
	 * 取得連結列表
	 */
	@ControllerLog(description="取得連結列表")
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getLinkUrlList")
	@ResponseBody
	public ResponseEntity<?> getLinkUrlList(
			HttpServletRequest request, 
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws IOException {
		logger.info("getLinkUrlList");

		try{ 
			List<ContentLink> result = new ArrayList<ContentLink>();
			
			// Add Coupon List Page
			ContentLink couponListPage = new ContentLink();
			couponListPage.setLinkUrl(UriHelper.getCouponListPagePattern());
			couponListPage.setLinkTitle("我的優惠列表");
			result.add(couponListPage);

			// Add Reward Card List Page
			ContentLink rewardCardListPage = new ContentLink();
			rewardCardListPage.setLinkUrl(UriHelper.getRewardCardListPagePattern());
			rewardCardListPage.setLinkTitle("我的集點卡列表");
			result.add(rewardCardListPage);
			
			List<GameModel> games = contentGameService.getAllContentGame();
			for(GameModel game : games){

				ContentLink gamePage = new ContentLink();
				//gamePage.setLinkUrl(UriHelper.getScratchPattern(game.getGameId()));
                gamePage.setLinkUrl(UriHelper.goScratchCardUri() + "/" + game.getGameId());
				gamePage.setLinkTitle("遊戲：" + game.getGameName());
				result.add(gamePage);
			}

			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Map<String, Map<String, Map<String, Long>>> cacheLinkReport = new HashMap<String, Map<String, Map<String, Long>>>();
	
	/**
	 * 使用時間取得連結列表
	 */
	@ControllerLog(description="使用時間取得連結列表")
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getLinkUrlfromTime")
	@ResponseBody
	public ResponseEntity<?> getLinkUrlfromtime(
			HttpServletRequest request, 
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws IOException {
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String pageStr = request.getParameter("page");
		if (startTime != null) {
			startTime += " 00:00:00";
		}
		if (endTime != null) {
			endTime += " 23:59:59";
		}
		logger.info("getLinkUrlfromTime, startTime=" + startTime + " endTime=" + endTime + " page=" + pageStr);
		Calendar yesterdayCalendar = Calendar.getInstance();
		yesterdayCalendar.add(Calendar.DATE, -1);
		Calendar nowCalendar = Calendar.getInstance();
		Calendar nextCalendar = Calendar.getInstance();
		nextCalendar.add(Calendar.DATE, 1);
		Map<String, Object> tracingResult = new HashMap<String, Object>();
		try{ 
			linkResult.clear();
			List<Object[]> result = null; // LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME, LINK_TAG, TRACING_ID
			int page = 0;
            if (StringUtils.isNotBlank(pageStr)) {
                page = Integer.parseInt(pageStr);
            }
//			result = contentLinkService.findAllLinkUrlWithTracingIdByLikeTime(startTime, endTime);
			result = contentLinkService.findAllTracingLinkByDateTime(startTime, endTime);
			String tracingUrlPre = UriHelper.getTracingUrlPre();
            tracingResult.put("TracingUrlPre", tracingUrlPre);
            int rowNum = 0, rowStart = page * 20, rowEnd = rowStart + 20;
            for (Object[] link : result) {
                if (rowNum >= rowStart && rowNum < rowEnd) {
                	/* ref : https://www.java67.com/2015/01/how-to-sort-hashmap-in-java-based-on.html */
                	/* 排序後還是會依照key來排序, 先workaround將key 由tracingLinke改為linkTime(modifyTime) */                	
                    String linkUrl = castToString(link[0]);
                    String linkTitle = castToString(link[1]);
                    String linkId = castToString(link[2]);
                    String linkTime = castToString(link[3]);
                    String linkFlag = castToString(link[4]);
                    String tracingLink = castToString(link[5]);
//                    LinkClickReportModel model = linkResult.get(linkTime);
                    LinkClickReportModel model = linkResult.get(linkId);
                    if (model == null) {
                        model = new LinkClickReportModel();
                        model.setLinkUrl(linkUrl);
                        model.setLinkId(linkId);
                        model.setLinkTitle(linkTitle);
                        model.setLinkTime(linkTime);
                        model.setLinkFlag(linkFlag);
                        model.setTracingLink(tracingLink);
//                        linkResult.put(linkTime, model);
                        linkResult.put(linkId, model);
                    } else {
                        if (StringUtils.isBlank(model.getLinkTitle())) {
                            model.setLinkTitle(linkTitle);
                        }
                        if (StringUtils.isBlank(model.getLinkUrl())) {
                            model.setLinkUrl(linkUrl);
                        }
                    }
                }
                rowNum++;
            }
			
			// Get ContentFlag, setLinkClickCount
			for(LinkClickReportModel model : linkResult.values()){				
				List<String> flags = contentFlagService.findFlagValueByReferenceIdAndContentTypeOrderByFlagValueAsc(model.getLinkId(), ContentFlag.CONTENT_TYPE_LINK);
				model.addFlags(flags);
				Thread.sleep(10);
				// setLinkClickCount
				this.setLinkClickCount(model, nowCalendar, yesterdayCalendar, nextCalendar);
			}
			tracingResult.put("ContentLinkTracingList", linkResult);
            logger.info("getLinkUrlfromTime, tracingUrlPre=" + tracingUrlPre + " sizeOfList=" + linkResult.size());
            return new ResponseEntity<>(tracingResult, HttpStatus.OK);
		}
		catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			logger.info( "getLinkUrlfromtime : " + ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException){
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			}
			else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	/**
	 * 取得連結列表
	 */
	
	@ControllerLog(description="取得連結列表")
	@RequestMapping(method = RequestMethod.POST, value = "/edit/getLinkUrlReportList" ,  consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getLinkUrlReportList(
			HttpServletRequest request, 
			HttpServletResponse response,
			@CurrentUser CustomUser customUser,
			@RequestBody LinkPageModel linkPageModel) throws IOException {
		String queryFlag = null;
		if (linkPageModel.getFlag() != null) {
		    queryFlag = new String(linkPageModel.getFlag().getBytes("utf-8"),"utf-8");
		}
		logger.info("getLinkUrlReportList, queryFlag=" + linkPageModel.getFlag() + " queryFlagUTF8=" + queryFlag + " page=" + linkPageModel.getPage());
        Calendar yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.add(Calendar.DATE, -1);
        Calendar nowCalendar = Calendar.getInstance();
        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.add(Calendar.DATE, 1);
        Map<String, Object> tracingResult = new HashMap<String, Object>();
        try {
        	linkResult.clear();
            List<Object[]> result = null; // LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME, LINK_TAG, TRACING_ID
            int page = 0;
            if (StringUtils.isNotBlank(linkPageModel.getPage())) {
                page = Integer.parseInt(linkPageModel.getPage());
            }
            if (StringUtils.isNotBlank(queryFlag)) {
//                result = contentLinkService.findAllLinkUrlWithTracingIdByLikeTag("%" + queryFlag + "%");
                result = contentLinkService.findAllTracingLinkByLikeTag(queryFlag);
            } else {
//                result = contentLinkService.findAllWithTracingId();
                result = contentLinkService.findAllTracingLink();
            }
            String tracingUrlPre = UriHelper.getTracingUrlPre();
            tracingResult.put("TracingUrlPre", tracingUrlPre);
            int rowNum = 0, rowStart = page * 20, rowEnd = rowStart + 20;
            for (Object[] link : result) {
                if (rowNum >= rowStart && rowNum < rowEnd) {
                	/* ref : https://www.java67.com/2015/01/how-to-sort-hashmap-in-java-based-on.html */
                	/* 排序後還是會依照key來排序, 先workaround將key 由tracingLinke改為linkTime(modifyTime) */
                    String linkUrl = castToString(link[0]);
                    String linkTitle = castToString(link[1]);
                    String linkId = castToString(link[2]);
                    String linkTime = castToString(link[3]);
                    String linkFlag = castToString(link[4]);
                    String tracingLink = castToString(link[5]);
                    
//                    LinkClickReportModel model = linkResult.get(linkTime);
                    LinkClickReportModel model = linkResult.get(linkId);
                    logger.info("LinkClickReportModel = {}", model);
                    
                    if (model == null) {
                        model = new LinkClickReportModel();
                        model.setLinkUrl(linkUrl);
                        model.setLinkId(linkId);
                        model.setLinkTitle(linkTitle);
                        model.setLinkTime(linkTime);
                        model.setLinkFlag(linkFlag);
                        model.setTracingLink(tracingLink);
//                        linkResult.put(linkTime, model);
                        linkResult.put(linkId, model);
                    } else {
                        if (StringUtils.isBlank(model.getLinkTitle())) {
                            model.setLinkTitle(linkTitle);
                        }
                        if (StringUtils.isBlank(model.getLinkUrl())) {
                            model.setLinkUrl(linkUrl);
                        }
                    }
                }
                rowNum++;
            }

            logger.info("linkResult = {}", linkResult);
            
            // Get ContentFlag, setLinkClickCount
            for (LinkClickReportModel model : linkResult.values()) {
                List<String> flags = contentFlagService.findFlagValueByReferenceIdAndContentTypeOrderByFlagValueAsc(model.getLinkId(), ContentFlag.CONTENT_TYPE_LINK);
                model.addFlags(flags);
                Thread.sleep(10);
                // setLinkClickCount
                this.setLinkClickCount(model, nowCalendar, yesterdayCalendar, nextCalendar);
            }
            tracingResult.put("ContentLinkTracingList", linkResult);
            logger.info("getLinkUrlReportList, tracingUrlPre=" + tracingUrlPre + " sizeOfList=" + linkResult.size());
            return new ResponseEntity<>(tracingResult, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(ErrorRecord.recordError(e));
            if(e instanceof BcsNoticeException){
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			}
			else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
        }
    }
	
	private String castToString(Object obj){
		if(obj != null){
			return obj.toString();
		}
		return "";
	}
	
	private void setLinkClickCount(LinkClickReportModel model, Calendar nowCalendar, Calendar yesterdayCalendar, Calendar nextCalendar) throws Exception{
		logger.info("BEFORE : model = {}", model.toJsonString());
		
		logger.info("tracingLink = {}", model.getTracingLink());
		logger.info("linkId = {}", model.getLinkId());
		logger.info("linkUrl = {}", model.getLinkUrl());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		logger.info("nowCalendar = {}", sdf.format(nowCalendar.getTime()));
		logger.info("yesterdayCalendar = {}", sdf.format(yesterdayCalendar.getTime()));
		logger.info("nextCalendar = {}", sdf.format(nextCalendar.getTime()));
		
		String systemStartDate = CoreConfigReader.getString(CONFIG_STR.SYSTEM_START_DATE); // 2017-04-01
		logger.info("systemStartDate = {}", systemStartDate);
		
//		String nowDateKey = model.getLinkUrl() + sdf.format(nowCalendar.getTime());
		String nowDateKey = model.getLinkId() + sdf.format(nowCalendar.getTime());
		logger.info("nowDateKey = {}", nowDateKey);
		
//		logger.info("nowDateKey=" + nowDateKey + ", tracingLink=" + model.getTracingLink() + ", linkId=" + model.getLinkId() + ", linkUrl=" + model.getLinkUrl() + ", systemStartDate=" + systemStartDate + ", yesterdayCalendarTime=" + sdf.format(yesterdayCalendar.getTime()));
		
		// Get Link Click Count
		Map<String, Map<String, Long>> mapResult = cacheLinkReport.get(nowDateKey);
		if(mapResult == null){
			logger.info("1-1 mapResult == null");
//			mapResult = contentLinkReportService.getLinkUrlReport(systemStartDate, sdf.format(yesterdayCalendar.getTime()), model.getLinkUrl());
			mapResult = contentLinkReportService.getLinkUrlReportByLinkUrlAndLinkId(systemStartDate, sdf.format(yesterdayCalendar.getTime()), model.getLinkUrl(), model.getLinkId());
			logger.info("1-2 mapResult = {}", mapResult);
			cacheLinkReport.put(nowDateKey, mapResult);
		}
		logger.info("1-1 cacheLinkReport = {}", cacheLinkReport);
		
		// Get From Cache
		AtomicLong totalCount = new AtomicLong(0);
		AtomicLong userCount = new AtomicLong(0);
		if(mapResult != null){
			for (Entry<String, Map<String, Long>> entry : mapResult.entrySet()) {
//				String keyDateTime = entry.getKey();
				Map<String, Long> dataMap = entry.getValue();
				totalCount.addAndGet(dataMap.get(RECORD_REPORT_TYPE.DATA_TYPE_LINK_COUNT.toString()));
				userCount.addAndGet(dataMap.get(RECORD_REPORT_TYPE.DATA_TYPE_LINK_DISTINCT_COUNT.toString()));
//				logger.info("datetime = {}, totalCount = {}, userCount = {}, dataMap = {}", keyDateTime, totalCount, userCount, dataMap);
			}

			logger.info("Get and count from cache : totalCount = {}, userCount = {}", totalCount, userCount);
			
//			for(Map<String, Long> dataMap : mapResult.values()){
//				totalCount.addAndGet(dataMap.get(RECORD_REPORT_TYPE.DATA_TYPE_LINK_COUNT.toString()));
//				userCount.addAndGet(dataMap.get(RECORD_REPORT_TYPE.DATA_TYPE_LINK_DISTINCT_COUNT.toString()));
//				logger.info("datetime = {}, totalCount = {}, userCount = {}, dataMap = {}", mapResult.entrySet(), totalCount, userCount, dataMap);
//			}
		}
		
		// Get Click Count Today
		List<Object[]> list = contentLinkService.countClickCountByLinkUrlAndLinkIdAndTime(model.getLinkUrl(), sdf.format(nowCalendar.getTime()), sdf.format(nextCalendar.getTime()), model.getLinkId());
		logger.info("list = {}", list);
		
		if(list != null){
			for(Object[] objArray : list){
				totalCount.addAndGet(DBResultUtil.caseCountResult(objArray[0], false).longValue());
				userCount.addAndGet(DBResultUtil.caseCountResult(objArray[1], false).longValue());
//				logger.info("totalCount = {}, userCount = {}", totalCount, userCount);
			}

			logger.info("Get and count final : totalCount = {}, userCount = {}", totalCount, userCount);
		}		
		model.setTotalCount(totalCount.longValue());
		model.setUserCount(userCount.longValue());
		
		logger.info("AFTER : model = {}", model.toJsonString());
		
//		logger.info("linkId=" + model.getLinkId() + " totalCount=" + totalCount.longValue() + " userCount=" + userCount.longValue());
	}
	
	private Map<String, Map<String, Map<String, Long>>> cachePageVisitReport = new HashMap<String, Map<String, Map<String, Long>>>();
	/**
	 * 取得頁面訪問列表
	 */
	@ControllerLog(description="取得頁面訪問列表")
	@RequestMapping(method = RequestMethod.GET, value = "/edit/getPageVisitReportList")
	@ResponseBody
	public ResponseEntity<?> getPageVisitReportList(
			HttpServletRequest request, 
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws IOException {
		logger.info("getPageVisitReportList");

		Calendar yesterdayCalendar = Calendar.getInstance();
		yesterdayCalendar.add(Calendar.DATE, -1);
		
		Calendar nowCalendar = Calendar.getInstance();
		
		Calendar nextCalendar = Calendar.getInstance();
		nextCalendar.add(Calendar.DATE, 1);
		
		try{ 
			Map<String, PageVisitReportModel> pageResult = new LinkedHashMap<String, PageVisitReportModel>();
			
			for(MobilePageEnum page : MobilePageEnum.values()){
				String pageUrl = page.getName();
				String pageTitle = page.getTitle();
				
				PageVisitReportModel model = new PageVisitReportModel();
				model.setPageUrl(pageUrl);
				model.setPageTitle(pageTitle);
				
				pageResult.put(pageUrl, model);
				
				Thread.sleep(10);

				// setLinkClickCount
				this.setPageVisitCount(model, nowCalendar, yesterdayCalendar, nextCalendar);
			}

			return new ResponseEntity<>(pageResult, HttpStatus.OK);
		}
		catch(Exception e){
			logger.error(ErrorRecord.recordError(e));

			if(e instanceof BcsNoticeException){
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			}
			else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	private void setPageVisitCount(PageVisitReportModel model, Calendar nowCalendar, Calendar yesterdayCalendar, Calendar nextCalendar) throws Exception{

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String systemStartDate = CoreConfigReader.getString(CONFIG_STR.SYSTEM_START_DATE);
		
		String nowDateKey = model.getPageUrl() + sdf.format(nowCalendar.getTime());
		logger.info("nowDateKey:" + nowDateKey);
		
		// Get Link Click Count
		Map<String, Map<String, Long>> mapResult = cacheLinkReport.get(nowDateKey);
		
		if(mapResult == null){
			mapResult = pageVisitReportService.getPageVisitReport(systemStartDate, sdf.format(yesterdayCalendar.getTime()), model.getPageUrl());
			cachePageVisitReport.put(nowDateKey, mapResult);
		}

		// Get From Cache
		AtomicLong totalCount = new AtomicLong(0);
		AtomicLong userCount = new AtomicLong(0);
		if(mapResult != null){
			for(Map<String, Long> dataMap : mapResult.values()){
				totalCount.addAndGet(dataMap.get(RECORD_REPORT_TYPE.DATA_TYPE_PAGE_COUNT.toString()));
				userCount.addAndGet(dataMap.get(RECORD_REPORT_TYPE.DATA_TYPE_PAGE_DISTINCT_COUNT.toString()));
			}
		}
		
		// Get Click Count Today
		List<Object[]> list = userTraceLogService.countByReferenceIdAndTime(LOG_TARGET_ACTION_TYPE.TARGET_MobilePage.toString(), 
																								LOG_TARGET_ACTION_TYPE.ACTION_VisitPage.toString(), 
																								model.getPageUrl(), 
																								sdf.format(nowCalendar.getTime()), 
																								sdf.format(nextCalendar.getTime()));

		if(list != null){
			for(Object[] objArray : list){
				totalCount.addAndGet(DBResultUtil.caseCountResult(objArray[0], false).longValue());
				userCount.addAndGet(DBResultUtil.caseCountResult(objArray[1], false).longValue());
			}
		}
		
		model.setTotalCount(totalCount.longValue());
		model.setUserCount(userCount.longValue());
	}
	
	@ControllerLog(description="countLinkUrlList")
	@RequestMapping(method = RequestMethod.GET, value = "/admin/countLinkUrlList")
	@ResponseBody
	public ResponseEntity<?> countLinkUrlList(
			HttpServletRequest request,
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws Exception {
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String linkUrl = request.getParameter("linkUrl");
		logger.info("countLinkUrlList, startDate=" + startDate + " endDate=" + endDate + " linkUrl=" + linkUrl);
		
		try {
			if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
				Map<String, Map<String, Long>> result = contentLinkReportService.getLinkUrlReport(startDate, endDate, linkUrl);
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				if (StringUtils.isBlank(startDate)) {
					logger.error("startDate null");
					throw new BcsNoticeException("缺少查詢起始日期");
				} else {
					logger.error("endDate null");
					throw new BcsNoticeException("缺少查詢結束日期");
				}
			}
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
			
			if(e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@ControllerLog(description="countLinkUrlList")
	@RequestMapping(method = RequestMethod.GET, value = "/admin/countLinkUrlListWithLinkId")
	@ResponseBody
	public ResponseEntity<?> countLinkUrlListWithLinkId(
			HttpServletRequest request,
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws Exception {
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String linkUrl = request.getParameter("linkUrl");
		String linkId = request.getParameter("linkId");
		logger.info("countLinkUrlList, startDate=" + startDate + " endDate=" + endDate + " linkUrl=" + linkUrl + " linkId=" + linkId);
		
		try {
			if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
//				Map<String, Map<String, Long>> result = contentLinkReportService.getLinkUrlReport(startDate, endDate, linkUrl);
				Map<String, Map<String, Long>> result = contentLinkReportService.getLinkUrlReportByLinkUrlAndLinkId(startDate, endDate, linkUrl, linkId);
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				if (StringUtils.isBlank(startDate)) {
					logger.error("startDate null");
					throw new BcsNoticeException("缺少查詢起始日期");
				} else {
					logger.error("endDate null");
					throw new BcsNoticeException("缺少查詢結束日期");
				}
			}
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
			
			if(e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@ControllerLog(description="countPageVisitList")
	@RequestMapping(method = RequestMethod.GET, value = "/admin/countPageVisitList")
	@ResponseBody
	public ResponseEntity<?> countPageVisitList(
			HttpServletRequest request,
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws Exception {
		logger.info("countPageVisitList");
		
		try {
			String pageUrl = request.getParameter("pageUrl");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			
			MobilePageEnum.valueOf(pageUrl);
			
			if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
				Map<String, Map<String, Long>> result = pageVisitReportService.getPageVisitReport(startDate, endDate, pageUrl);
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				if (StringUtils.isBlank(startDate)) {
					logger.error("startDate null");
					throw new BcsNoticeException("缺少查詢起始日期");
				} else {
					logger.error("endDate null");
					throw new BcsNoticeException("缺少查詢結束日期");
				}
			}
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
			
			if(e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	@ControllerLog(description="exportMidForLinkClickReport")
	@RequestMapping(method = RequestMethod.GET, value = "/edit/exportMidForLinkClickReport")
	@ResponseBody
	public void exportMidForLinkClickReport(
			HttpServletRequest request,
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws Exception {
		logger.info("exportMidForLinkClickReport");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");

		String linkUrl = request.getParameter("linkUrl");
		String linkId = request.getParameter("linkId");
		
		 if(StringUtils.isNotBlank(linkUrl)){
				logger.info("linkUrl:" + linkUrl);
				
//				List<ContentLink> list = contentLinkService.findByLinkUrl(linkUrl);
				List<ContentLink> list = contentLinkService.findByLinkId(linkId);
				logger.info("list = {}", list);
				
				if(list == null || list.size() == 0){
					throw new Exception("linkUrl Error");
				}
				
				if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date timeStart = sdf.parse(startDate);
					
					Date timeEnd = sdf.parse(endDate);
					Calendar calendarEnd = Calendar.getInstance();
					calendarEnd.setTime(timeEnd);
					calendarEnd.add(Calendar.DATE, 1);
				
					String title = "";
					for(ContentLink link : list){
						if(StringUtils.isBlank(title)){
							title = link.getLinkTitle();
						}
					}
				
//					List<String> clickLinkMids = contentLinkService.findClickMidByLinkUrlAndTime(linkUrl, sdf.format(timeStart), sdf.format(calendarEnd.getTime()));
					List<String> clickLinkMids = contentLinkService.findClickMidByLinkUrlAndTimeAndLinkId(linkUrl, sdf.format(timeStart), sdf.format(calendarEnd.getTime()), linkId);
					
					if(clickLinkMids != null){
						
						List<String> titles = new ArrayList<String>();
						titles.add("點擊人UID");
						List<List<String>> data = new ArrayList<List<String>>();
						data.add(clickLinkMids);
		
						String time = sdf.format(timeStart) + "~" + sdf.format(calendarEnd.getTime()) ;
						exportExcelUIService.exportMidResultToExcel(request, response, "ClickUrlMid", "點擊連結:" + title , time, titles, data);
						return;
					}
				}
		 }
		 throw new Exception("資料產生錯誤");
	}
	
	
	@ControllerLog(description="匯出 成效畫面列表 EXCEL")
	@RequestMapping(method = RequestMethod.GET, value = "edit/exportToExcelForInterface")
	@ResponseBody
	public void exportToExcelForInterface(
			HttpServletRequest request, 
			HttpServletResponse response,
			@CurrentUser CustomUser customUser
			) throws IOException{
		logger.info("----------- exportToExcelForInterface -----------");
		//Map<String, LinkClickReportModel> linkResult = new LinkedHashMap<String, LinkClickReportModel>();
		ExportExcelForLinkPageSrevice.exportExcelForInterface(request,response,linkResult);
		
	}
	
	@ControllerLog(description="匯出 成效畫面列表 總表")
	@RequestMapping(method = RequestMethod.GET, value = "edit/exportToExcelForSummaryUid")
	@ResponseBody
	public void exportToExcelForSummaryUid(
			HttpServletRequest request, 
			HttpServletResponse response,
			@CurrentUser CustomUser customUser
			) throws IOException{
		logger.info("---------- exportToExcelForSummaryUid ------------");
		
		ExportExcelForLinkPageSrevice.exportExcelForSummary(request,response,linkResult);
		
	}
	
	/**
	 * 取得連結列表
	 */
	@ControllerLog(description="取得連結列表-新版本")
	@RequestMapping(method = RequestMethod.POST, value = "/edit/getLinkClickReportListNew", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getLinkClickReportListNew(
			HttpServletRequest request, 
			HttpServletResponse response,
			@CurrentUser CustomUser customUser,
			@RequestBody LinkClickReportSearchModel linkClickReportSearchModel) throws IOException {
		String queryFlag = linkClickReportSearchModel.getQueryFlag() == null ? "" : new String(linkClickReportSearchModel.getQueryFlag().getBytes("utf-8"),"utf-8");
		Integer page = linkClickReportSearchModel.getPage() == null ? 0 : linkClickReportSearchModel.getPage();
		int pageSize = linkClickReportSearchModel.getPageSize() == null ? 20 : linkClickReportSearchModel.getPageSize();
		String startDate = linkClickReportSearchModel.getStartDate();
		String endDate = linkClickReportSearchModel.getEndDate();
		String dataStartDate = linkClickReportSearchModel.getDataStartDate();
		String dataEndDate = linkClickReportSearchModel.getDataEndDate();
		String orderBy = linkClickReportSearchModel.getOrderBy();
		logger.info("getLinkClickReportListNew start, queryFlag=" + queryFlag + " page=" + page + " pageSize=" + pageSize + " startDate=" + startDate + " endDate=" + endDate + " dataStartDate=" + dataStartDate + " dataEndDate=" + dataEndDate + " orderBy=" + orderBy);
		try{
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (endDate == null) {
				endDate = sdf.format(calendar.getTime());
			}	
			if (startDate == null) {
				calendar.add(Calendar.DATE, -7);
				startDate = sdf.format(calendar.getTime());
			}
			if (dataEndDate == null) {
				dataEndDate = sdf.format(calendar.getTime());
			}	
			if (dataStartDate == null) {
				calendar.add(Calendar.DATE, -7);
				dataStartDate = sdf.format(calendar.getTime());
			}
			int orderByID = EnumClickReportSortType.ByTracingID.getValue();
			if(StringUtils.isNotBlank(orderBy)){
				orderByID = Integer.parseInt(orderBy);
			}
			// TRACING_ID, LINK_ID, LINK_TITLE, LINK_URL, MODIFY_TIME, LINK_TAG, CLICK_COUNT, USER_COUNT
			List<Object[]> result = contentLinkService.findListByModifyDateAndFlag(startDate, endDate, dataStartDate, dataEndDate, queryFlag, orderByID, page * pageSize + 1, pageSize);
			Map<String, Object> tracingResult = new HashMap<String, Object>();
			Map<String, LinkClickReportModel> linkResult = new LinkedHashMap<String, LinkClickReportModel>();
			String tracingUrlPre = UriHelper.getTracingUrlPre();
            tracingResult.put("TracingUrlPre", tracingUrlPre);
			for(Object[] data : result){
				String tracingId = castToString(data[0]);
				String linkId = castToString(data[1]);
				String linkTitle = castToString(data[2]);
				String linkUrl = castToString(data[3]);
				String linkTime = castToString(data[4]);
				String totalCount = castToString(data[6]);
				String userCount = castToString(data[7]);
				LinkClickReportModel model = new LinkClickReportModel();
				model.setTracingLink(tracingId);
				model.setLinkUrl(linkUrl);
				model.setLinkId(linkId);
				model.setLinkTitle(linkTitle);
				model.setLinkTime(linkTime);
				model.setTotalCount(StringUtils.isBlank(totalCount) ? 0 : Long.parseLong(totalCount));
				model.setUserCount(StringUtils.isBlank(userCount) ? 0 : Long.parseLong(userCount));
				model.addFlags(contentFlagService.findFlagValueByReferenceIdAndContentTypeOrderByFlagValueAsc(linkId, "LINK"));
				linkResult.put(linkId, model);
			}
			tracingResult.put("ContentLinkTracingList", linkResult);
			logger.info("getLinkClickReportListNew end, queryFlag=" + queryFlag + " page=" + page + " pageSize=" + pageSize + " startDate=" + startDate + " endDate=" + endDate + " dataStartDate=" + dataStartDate + " dataEndDate=" + dataEndDate + " tracingUrlPre=" + tracingUrlPre + " linkResultSize=" + (linkResult == null ? 0 : linkResult.size()));
			return new ResponseEntity<>(tracingResult, HttpStatus.OK);
		}
		catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException){
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			}
			else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	/**
	 * 匯出連結列表到Excel
	 */
	@ControllerLog(description="匯出連結列表到Excel-新版本")
	@RequestMapping(method = RequestMethod.POST, value = "/edit/exportLinkClickReportListNew")
	@ResponseBody
	public void exportLinkClickReportListNew(
			HttpServletRequest request, 
			HttpServletResponse response,
			@CurrentUser CustomUser customUser,
			@RequestBody LinkClickReportSearchModel linkClickReportSearchModel) throws IOException {
		String queryFlag = linkClickReportSearchModel.getQueryFlag() == null ? "" : new String(linkClickReportSearchModel.getQueryFlag().getBytes(StandardCharsets.UTF_8.name()), StandardCharsets.UTF_8.name());
		String startDate = linkClickReportSearchModel.getStartDate();
		String endDate = linkClickReportSearchModel.getEndDate();
		String dataStartDate = linkClickReportSearchModel.getDataStartDate();
		String dataEndDate = linkClickReportSearchModel.getDataEndDate();
		String orderBy = linkClickReportSearchModel.getOrderBy();
		logger.info("exportLinkClickReportListNew start, queryFlag=" + queryFlag + " startDate=" + startDate + " endDate=" + endDate + " dataStartDate=" + dataStartDate + " dataEndDate=" + dataEndDate + " orderBy=" + orderBy);
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (endDate == null) {
				endDate = sdf.format(calendar.getTime());
			}	
			if (startDate == null) {
				calendar.add(Calendar.DATE, -7);
				startDate = sdf.format(calendar.getTime());
			}
			if (dataEndDate == null) {
				dataEndDate = sdf.format(calendar.getTime());
			}	
			if (dataStartDate == null) {
				calendar.add(Calendar.DATE, -7);
				dataStartDate = sdf.format(calendar.getTime());
			}
			int orderByID = EnumClickReportSortType.ByTracingID.getValue();
			if(StringUtils.isNotBlank(orderBy)){
				orderByID = Integer.parseInt(orderBy);
			}
			String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
			Date date = new Date();
			String fileName = "LinkClickReportList_" + sdf.format(date) + ".xlsx";
			File folder = new File(filePath);
			if(!folder.exists()){
				folder.mkdirs();
			}
			exportToExcelForLinkClickReport.exportLinkClickReportListNew(filePath, fileName, startDate, endDate, dataStartDate, dataEndDate, queryFlag, orderByID);
			LoadFileUIService.loadFileToResponse(filePath, fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", response);
			logger.info("exportLinkClickReportListNew end, queryFlag=" + queryFlag + " startDate=" + startDate + " endDate=" + endDate + " dataStartDate=" + dataStartDate + " dataEndDate=" + dataEndDate + " orderBy=" + orderBy + " filePaht=" + filePath + " fileName=" + fileName);
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
		}
	}
	
	@ControllerLog(description="countLinkIdList")
	@RequestMapping(method = RequestMethod.GET, value = "/admin/countLinkIdList")
	@ResponseBody
	public ResponseEntity<?> countLinkIdList(
			HttpServletRequest request,
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws Exception {
		String linkId = request.getParameter("linkId");
		String linkUrl = request.getParameter("linkUrl");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		logger.info("countLinkIdList start, linkId=" + linkId + " linkUrl=" + linkUrl + " startDate=" + startDate + " endDate=" + endDate);
		try {
			if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
				Map<String, Map<String, Long>> result = contentLinkReportService.getLinkIdReportNew(startDate, endDate, linkId);
				logger.info("countLinkIdList end, linkId=" + linkId + " linkUrl=" + linkUrl + " startDate=" + startDate + " endDate=" + endDate + " listSize=" + (result == null ? 0 : result.size()));
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				if (StringUtils.isBlank(startDate)) {
					logger.error("countLinkIdList end, no start date");
					throw new BcsNoticeException("缺少查詢起始日期");
				} else {
					logger.error("countLinkIdList end, no start date");
					throw new BcsNoticeException("缺少查詢結束日期");
				}
			}
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
			if(e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else{
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	/**
	 * 匯出 Link Click Report EXCEL
	 */
	@ControllerLog(description="匯出 Link Click Report EXCEL")
	@RequestMapping(method = RequestMethod.GET, value = "/edit/exportToExcelForLinkClickReportNew")
	@ResponseBody
	public void exportToExcelForLinkClickReportNew(
			HttpServletRequest request, 
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws IOException{
		String linkId = request.getParameter("linkId");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String linkUrl = request.getParameter("linkUrl");
		logger.info("exportToExcelForLinkClickReportNew start, linkId=" + linkId + " linkUrl=" + linkUrl + " startDate=" + startDate + " endDate=" + endDate);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
			String filePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "REPORT";
			Date date = new Date();
			String fileName = "LinkUrlClickReportList_" + sdf.format(date) + ".xlsx";
			File folder = new File(filePath);
			if(!folder.exists()){
				folder.mkdirs();
			}
			exportToExcelForLinkClickReport.exportToExcelForLinkClickReportNew(filePath, fileName, startDate, endDate, linkId, linkUrl);
			LoadFileUIService.loadFileToResponse(filePath, fileName, response);
			logger.info("exportToExcelForLinkClickReportNew end, linkId=" + linkId + " linkUrl=" + linkUrl + " startDate=" + startDate + " endDate=" + endDate + " filePaht=" + filePath + " fileName=" + fileName);
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
		}
	}
	
	@ControllerLog(description="exportMidForLinkClickReportNew")
	@RequestMapping(method = RequestMethod.GET, value = "/edit/exportMidForLinkClickReportNew")
	@ResponseBody
	public void exportMidForLinkClickReportNew(
			HttpServletRequest request,
			HttpServletResponse response,
			@CurrentUser CustomUser customUser) throws Exception {
		String linkId = request.getParameter("linkId");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String linkUrl = request.getParameter("linkUrl");
		logger.info("exportMidForLinkClickReport start, linkId=" + linkId + " linkUrl=" + linkUrl + " startDate=" + startDate + " endDate=" + endDate);
		try {
		    if(StringUtils.isNotBlank(linkUrl)){
			    ContentLink contentLink = contentLinkService.findOne(linkId);
			    if(contentLink == null){
				    throw new Exception("linkId Error");
			    }				
			    if(StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)){
				    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				    Date timeStart = sdf.parse(startDate);					
				    Date timeEnd = sdf.parse(endDate);
				    Calendar calendarEnd = Calendar.getInstance();
				    calendarEnd.setTime(timeEnd);
				    String title = contentLink.getLinkTitle();
				    List<String> clickLinkMids = contentLinkService.findClickMidByLinkIdAndTime(linkId, sdf.format(timeStart), sdf.format(calendarEnd.getTime()));
				    if(clickLinkMids != null){						
					    List<String> titles = new ArrayList<String>();
					    titles.add("點擊人UID");
					    List<List<String>> data = new ArrayList<List<String>>();
					    data.add(clickLinkMids);	
					    String time = sdf.format(timeStart) + "~" + sdf.format(calendarEnd.getTime()) ;
					    exportExcelUIService.exportMidResultToExcel(request, response, "ClickUrlMid", "點擊連結:" + title , time, titles, data);
					    logger.info("exportMidForLinkClickReport end, linkId=" + linkId + " linkUrl=" + linkUrl + " startDate=" + startDate + " endDate=" + endDate + " numOfMID=" + clickLinkMids.size());
					    return;
				    }
				    logger.info("exportMidForLinkClickReport end, linkId=" + linkId + " linkUrl=" + linkUrl + " startDate=" + startDate + " endDate=" + endDate);
			    }
		     }
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));
		}
		throw new Exception("資料產生錯誤");
	}
}
