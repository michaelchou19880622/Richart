package com.bcs.core.richart.scheduler.service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.quartz.SchedulerException;
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

	/** Logger */
	private static Logger logger = Logger.getLogger(LinePointAMSchedulerService.class);
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
	        ZonedDateTime nextTrigger = now.withHour(start.get(Calendar.AM_PM) * 12 + start.get(Calendar.HOUR))
	        		.withMinute(start.get(Calendar.MINUTE)).withSecond(start.get(Calendar.SECOND));
			if(now.compareTo(nextTrigger) > 0) {
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
		}catch(Exception e) {
			logger.info("startCircle Calucute Delay Error:" + e.getMessage());
		}

		// run every day
		scheduledFuture = scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				logger.info("LinePointAMSchedulerService startCircle....");
				pushScheduledLinePoint();
			}
		//}, delay, 86400, TimeUnit.SECONDS);
		}, 0, 120, TimeUnit.SECONDS);
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
		// get undoneUser			
		List<ShareUserRecord> undoneUsers = shareUserRecordService.findLatelyUndoneUsers();
		logger.info("undoneUsers:"+undoneUsers);
		
		for(ShareUserRecord undoneUser : undoneUsers) {
			logger.info("undoneUser:"+undoneUser);
		    
		    // get autoSendPoint & judgment
		    ShareCampaign shareCampaign = shareCampaignService.findOne(undoneUser.getCampaignId());
		    Boolean autoSendPoint = shareCampaign.getAutoSendPoint();
		    String judgment = shareCampaign.getJudgement();
		    
		    // combine stateJudgment
		    String stateJudgment = "";
		    if(judgment == ShareCampaign.JUDGEMENT_FOLLOW) {
		    	String campaignStartDate = shareCampaign.getStartTime().toString();
		    	logger.info("campaignStartDate:"+campaignStartDate);
		    	stateJudgment = " and status <> 'BLOCK' and createTime >= " + campaignStartDate + " ";
		    }else if (judgment == ShareCampaign.JUDGEMENT_BINDED) {
		    	stateJudgment = " and status = 'BINDED' ";
		    }

		    // add count
		    Long noJudgementCount = 0L;
		    List<ShareCampaignClickTracing> friends =  shareCampaignClickTracingService.findByShareUserRecordId(undoneUser.getShareUserRecordId());
		    for(ShareCampaignClickTracing shareCampaignClickTracing : friends) {
		    	String friendUid = shareCampaignClickTracing.getUid();
		    	logger.info("friendUid:"+friendUid);
		    	
		    	// check Judgment
		    	if(shareUserRecordService.checkJudgment(friendUid, stateJudgment)) {
		    		logger.info("friendUid who Satisfied Judgment:"+friendUid);
		    		
		    		// check Exclusive
		    		if(judgment.equals(ShareCampaign.JUDGEMENT_DISABLE)){
		    			noJudgementCount += 1L;
		    		}else if(judgment.equals(ShareCampaign.JUDGEMENT_BINDED)){
				    	// find one row with same donatorUid
				    	List<ShareDonatorRecord> pastDonators = shareDonatorRecordService.findByDonatorUidAndDonateLevel(friendUid, ShareDonatorRecord.DONATE_LEVEL_BINDED);
				    	logger.info("pastDonators:"+pastDonators);
				    	
				    	// if not duplicated
				    	if(pastDonators.isEmpty()) {
				    		logger.info("donator is unique:"+friendUid);
				    		
				    		// save ShareDonatorRecord
				    		ShareDonatorRecord shareDonatorRecord = new ShareDonatorRecord();
				    		shareDonatorRecord.setDonatorUid(friendUid);
				    		shareDonatorRecord.setBenefitedUid(undoneUser.getUid());
				    		shareDonatorRecord.setCampaignId(undoneUser.getCampaignId());
				    		shareDonatorRecord.setModifyTime(new Date());
				    		shareDonatorRecord.setShareCampaignClickTracingId(shareCampaignClickTracing.getClickTracingId());
				    		shareDonatorRecord.setShareUserRecordId(undoneUser.getShareUserRecordId());
				    		shareDonatorRecord.setDonateLevel(ShareDonatorRecord.DONATE_LEVEL_BINDED);
				    		shareDonatorRecordService.save(shareDonatorRecord);
				    		logger.info("shareDonatorRecord for saving:"+shareDonatorRecord);
				    		
				    		// save cumulative count
				    		undoneUser.setCumulativeCount(undoneUser.getCumulativeCount() + 1);
				    		undoneUser.setModifyTime(new Date());
				    		shareUserRecordService.save(undoneUser);
				    		logger.info("undoneUser for saving:"+undoneUser);
				    	}else{
				    		logger.info("donator is duplicated:"+friendUid);
				    	}
				    	
		    		}else if(judgment.equals(ShareCampaign.JUDGEMENT_FOLLOW)){
				    	// find one row with same donatorUid
				    	List<ShareDonatorRecord> pastDonators = shareDonatorRecordService.findByDonatorUid(friendUid);
				    	logger.info("pastDonators:"+pastDonators);
				    	
				    	// if not duplicated
				    	if(pastDonators.isEmpty()) {
				    		logger.info("donator is unique:"+friendUid);
				    		
				    		// save ShareDonatorRecord
				    		ShareDonatorRecord shareDonatorRecord = new ShareDonatorRecord();
				    		shareDonatorRecord.setDonatorUid(friendUid);
				    		shareDonatorRecord.setBenefitedUid(undoneUser.getUid());
				    		shareDonatorRecord.setCampaignId(undoneUser.getCampaignId());
				    		shareDonatorRecord.setModifyTime(new Date());
				    		shareDonatorRecord.setShareCampaignClickTracingId(shareCampaignClickTracing.getClickTracingId());
				    		shareDonatorRecord.setShareUserRecordId(undoneUser.getShareUserRecordId());
				    		shareDonatorRecord.setDonateLevel(ShareDonatorRecord.DONATE_LEVEL_FOLLLOW);
				    		shareDonatorRecordService.save(shareDonatorRecord);
				    		logger.info("shareDonatorRecord for saving:"+shareDonatorRecord);
				    		
				    		// save cumulative count
				    		undoneUser.setCumulativeCount(undoneUser.getCumulativeCount() + 1);
				    		undoneUser.setModifyTime(new Date());
				    		shareUserRecordService.save(undoneUser);
				    		logger.info("undoneUser for saving:"+undoneUser);
				    	}else{
				    		logger.info("donator is duplicated:"+friendUid);
				    	}
		    		}
		    		
		    		
				    if(judgment.equals(ShareCampaign.JUDGEMENT_FOLLOW)  ||judgment.equals(ShareCampaign.JUDGEMENT_BINDED)) {
				    	// find one row with same donatorUid
				    	List<ShareDonatorRecord> pastDonators = shareDonatorRecordService.findByDonatorUid(friendUid);
				    	logger.info("pastDonators:"+pastDonators);
				    	
				    	// if not duplicated
				    	if(pastDonators.isEmpty()) {
				    		logger.info("donator is unique:"+friendUid);
				    		ShareDonatorRecord shareDonatorRecord = new ShareDonatorRecord();
				    		shareDonatorRecord.setDonatorUid(friendUid);
				    		shareDonatorRecord.setBenefitedUid(undoneUser.getUid());
				    		shareDonatorRecord.setCampaignId(undoneUser.getCampaignId());
				    		shareDonatorRecord.setModifyTime(new Date());
				    		shareDonatorRecord.setShareCampaignClickTracingId(shareCampaignClickTracing.getClickTracingId());
				    		shareDonatorRecord.setShareUserRecordId(undoneUser.getShareUserRecordId());
				    		
				    		undoneUser.setCumulativeCount(undoneUser.getCumulativeCount() + 1);
				    		undoneUser.setModifyTime(new Date());
				    		
				    		logger.info("shareDonatorRecord for saving:"+shareDonatorRecord);
				    		logger.info("undoneUser for saving:"+undoneUser);
				    		
				    		shareDonatorRecordService.save(shareDonatorRecord);
				    		shareUserRecordService.save(undoneUser);
				    	}else{
				    		logger.info("donator is duplicated:"+friendUid);
				    	}
				    }else{
				    	
				    }
		    	}
		    }
		    if(judgment.equals(ShareCampaign.JUDGEMENT_DISABLE)) {
		    	undoneUser.setCumulativeCount(noJudgementCount);
		    	logger.info("noJudgementCount for saving:"+noJudgementCount);
		    	shareUserRecordService.save(undoneUser);
		    }
		    
		    // undone -> done
		    logger.info("符合要求人數:"+undoneUser.getCumulativeCount() + "/" + shareCampaign.getShareTimes());
		    if(undoneUser.getCumulativeCount() >= shareCampaign.getShareTimes()) {

		    	// shareUserRecord.status = done
		    	ShareUserRecord shareUserRecord = shareUserRecordService.findOne(undoneUser.getShareUserRecordId());
		    	shareUserRecord.setCompleteStatus(ShareUserRecord.COMPLETE_STATUS_DONE);
		    	shareUserRecordService.save(shareUserRecord);
		    	
		    	// autoSendPoint
		    	logger.info("autoSendPoint:"+autoSendPoint);
		    	if(!autoSendPoint) {
		    		continue;
		    	}

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
		
		// find linePointMain.status = scheduled
		List<LinePointMain> mains = linePointMainService.findByStatus(LinePointMain.STATUS_SCHEDULED);
		for(LinePointMain main : mains) {
			logger.info("Scheduled LinePointMainId:"+main.getId());
			
			// linePointMain.status = idle
			main.setStatus(LinePointMain.STATUS_IDLE);
			linePointMainService.save(main);
			
			// find linePointScheduledDetail.mainId = mainId
			List<LinePointScheduledDetail> details = linePointScheduledDetailService.findByLinePointMainId(main.getId());
			
			JSONArray uid = new JSONArray();
			for(LinePointScheduledDetail detail : details) {
				uid.put(detail.getUid());
				
				detail.setStatus(LinePointScheduledDetail.STATUS_SENDED);
				detail.setModifyTime(new Date());
				linePointScheduledDetailService.save(detail);
			}
			logger.info("uid (begin to send):"+uid);
			
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
	}
}
