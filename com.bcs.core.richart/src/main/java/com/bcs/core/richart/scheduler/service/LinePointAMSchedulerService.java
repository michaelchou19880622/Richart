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
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
