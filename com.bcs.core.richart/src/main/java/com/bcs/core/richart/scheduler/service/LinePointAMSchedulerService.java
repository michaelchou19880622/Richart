package com.bcs.core.richart.scheduler.service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
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
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.richart.akka.service.LinePointPushAkkaService;
import com.bcs.core.richart.api.model.LinePointPushModel;
import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.richart.db.entity.LinePointScheduledDetail;
import com.bcs.core.richart.db.service.LinePointMainService;
import com.bcs.core.richart.db.service.LinePointScheduledDetailService;

@Service
public class LinePointAMSchedulerService {

//	/** Logger */
//	private static Logger logger = Logger.getLogger(LinePointAMSchedulerService.class);

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

	public LinePointAMSchedulerService() {
	}

	/**
	 * Start Schedule
	 * 
	 * @throws SchedulerException
	 * @throws InterruptedException
	 */
	public void startCircle() throws SchedulerException, InterruptedException {
		// calculate delay
		Long delay = 0L;
		try {
			// get start time
			String startTimeStr = CoreConfigReader.getString(CONFIG_STR.LINE_POINT_AM_START_TIME, true);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			Date startDate = sdf.parse(startTimeStr);
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);

			// calculate next trigger time
			LocalDateTime localNow = LocalDateTime.now();
			ZonedDateTime now = ZonedDateTime.of(localNow, ZoneId.systemDefault());
			ZonedDateTime nextTrigger = now.withHour(start.get(Calendar.AM_PM) * 12 + start.get(Calendar.HOUR)).withMinute(start.get(Calendar.MINUTE)).withSecond(start.get(Calendar.SECOND));
			if (now.compareTo(nextTrigger) > 0) {
				// trigger at tomorrow
				nextTrigger = nextTrigger.plusDays(1);
			}

			// calculate delay
			Duration duration = Duration.between(now, nextTrigger);
			delay = duration.getSeconds();

			// write logger
			logger.info("now: " + now);
			logger.info("nextTrigger: " + nextTrigger);
			logger.info("delay: " + delay);
		} catch (Exception e) {
			logger.info("startCircle Calucute Delay Error:" + e.getMessage());
		}

