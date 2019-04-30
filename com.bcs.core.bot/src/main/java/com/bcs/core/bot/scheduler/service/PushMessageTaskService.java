package com.bcs.core.bot.scheduler.service;

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

import com.bcs.core.api.service.model.PushApiModel;
import com.bcs.core.bot.pnp.model.FtpTaskModel;
import com.bcs.core.bot.scheduler.handler.FileParseTask;
import com.bcs.core.bot.scheduler.handler.PushMessageTask;
import com.bcs.core.bot.scheduler.handler.PushMessageFromFtpTask;

@Service
public class PushMessageTaskService {
	public void startTask(PushApiModel pushApiModel) throws SchedulerException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    
	    JobDetail jobDetail = newJob(PushMessageTask.class).withIdentity("pushApi", "PNP").build();
	    Trigger trigger = newTrigger().withIdentity("pushApi", "PNP").startAt(pushApiModel.getSendTimeSet()).build();
	    
	    scheduler.getContext().put("PushApiModel", pushApiModel);
	    
	    scheduler.scheduleJob(jobDetail, trigger);
	    scheduler.start();
	}
	
	public void startTaskFromFtp(FtpTaskModel ftpTaskModel) throws SchedulerException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    
	    JobDetail jobDetail = newJob(PushMessageFromFtpTask.class).withIdentity("pushFromFtp", "PNP").build();
	    Trigger trigger = newTrigger().withIdentity("pushFromFtp", "PNP").startAt(ftpTaskModel.getFileHead().getScheduledTime()).build();
	    
	    scheduler.getContext().put("FtpTaskModel", ftpTaskModel);
	    
	    scheduler.scheduleJob(jobDetail, trigger);
	    scheduler.start();
	}
	
	public void findNewTaskFromFtp() throws SchedulerException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
	    Scheduler scheduler = schedulerFactory.getScheduler();
	    SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule();
	    
	    JobDetail jobDetail = newJob(FileParseTask.class).withIdentity("findNewTaskFromFTP", "PNP").build();
	    Trigger trigger = newTrigger().withIdentity("findNewTaskFromFTP", "PNP").withSchedule(simpleSchedule.withIntervalInMinutes(1).repeatForever()).build();
	    
	    scheduler.scheduleJob(jobDetail, trigger);
	    scheduler.start();
	}
}