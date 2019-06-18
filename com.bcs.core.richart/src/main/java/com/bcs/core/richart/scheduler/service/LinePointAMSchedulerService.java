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
import com.bcs.core.db.entity.ShareUserRecord;
import com.bcs.core.db.service.ShareCampaignClickTracingService;
import com.bcs.core.db.service.ShareCampaignService;
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
				logger.debug("LinePointAMSchedulerService startCircle....");
				pushScheduledLinePoint();
			}
		}, delay, 86400, TimeUnit.SECONDS);
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
		Map<String, List<String>> undoneUser = shareUserRecordService.findLatelyUndoneUsers();
		logger.info("undoneUser:"+undoneUser);
		
		for(Map.Entry<String, List<String>> entry : undoneUser.entrySet()) {
		    String shareUserRecordId = entry.getKey();
		    List<String> list = entry.getValue();
		    String campaignId = list.get(0);
		    String uid = list.get(1);
		    logger.info("不滿足的人："+uid);
		    
		    // get stateJudgement
		    ShareCampaign shareCampaign = shareCampaignService.findOne(campaignId);
		    String judgement = shareCampaign.getJudgement();
		    String stateJudgement = "";
		    if(judgement == ShareCampaign.JUDGEMENT_FOLLOW) stateJudgement = " and status <> 'BLOCK' ";
		    else if (judgement == ShareCampaign.JUDGEMENT_BINDED) stateJudgement = " and status = 'BINDED' ";

		    // count checkJudgement
		    List<ShareCampaignClickTracing> friends =  shareCampaignClickTracingService.findByShareUserRecordId(shareUserRecordId);
		    Integer count = 0;
		    for(ShareCampaignClickTracing shareCampaignClickTracing : friends) {
		    	String friendUid = shareCampaignClickTracing.getUid();
		    	if(shareUserRecordService.checkJudgement(friendUid, stateJudgement)) {
		    		logger.info("他送符合要求的人："+friendUid);
		    		count++;
		    	}
		    }
		    
		    // undone -> done
		    logger.info("符合要求人數:"+count + "/" + shareCampaign.getShareTimes());
		    if(count >= shareCampaign.getShareTimes()) {
		    	// change shareUserRecord status
		    	ShareUserRecord shareUserRecord = shareUserRecordService.findOne(shareUserRecordId);
		    	shareUserRecord.setCompleteStatus(ShareUserRecord.COMPLETE_STATUS_DONE);
		    	shareUserRecordService.save(shareUserRecord);
		    	
		    	// change linePointMain status
		    	String linePointSerialId = shareCampaign.getLinePointSerialId();
		    	LinePointMain linePointMain = linePointMainService.findBySerialId(linePointSerialId);
		    	linePointMain.setStatus(LinePointMain.STATUS_SCHEDULED);
		    	linePointMainService.save(linePointMain);
		    	
		    	// save linePointScheduledDetail
		    	LinePointScheduledDetail linePointScheduledDetail = new LinePointScheduledDetail();
		    	linePointScheduledDetail.setUid(uid);
		    	linePointScheduledDetail.setLinePointMainId(linePointMain.getId());
		    	linePointScheduledDetailService.save(linePointScheduledDetail);
		    }
		}
		
		// find main.status = Scheduled
		List<LinePointMain> mains = linePointMainService.findByStatus(LinePointMain.STATUS_SCHEDULED);
		for(LinePointMain main : mains) {
			
			// change main status
			main.setStatus(LinePointMain.STATUS_IDLE);
			linePointMainService.save(main);
			
			// find ScheduledDetails by mainId, input & delete them
			List<LinePointScheduledDetail> details = linePointScheduledDetailService.findByLinePointMainId(main.getId());
			JSONArray uid = new JSONArray();
			for(LinePointScheduledDetail detail : details) {
				uid.put(detail.getUid());
				linePointScheduledDetailService.delete(detail);
			}
			
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
