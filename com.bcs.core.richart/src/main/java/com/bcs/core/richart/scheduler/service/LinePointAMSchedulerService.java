package com.bcs.core.richart.scheduler.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.json.JSONArray;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.ShareCampaign;
import com.bcs.core.db.entity.ShareCampaignClickTracing;
import com.bcs.core.db.entity.ShareDonatorRecord;
import com.bcs.core.db.entity.ShareUserRecord;
import com.bcs.core.db.service.ShareCampaignClickTracingService;
import com.bcs.core.db.service.ShareCampaignService;
import com.bcs.core.db.service.ShareDonatorRecordService;
import com.bcs.core.db.service.ShareUserRecordService;
import com.bcs.core.richart.akka.service.LinePointPushAkkaService;
import com.bcs.core.richart.api.model.LinePointPushModel;
import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.richart.db.entity.LinePointScheduledDetail;
import com.bcs.core.richart.db.service.LinePointMainService;
import com.bcs.core.richart.db.service.LinePointScheduledDetailService;

@Service
public class LinePointAMSchedulerService {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(LinePointAMSchedulerService.class);

	@Autowired
	LinePointPushAkkaService linePointPushAkkaService;
	@Autowired
	LinePointMainService linePointMainService;
	@Autowired
	LinePointScheduledDetailService linePointScheduledDetailService;
	@Autowired
	ShareUserRecordService shareUserRecordService;
	@Autowired
	ShareCampaignClickTracingService shareCampaignClickTracingService;
	@Autowired
	ShareCampaignService shareCampaignService;
	@Autowired
	ShareDonatorRecordService shareDonatorRecordService;

	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> scheduledFuture = null;
	
	private static final long INITIAL_DELAY = 60L;
	
	private static final long DELAY = 120L;
	
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

	public LinePointAMSchedulerService() {
	}

