package com.bcs.core.richart.scheduler.handler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.bcs.core.db.entity.ShareCampaign;
import com.bcs.core.db.entity.ShareCampaignClickTracing;
import com.bcs.core.db.entity.ShareUserRecord;
import com.bcs.core.db.service.ShareCampaignClickTracingService;
import com.bcs.core.db.service.ShareCampaignService;
import com.bcs.core.db.service.ShareUserRecordService;
import com.bcs.core.richart.akka.service.LinePointPushAkkaService;
import com.bcs.core.richart.api.model.LinePointPushModel;
import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.richart.db.entity.LinePointScheduledDetail;
import com.bcs.core.richart.db.service.LinePointMainService;
import com.bcs.core.richart.db.service.LinePointScheduledDetailService;
//import com.bcs.core.richart.scheduler.service.MGMService;
import com.bcs.core.spring.ApplicationContextProvider;
//import com.bcs.core.taishin.circle.db.entity.BillingNoticeMain;
//import com.bcs.core.taishin.circle.service.BillingNoticeService;
import com.bcs.core.utils.ErrorRecord;

public class MGMTask implements Job {
	ShareUserRecordService shareUserRecordService = ApplicationContextProvider.getApplicationContext().getBean(ShareUserRecordService.class);
	ShareCampaignService shareCampaignService = ApplicationContextProvider.getApplicationContext().getBean(ShareCampaignService.class);
	ShareCampaignClickTracingService shareCampaignClickTracingService = ApplicationContextProvider.getApplicationContext().getBean(ShareCampaignClickTracingService.class);
	LinePointMainService linePointMainService = ApplicationContextProvider.getApplicationContext().getBean(LinePointMainService.class);
	LinePointScheduledDetailService linePointScheduledDetailService = ApplicationContextProvider.getApplicationContext().getBean(LinePointScheduledDetailService.class);
	//LinePointPushAkkaService linePointPushAkkaService = ApplicationContextProvider.getApplicationContext().getBean(LinePointPushAkkaService.class);
	
	private static Logger logger = Logger.getLogger(MGMTask.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			logger.info("[MGMTask execute]");
			
//			// get undoneUser			
//			Map<String, List<String>> undoneUser = shareUserRecordService.findLatelyUndoneUsers();
//			logger.info("undoneUser:"+undoneUser);
//			
//			for(Map.Entry<String, List<String>> entry : undoneUser.entrySet()) {
//			    String shareUserRecordId = entry.getKey();
//			    List<String> list = entry.getValue();
//			    String campaignId = list.get(0);
//			    String uid = list.get(1);
//			    logger.info("不滿足的人："+uid);
//			    
//			    // get stateJudgement
//			    ShareCampaign shareCampaign = shareCampaignService.findOne(campaignId);
//			    String judgement = shareCampaign.getJudgement();
//			    String stateJudgement = "";
//			    if(judgement == ShareCampaign.JUDGEMENT_FOLLOW) stateJudgement = " and status <> 'BLOCK' ";
//			    else if (judgement == ShareCampaign.JUDGEMENT_BINDED) stateJudgement = " and status = 'BINDED' ";
//
//			    // count checkJudgement
//			    List<ShareCampaignClickTracing> friends =  shareCampaignClickTracingService.findByShareUserRecordId(shareUserRecordId);
//			    Integer count = 0;
//			    for(ShareCampaignClickTracing shareCampaignClickTracing : friends) {
//			    	String friendUid = shareCampaignClickTracing.getUid();
//			    	if(shareUserRecordService.checkJudgement(friendUid, stateJudgement)) {
//			    		logger.info("他送符合要求的人："+friendUid);
//			    		count++;
//			    	}
//			    }
//			    
//			    // undone -> done
//			    logger.info("符合要求人數:"+count + "/" + shareCampaign.getShareTimes());
//			    if(count >= shareCampaign.getShareTimes()) {
//			    	// change shareUserRecord status
//			    	ShareUserRecord shareUserRecord = shareUserRecordService.findOne(shareUserRecordId);
//			    	shareUserRecord.setCompleteStatus(ShareUserRecord.COMPLETE_STATUS_DONE);
//			    	shareUserRecordService.save(shareUserRecord);
//			    	
//			    	// change linePointMain status
//			    	String linePointSerialId = shareCampaign.getLinePointSerialId();
//			    	LinePointMain linePointMain = linePointMainService.findBySerialId(linePointSerialId);
//			    	linePointMain.setStatus(LinePointMain.STATUS_SCHEDULED);
//			    	linePointMainService.save(linePointMain);
//			    	
//			    	// save linePointScheduledDetail
//			    	LinePointScheduledDetail linePointScheduledDetail = new LinePointScheduledDetail();
//			    	linePointScheduledDetail.setUid(uid);
//			    	linePointScheduledDetail.setLinePointMainId(linePointMain.getId());
//			    	linePointScheduledDetailService.save(linePointScheduledDetail);
//			    }
//			}
		} catch (Exception e) {
			String error = ErrorRecord.recordError(e, false);
			logger.error("MGMTask Error:" + error);
		}
	}
}