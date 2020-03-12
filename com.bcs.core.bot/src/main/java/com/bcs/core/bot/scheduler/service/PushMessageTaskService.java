package com.bcs.core.bot.scheduler.service;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bcs.core.api.service.model.PushApiModel;
import com.bcs.core.bot.pnp.model.FtpTaskModel;
import com.bcs.core.bot.scheduler.handler.FileParseTask;
import com.bcs.core.bot.scheduler.handler.PushMessageFromFtpTask;
import com.bcs.core.bot.scheduler.handler.PushMessageTask;

@Service
public class PushMessageTaskService {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(PushMessageTaskService.class);
	
	public void startTask(PushApiModel pushApiModel) throws SchedulerException {
		logger.info("PushMessageTaskService - startTask");
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    
	    JobDetail jobDetail = newJob(PushMessageTask.class).withIdentity("pushApi", "PNP").build();
		logger.info("jobDetail = {}", jobDetail);
	    
	    Trigger trigger = newTrigger().withIdentity("pushApi", "PNP").startAt(pushApiModel.getSendTimeSet()).build();
		logger.info("trigger = {}", trigger);
	    
	    scheduler.getContext().put("PushApiModel", pushApiModel);
	    
	    Date dateScheduleJob = scheduler.scheduleJob(jobDetail, trigger);
		logger.info("dateScheduleJob = {}", dateScheduleJob);
		
	    scheduler.start();
	}
	
	public void startTaskFromFtp(FtpTaskModel ftpTaskModel) throws SchedulerException {
		logger.info("PushMessageTaskService - startTaskFromFtp");
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    
	    JobDetail jobDetail = newJob(PushMessageFromFtpTask.class).withIdentity("pushFromFtp", "PNP").build();
		logger.info("jobDetail = {}", jobDetail);
		
	    Trigger trigger = newTrigger().withIdentity("pushFromFtp", "PNP").startAt(ftpTaskModel.getFileHead().getScheduledTime()).build();
		logger.info("trigger = {}", trigger);
	    
	    scheduler.getContext().put("FtpTaskModel", ftpTaskModel);
	    
	    Date dateScheduleJob = scheduler.scheduleJob(jobDetail, trigger);
		logger.info("dateScheduleJob = {}", dateScheduleJob);
		
	    scheduler.start();
	}
	
	public void findNewTaskFromFtp() throws SchedulerException {
		logger.info("PushMessageTaskService - findNewTaskFromFtp");
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule();
	    
	    JobDetail jobDetail = newJob(FileParseTask.class).withIdentity("findNewTaskFromFTP", "PNP").build();
		logger.info("jobDetail = {}", jobDetail);
		
	    Trigger trigger = newTrigger().withIdentity("findNewTaskFromFTP", "PNP").withSchedule(simpleSchedule.withIntervalInMinutes(1).repeatForever()).build();
		logger.info("trigger = {}", trigger);
		
		Date dateScheduleJob = scheduler.scheduleJob(jobDetail, trigger);
		logger.info("dateScheduleJob = {}", dateScheduleJob);
		
	    scheduler.start();
	}
}