	/**
	 * Start Schedule
	 * 
	 * @throws SchedulerException
	 * @throws InterruptedException
	 */
	public void startCircle() throws SchedulerException, InterruptedException {
//		// calculate delay
//		Long delay = 0L;
//		try {
//			// get start time
//			String startTimeStr = CoreConfigReader.getString(CONFIG_STR.LINE_POINT_AM_START_TIME, true);
//			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//			Date startDate = sdf.parse(startTimeStr);
//			Calendar start = Calendar.getInstance();
//			start.setTime(startDate);
//
//			// calculate next trigger time
//			LocalDateTime localNow = LocalDateTime.now();
//			ZonedDateTime now = ZonedDateTime.of(localNow, ZoneId.systemDefault());
//			ZonedDateTime nextTrigger = now.withHour(start.get(Calendar.AM_PM) * 12 + start.get(Calendar.HOUR)).withMinute(start.get(Calendar.MINUTE)).withSecond(start.get(Calendar.SECOND));
//			if (now.compareTo(nextTrigger) > 0) {
//				// trigger at tomorrow
//				nextTrigger = nextTrigger.plusDays(1);
//			}
//
//			// calculate delay
//			Duration duration = Duration.between(now, nextTrigger);
//			delay = duration.getSeconds();
//
//			// write logger
//			logger.info("now: " + now);
//			logger.info("nextTrigger: " + nextTrigger);
//			logger.info("delay: " + delay);
//		} catch (Exception e) {
//			logger.info("startCircle Calucute Delay Error:" + e.getMessage());
//		}
//
//		// run every day
//		scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
//			public void run() {
//				logger.info("");
//				long startTime = System.nanoTime();
//				logger.info("[ LinePointAMSchedulerService ] Check and push line points - START");
//				
//				pushScheduledLinePoint();
//				
//				long endTime = System.nanoTime();
//				logger.info("[ LinePointAMSchedulerService ] Check and push line points - FINISH");
//				
//				logger.info("[ LinePointAMSchedulerService ] Elapsed Time : {} seconds", (endTime - startTime) / 1_000_000_000);
//				logger.info("");
//			}
//		}, delay, 86400, TimeUnit.SECONDS);
		

		scheduledFuture = scheduler.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				logger.debug("--------------------------------------------------------------------");
				logger.debug("Parameters [initialDelay] = {}", INITIAL_DELAY);
				logger.debug("Parameters [delay] = {}", DELAY);
				logger.debug("Time unit of the [initialDelay] and [delay] parameters is : {}", TIME_UNIT.toString());
				
				long startTime = System.nanoTime();
				logger.debug("[ LinePointAMSchedulerService ] Check and push line points - START");

				pushScheduledLinePoint();

				long endTime = System.nanoTime();
				logger.debug("[ LinePointAMSchedulerService ] Check and push line points - FINISH");

				logger.debug("[ LinePointAMSchedulerService ] Elapsed Time : {} seconds", (endTime - startTime) / 1_000_000_000);
				logger.debug("--------------------------------------------------------------------");
			}
		}, INITIAL_DELAY, DELAY, TIME_UNIT);
	}

	/**
	 * Stop Schedule : Wait for Executing Jobs to Finish
	 * 
	 * @throws SchedulerException
	 */
	@PreDestroy
	public void destroy() {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
			logger.debug(" LinePointAMSchedulerService cancel....");
		}
		if (scheduler != null && !scheduler.isShutdown()) {
			logger.debug(" LinePointAMSchedulerService shutdown....");
			scheduler.shutdown();
		}
	}

	public void pushScheduledLinePoint() {
		logger.debug("----------------------------------------");
		
		// get undoneUser
		List<ShareUserRecord> list_ShareUserRecord_undone = shareUserRecordService.findLatelyUndoneUsers();
		logger.debug("list_ShareUserRecord_undone.size() = {}", list_ShareUserRecord_undone.size());
		logger.debug("list_ShareUserRecord_undone = {}", list_ShareUserRecord_undone);

		for (int i = 0; i < list_ShareUserRecord_undone.size(); i++) {
			ShareUserRecord shareUserRecord = list_ShareUserRecord_undone.get(i);
			logger.debug("No.{} shareUserRecord = {}", (i + 1), shareUserRecord);

			// get autoSendPoint & judgment
			ShareCampaign shareCampaign = shareCampaignService.findOne(shareUserRecord.getCampaignId());
			logger.debug("shareCampaign = {}", shareCampaign);
			
			Boolean autoSendPoint = shareCampaign.getAutoSendPoint();
			logger.debug("autoSendPoint = {}", autoSendPoint);
			
			String judgment = shareCampaign.getJudgement();
			logger.debug("judgment = {}", judgment);

			// count
			Long cumulativeCount = 0L;
			
			String shareUserRecordId = shareUserRecord.getShareUserRecordId();
			logger.debug("shareUserRecordId = {}", shareUserRecordId);
			
			List<ShareCampaignClickTracing> list_shareCampaignClickTracing = shareCampaignClickTracingService.findByShareUserRecordIdOrderByModifyTimeAsc(shareUserRecordId);
			logger.debug("list_shareCampaignClickTracing = {}", list_shareCampaignClickTracing);
			
			// Find all relatived shareUserRecord by shareUserRecordId
			for (ShareCampaignClickTracing shareCampaignClickTracing : list_shareCampaignClickTracing) {
				logger.debug("shareCampaignClickTracing = {}", shareCampaignClickTracing);
				
				// Get be shared UID from share campaign click tracing record.
				String beSharedUid = shareCampaignClickTracing.getUid();
				logger.debug("beSharedUid = {}", beSharedUid);
				
				if (judgment.equals(ShareCampaign.JUDGEMENT_DISABLE)) {

					cumulativeCount += 1L;
					logger.debug("cumulativeCount = {}", cumulativeCount);

					// 更新完成活動累積人數紀錄
					shareUserRecord.setCumulativeCount(cumulativeCount);
					shareUserRecordService.save(shareUserRecord);

					long currentCumulativeCount = shareUserRecord.getCumulativeCount();
					logger.debug("完成任務累積人數: " + currentCumulativeCount);
					logger.debug("活動任務要求人數: " + shareCampaign.getShareTimes());
					
					if (currentCumulativeCount < shareCampaign.getShareTimes()) {
						continue;
					}
					
					if (currentCumulativeCount == shareCampaign.getShareTimes()) {
						Date date = new Date();
						
						logger.debug("任務已完成!");
						logger.debug("完成時間 : {}", date);
						logger.debug("活動任務 : {}", shareCampaign.getCampaignId());
						logger.debug("分享紀錄 : {}", shareUserRecord.getShareUserRecordId());
						
						shareUserRecord.setCompleteStatus(ShareUserRecord.COMPLETE_STATUS_DONE);
						shareUserRecord.setDoneTime(date);
						shareUserRecordService.save(shareUserRecord);
					} 
					
					// autoSendPoint
					logger.debug("是否自動發點: {}", autoSendPoint);
					if (autoSendPoint) {
						// linePointMain.status = scheduled
						String linePointSerialId = shareCampaign.getLinePointSerialId();
						LinePointMain linePointMain = linePointMainService.findBySerialId(linePointSerialId);
						linePointMain.setStatus(LinePointMain.STATUS_SCHEDULED);
						linePointMainService.save(linePointMain);

						// linePointScheduledDetail.status = waiting
						LinePointScheduledDetail linePointScheduledDetail = new LinePointScheduledDetail();
						linePointScheduledDetail.setUid(shareUserRecord.getUid());
						linePointScheduledDetail.setLinePointMainId(linePointMain.getId());
						linePointScheduledDetail.setStatus(LinePointScheduledDetail.STATUS_WAITING);
						linePointScheduledDetail.setModifyTime(new Date());
						linePointScheduledDetailService.save(linePointScheduledDetail);
					}
					
				} else {
					// Set stateJudgment depend on judgment type.
					String stateJudgment = "";
					
					if (judgment.equals(ShareCampaign.JUDGEMENT_FOLLOW)) {
						stateJudgment = " and status <> 'BLOCK' and create_Time >= '";
					} else if (judgment.equals(ShareCampaign.JUDGEMENT_BINDED)) {
						stateJudgment = " and isBinded = 'BINDED' and bind_Time >= '";
					} 
					
					stateJudgment = stateJudgment + shareCampaignClickTracing.getSharedTime() + "' ";
					
					// 判斷是否達成任務? (判斷此 UID 在 BCS_LINE_USER 裡面的狀態是否符合?)
					boolean isAchieved = shareUserRecordService.checkJudgment(beSharedUid, stateJudgment);
					logger.debug("isAchieved = {}", isAchieved);
					
					if (isAchieved) {
						cumulativeCount += 1L;
						logger.debug("cumulativeCount = {}", cumulativeCount);
						
						// 更新完成活動累積人數紀錄
						shareUserRecord.setCumulativeCount(cumulativeCount);
						shareUserRecordService.save(shareUserRecord);
						logger.debug("shareUserRecord : {}", shareUserRecord);

						long currentCumulativeCount = shareUserRecord.getCumulativeCount();
						logger.debug("完成任務累積人數: " + currentCumulativeCount);
						logger.debug("活動任務要求人數: " + shareCampaign.getShareTimes());

						// 查看對應用戶UID的貢獻紀錄
						List<ShareDonatorRecord> currentDonators = shareDonatorRecordService.findByDonatorUidAndShareUserRecordId(beSharedUid, shareUserRecordId);
						logger.debug("currentDonators: " + currentDonators);
						
						if (currentDonators.isEmpty()) { // 被分享者UID貢獻紀錄不存在
							// save ShareDonatorRecord
							ShareDonatorRecord shareDonatorRecord = new ShareDonatorRecord();
							shareDonatorRecord.setDonatorUid(beSharedUid);
							shareDonatorRecord.setBenefitedUid(shareUserRecord.getUid());
							shareDonatorRecord.setCampaignId(shareUserRecord.getCampaignId());
							shareDonatorRecord.setModifyTime(new Date());
							shareDonatorRecord.setShareCampaignClickTracingId(shareCampaignClickTracing.getClickTracingId());
							shareDonatorRecord.setShareUserRecordId(shareUserRecord.getShareUserRecordId());
							shareDonatorRecord.setDonateLevel(judgment);
							shareDonatorRecordService.save(shareDonatorRecord);
							logger.debug("shareDonatorRecord = {}", shareDonatorRecord);
							
							if (currentCumulativeCount < shareCampaign.getShareTimes()) {
								continue;
							}
							
							if (currentCumulativeCount == shareCampaign.getShareTimes()) {
								Date date = new Date();
								
								logger.debug("任務已完成!");
								logger.debug("完成時間 : {}", date);
								logger.debug("活動任務 : {}", shareCampaign.getCampaignId());
								logger.debug("分享紀錄 : {}", shareUserRecord.getShareUserRecordId());
								
								shareUserRecord.setCompleteStatus(ShareUserRecord.COMPLETE_STATUS_DONE);
								shareUserRecord.setDoneTime(date);
								shareUserRecordService.save(shareUserRecord);
							}

							// autoSendPoint
							logger.debug("是否自動發點: {}", autoSendPoint);
							if (autoSendPoint) {
								// linePointMain.status = scheduled
								String linePointSerialId = shareCampaign.getLinePointSerialId();
								LinePointMain linePointMain = linePointMainService.findBySerialId(linePointSerialId);
								linePointMain.setStatus(LinePointMain.STATUS_SCHEDULED);
								linePointMainService.save(linePointMain);

								// linePointScheduledDetail.status = waiting
								LinePointScheduledDetail linePointScheduledDetail = new LinePointScheduledDetail();
								linePointScheduledDetail.setUid(shareUserRecord.getUid());
								linePointScheduledDetail.setLinePointMainId(linePointMain.getId());
								linePointScheduledDetail.setStatus(LinePointScheduledDetail.STATUS_WAITING);
								linePointScheduledDetail.setModifyTime(new Date());
								linePointScheduledDetailService.save(linePointScheduledDetail);
							}
							
						} else { // 被分享者UID貢獻紀錄已存在
							
							// 判斷被分享者UID的貢獻紀錄是否小於兩筆? ( 因為最多只能有兩筆 : 一筆的為綁定活動的貢獻、另一筆為加好友活動的貢獻 )
							// 注意，因為綁定與加好友有層級關係，所以 :
							// 1. 如果之前已經貢獻過加好友的活動，可以再貢獻其他人分享的綁定活動。
							// 2. 如果之前已經貢獻過綁定的活動，則無法再貢獻其他人分享的加好友活動。
							if (currentDonators.size() < 2) { 
								
								// 如果只有一筆，則判斷之前貢獻紀錄的狀態是否為"FOLLOW(加好友)" ?
								if (currentDonators.get(0).getDonateLevel().equals("FOLLOW")) {
									
									// 判斷活動判定條件是否為"BINDED(綁定)"? 是的話就再新增一筆(BINDED 綁定的貢獻)
									if (judgment.equals(ShareCampaign.JUDGEMENT_BINDED)) {
										ShareDonatorRecord shareDonatorRecord = new ShareDonatorRecord();
										shareDonatorRecord.setDonatorUid(beSharedUid);
										shareDonatorRecord.setBenefitedUid(shareUserRecord.getUid());
										shareDonatorRecord.setCampaignId(shareUserRecord.getCampaignId());
										shareDonatorRecord.setModifyTime(new Date());
										shareDonatorRecord.setShareCampaignClickTracingId(shareCampaignClickTracing.getClickTracingId());
										shareDonatorRecord.setShareUserRecordId(shareUserRecord.getShareUserRecordId());
										shareDonatorRecord.setDonateLevel(judgment);
										shareDonatorRecordService.save(shareDonatorRecord);
										logger.debug("shareDonatorRecord : {}", shareDonatorRecord);

										if (currentCumulativeCount < shareCampaign.getShareTimes()) {
											continue;
										}
										
										if (currentCumulativeCount == shareCampaign.getShareTimes()) {
											Date date = new Date();
											
											logger.debug("任務已完成!");
											logger.debug("完成時間 : {}", date);
											logger.debug("活動任務 : {}", shareCampaign.getCampaignId());
											logger.debug("分享紀錄 : {}", shareUserRecord.getShareUserRecordId());
											
											shareUserRecord.setCompleteStatus(ShareUserRecord.COMPLETE_STATUS_DONE);
											shareUserRecord.setDoneTime(date);
											shareUserRecordService.save(shareUserRecord);
										}

										// autoSendPoint
										logger.debug("是否自動發點: {}", autoSendPoint);
										if (autoSendPoint) {
											// linePointMain.status = scheduled
											String linePointSerialId = shareCampaign.getLinePointSerialId();
											LinePointMain linePointMain = linePointMainService.findBySerialId(linePointSerialId);
											linePointMain.setStatus(LinePointMain.STATUS_SCHEDULED);
											linePointMainService.save(linePointMain);

											// linePointScheduledDetail.status = waiting
											LinePointScheduledDetail linePointScheduledDetail = new LinePointScheduledDetail();
											linePointScheduledDetail.setUid(shareUserRecord.getUid());
											linePointScheduledDetail.setLinePointMainId(linePointMain.getId());
											linePointScheduledDetail.setStatus(LinePointScheduledDetail.STATUS_WAITING);
											linePointScheduledDetail.setModifyTime(new Date());
											linePointScheduledDetailService.save(linePointScheduledDetail);
										}
									}
								}
							}
							
							logger.debug("被分享者 {} 已無法再貢獻。", beSharedUid);
						}
					}
				}
			}
		}

		logger.debug("----------------------------------------");
		// find linePointMain.status = scheduled
		List<LinePointMain> list_LinePointMain = linePointMainService.findByStatus(LinePointMain.STATUS_SCHEDULED);
		logger.debug("list_LinePointMain.size() = {}", list_LinePointMain.size());
		logger.debug("list_LinePointMain = {}", list_LinePointMain);

		for (int i = 0; i < list_LinePointMain.size(); i++) {
			
			LinePointMain linePointMain = list_LinePointMain.get(i);
			logger.debug("No.{} linePointMain = {}", i, linePointMain);

			// linePointMain.status = idle
			linePointMain.setStatus(LinePointMain.STATUS_IDLE);
			linePointMainService.save(linePointMain);

			// find linePointScheduledDetail.mainId = mainId
			List<LinePointScheduledDetail> details = linePointScheduledDetailService.findByLinePointMainId(linePointMain.getId());
			logger.debug("details: {}", details);

			JSONArray uid = new JSONArray();
			logger.debug("uid: {}", uid);
			
			for (LinePointScheduledDetail detail : details) {
				// 這邊應該判定 發過點就不發了
				if (LinePointScheduledDetail.STATUS_WAITING.equals(detail.getStatus())) {
					uid.put(detail.getUid());
				}
				
				detail.setStatus(LinePointScheduledDetail.STATUS_SENDED);
				detail.setModifyTime(new Date());
				linePointScheduledDetailService.save(detail);
			}

			logger.debug("Ready to tell akka to send LinePoint");
			// push to AkkaService
			LinePointPushModel linePointPushModel = new LinePointPushModel();
			linePointPushModel.setAmount(linePointMain.getAmount());
			linePointPushModel.setUid(uid);
			linePointPushModel.setEventId(linePointMain.getId());
			linePointPushModel.setSource(LinePointPushModel.SOURCE_TYPE_MGM);
			linePointPushModel.setSendTimeType(LinePointPushModel.SEND_TYPE_IMMEDIATE);
			linePointPushModel.setTriggerTime(new Date());

			logger.debug("linePointPushModel = {}", linePointPushModel);
			
			linePointPushAkkaService.tell(linePointPushModel);
		}
	}
}