		// run every day
//		scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
		scheduledFuture = scheduler.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				logger.info("---------------------------------------------------------");
				logger.info("LinePointAMSchedulerService startCircle....");
				pushScheduledLinePoint();
				logger.info("LinePointAMSchedulerService endCircle....");
				logger.info(" ");
			}
			// }, delay, 86400, TimeUnit.SECONDS);
		}, 60, 120, TimeUnit.SECONDS);
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
			logger.info(" LinePointAMSchedulerService cancel....");
		}
		if (scheduler != null && !scheduler.isShutdown()) {
			logger.info(" LinePointAMSchedulerService shutdown....");
			scheduler.shutdown();
		}
	}

	public void pushScheduledLinePoint() {
		long startTime = System.nanoTime();
		logger.info("[ pushScheduledLinePoint ] Start Time : {}", startTime);
		
		// get undoneUser
		List<ShareUserRecord> undoneUsers = shareUserRecordService.findLatelyUndoneUsers();
		logger.info("undoneUsers.size() = {}", undoneUsers.size());
		logger.info("undoneUsers = {}", undoneUsers);

		for (ShareUserRecord undoneUser : undoneUsers) {
			logger.info("----------------------------------------");
			logger.info("undoneUser = {}", undoneUser);

			// get autoSendPoint & judgment
			ShareCampaign shareCampaign = shareCampaignService.findOne(undoneUser.getCampaignId());
			logger.info("shareCampaignService.findOne = {}", shareCampaign);
			
			Boolean autoSendPoint = shareCampaign.getAutoSendPoint();
			logger.info("shareCampaign.getAutoSendPoint() = {}", autoSendPoint);
			
			String judgment = shareCampaign.getJudgement();
			logger.info("shareCampaign.getJudgement() = {}", judgment);

			// add count
			Long noJudgementCount = 0L;
			
			String shareUserRecordId = undoneUser.getShareUserRecordId();
			logger.info("shareUserRecordId = {}", shareUserRecordId);
			
			List<ShareCampaignClickTracing> list_shareCampaignClickTracing = shareCampaignClickTracingService.findByShareUserRecordId(shareUserRecordId);
			logger.info("list_shareCampaignClickTracing = {}", list_shareCampaignClickTracing);
			
			for (ShareCampaignClickTracing shareCampaignClickTracing : list_shareCampaignClickTracing) {
				logger.info("shareCampaignClickTracing = {}", shareCampaignClickTracing);
				
				String friendUid = shareCampaignClickTracing.getUid();
				logger.info("friendUid = {}", friendUid);
				
				// combine stateJudgment(MGM)
				String stateJudgment = "";
				
				if (judgment.equals(ShareCampaign.JUDGEMENT_FOLLOW)) {
					stateJudgment = " and status <> 'BLOCK' and create_Time >= '" + shareCampaignClickTracing.getSharedTime() + "' ";
				} else if (judgment.equals(ShareCampaign.JUDGEMENT_BINDED)) {
					stateJudgment = " and isBinded = 'BINDED' and bind_Time >= '" + shareCampaignClickTracing.getSharedTime() + "' ";
				}

				// check Judgment(判斷此UID 在line_USER裡面狀態是否符合)
				if (shareUserRecordService.checkJudgment(friendUid, stateJudgment)) {

					// check Exclusive
					if (judgment.equals(ShareCampaign.JUDGEMENT_DISABLE)) {
						noJudgementCount += 1L;
					} else {
						// find one row with same donatorUid(名人堂)
						List<ShareDonatorRecord> pastDonators = shareDonatorRecordService.findByDonatorUidAndShareUserRecordId(friendUid, shareUserRecordId);
						logger.info("pastDonators:" + pastDonators);
						
						// if not duplicated
						if (pastDonators.isEmpty()) {
							logger.info("donator is unique:" + friendUid);
							
							// save ShareDonatorRecord
							ShareDonatorRecord shareDonatorRecord = new ShareDonatorRecord();
							shareDonatorRecord.setDonatorUid(friendUid);
							shareDonatorRecord.setBenefitedUid(undoneUser.getUid());
							shareDonatorRecord.setCampaignId(undoneUser.getCampaignId());
							shareDonatorRecord.setModifyTime(new Date());
							shareDonatorRecord.setShareCampaignClickTracingId(shareCampaignClickTracing.getClickTracingId());
							shareDonatorRecord.setShareUserRecordId(undoneUser.getShareUserRecordId());
							shareDonatorRecord.setDonateLevel(judgment);
							shareDonatorRecordService.save(shareDonatorRecord);
							logger.info("shareDonatorRecord for saving:" + shareDonatorRecord);

							// save cumulative count
							undoneUser.setCumulativeCount(undoneUser.getCumulativeCount() + 1);
//							undoneUser.setModifyTime(new Date());
							shareUserRecordService.save(undoneUser);
							logger.info("undoneUser for saving:" + undoneUser);
						} else {
							// sun 修改 MGM 名人堂 更新為兩筆資料狀態分別為綁定及加好友
							if (pastDonators.size() < 2) {
								// ShareDonatorRecord shareDonatorRecord1 = pastDonators.get(0);
								// 如果兩筆以上代表有一筆是綁定所以不會進來 如果只有一筆就判斷他貢獻狀態是否是FOLLOW 是的話就在新增一筆
								if (pastDonators.get(0).getDonateLevel().equals("FOLLOW") && judgment.equals(ShareCampaign.JUDGEMENT_BINDED)) {

									ShareDonatorRecord shareDonatorRecord = new ShareDonatorRecord();
									shareDonatorRecord.setDonatorUid(friendUid);
									shareDonatorRecord.setBenefitedUid(undoneUser.getUid());
									shareDonatorRecord.setCampaignId(undoneUser.getCampaignId());
									shareDonatorRecord.setModifyTime(new Date());
									shareDonatorRecord.setShareCampaignClickTracingId(shareCampaignClickTracing.getClickTracingId());
									shareDonatorRecord.setShareUserRecordId(undoneUser.getShareUserRecordId());
									shareDonatorRecord.setDonateLevel(judgment);
									shareDonatorRecordService.save(shareDonatorRecord);
									logger.info("shareDonatorRecord for saving:" + shareDonatorRecord);

									// save cumulative count
									undoneUser.setCumulativeCount(undoneUser.getCumulativeCount() + 1);
//									undoneUser.setModifyTime(new Date());
									shareUserRecordService.save(undoneUser);
									logger.info("undoneUser for saving:" + undoneUser);
								}
							}
							
							logger.info("donator is duplicated:" + friendUid);
						}
					}
				}
			}
			
			if (judgment.equals(ShareCampaign.JUDGEMENT_DISABLE)) {
				undoneUser.setCumulativeCount(noJudgementCount);
				logger.info("noJudgementCount for saving:" + noJudgementCount);
				shareUserRecordService.save(undoneUser);
			}

			// undone -> done
			if (undoneUser.getCumulativeCount() >= shareCampaign.getShareTimes()) {
				logger.info("符合要求人數:" + undoneUser.getCumulativeCount() + "/" + shareCampaign.getShareTimes());
			}
			
			if (undoneUser.getCumulativeCount() >= shareCampaign.getShareTimes()) {

				// shareUserRecord.status = done
				ShareUserRecord shareUserRecord = shareUserRecordService.findOne(undoneUser.getShareUserRecordId());
				shareUserRecord.setCompleteStatus(ShareUserRecord.COMPLETE_STATUS_DONE);
				shareUserRecord.setDoneTime(new Date());
				shareUserRecordService.save(shareUserRecord);

				// autoSendPoint
				logger.info("autoSendPoint:" + autoSendPoint);
				if (autoSendPoint) {
					logger.info("LinePointMain.STATUS_SCHEDULED Saving");
					// linePointMain.status = scheduled
					String linePointSerialId = shareCampaign.getLinePointSerialId();
					LinePointMain linePointMain = linePointMainService.findBySerialId(linePointSerialId);
					linePointMain.setStatus(LinePointMain.STATUS_SCHEDULED);
					linePointMainService.save(linePointMain);

					// linePointScheduledDetail.status = waiting
					LinePointScheduledDetail linePointScheduledDetail = new LinePointScheduledDetail();
					linePointScheduledDetail.setUid(undoneUser.getUid());
					linePointScheduledDetail.setLinePointMainId(linePointMain.getId());
					linePointScheduledDetail.setStatus(LinePointScheduledDetail.STATUS_WAITING);
					linePointScheduledDetail.setModifyTime(new Date());
					linePointScheduledDetailService.save(linePointScheduledDetail);
				}
			}
		}

		// find linePointMain.status = scheduled
		List<LinePointMain> mains = linePointMainService.findByStatus(LinePointMain.STATUS_SCHEDULED);
		logger.info("mains.size() = {}", mains.size());
		logger.info("mains = {}", mains);
		
		for (LinePointMain main : mains) {
			logger.info("Scheduled LinePointMainId:" + main.getId());

			// linePointMain.status = idle
			main.setStatus(LinePointMain.STATUS_IDLE);
			linePointMainService.save(main);

			// find linePointScheduledDetail.mainId = mainId
			List<LinePointScheduledDetail> details = linePointScheduledDetailService.findByLinePointMainId(main.getId());

			JSONArray uid = new JSONArray();
			for (LinePointScheduledDetail detail : details) {
				// 這邊應該判定 發過點就不發了
				if (LinePointScheduledDetail.STATUS_WAITING.equals(detail.getStatus())) {
					uid.put(detail.getUid());
				}
				detail.setStatus(LinePointScheduledDetail.STATUS_SENDED);
				detail.setModifyTime(new Date());
				linePointScheduledDetailService.save(detail);
			}
			logger.info("uid (begin to send):" + uid);

			// push to AkkaService
			LinePointPushModel linePointPushModel = new LinePointPushModel();
			linePointPushModel.setAmount(main.getAmount());
			linePointPushModel.setUid(uid);
			linePointPushModel.setEventId(main.getId());
			linePointPushModel.setSource(LinePointPushModel.SOURCE_TYPE_MGM);
			linePointPushModel.setSendTimeType(LinePointPushModel.SEND_TYPE_IMMEDIATE);
			linePointPushModel.setTriggerTime(new Date());
			linePointPushAkkaService.tell(linePointPushModel);
		}

		long endTime = System.nanoTime();
		logger.info("[ pushScheduledLinePoint ] End Time : {}", endTime);
		logger.info("[ pushScheduledLinePoint ] Elapsed Time : {} seconds", (endTime - startTime) / 1_000_000_000);
	}
}
