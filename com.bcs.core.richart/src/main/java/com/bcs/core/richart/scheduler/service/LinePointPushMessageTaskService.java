package com.bcs.core.richart.scheduler.service;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import com.bcs.core.richart.api.model.LinePointPushModel;
import com.bcs.core.bot.pnp.model.FtpTaskModel;
import com.bcs.core.richart.scheduler.handler.LinePointFileParseTask;
import com.bcs.core.richart.scheduler.handler.LinePointPushMessageTask;
import com.bcs.core.richart.scheduler.handler.LinePointPushMessageFromFtpTask;
@Service
public class LinePointPushMessageTaskService {
	public void startTask(LinePointPushModel pushApiModel) throws SchedulerException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    
	    JobDetail jobDetail = newJob(LinePointPushMessageTask.class).withIdentity("pushApi", "PNP").build();
	    //Trigger trigger = newTrigger().withIdentity("pushApi", "PNP").startAt(pushApiModel.getSendTimeSet()).build();
	    
	    scheduler.getContext().put("PushApiModel", pushApiModel);
	    
	    //scheduler.scheduleJob(jobDetail, trigger);
	    scheduler.start();
	}
	
	public void startTaskFromFtp(FtpTaskModel ftpTaskModel) throws SchedulerException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    
	    JobDetail jobDetail = newJob(LinePointPushMessageFromFtpTask.class).withIdentity("pushFromFtp", "PNP").build();
	    Trigger trigger = newTrigger().withIdentity("pushFromFtp", "PNP").startAt(ftpTaskModel.getFileHead().getScheduledTime()).build();
	    
	    scheduler.getContext().put("FtpTaskModel", ftpTaskModel);
	    
	    scheduler.scheduleJob(jobDetail, trigger);
	    scheduler.start();
	}
	
	public void findNewTaskFromFtp() throws SchedulerException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule();
	    
	    JobDetail jobDetail = newJob(LinePointFileParseTask.class).withIdentity("findNewTaskFromFTP", "PNP").build();
	    Trigger trigger = newTrigger().withIdentity("findNewTaskFromFTP", "PNP").withSchedule(simpleSchedule.withIntervalInMinutes(1).repeatForever()).build();
	    
	    scheduler.scheduleJob(jobDetail, trigger);
	    scheduler.start();
	}
}