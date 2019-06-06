package com.bcs.core.richart.scheduler.service;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.richart.scheduler.handler.MGMTask;


@Service
public class MGMTaskService {
	public void mgmCheckLinePoint() throws SchedulerException {
		try {		
			// Create JobDetail
			JobDetail jobDetail = JobBuilder.newJob(MGMTask.class).withIdentity("mgmCheckLinePointJob", "MGMJobGroup").build(); // JobName, JobGroup	
			
			// Create Trigger
			SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(30).repeatForever();
			Trigger trigger=TriggerBuilder.newTrigger().withIdentity("MGMTrigger","MGMTriggerGroup").startNow().withSchedule(builder).build();
			
			// Create Scheduler
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		    Scheduler scheduler = schedulerFactory.getScheduler();
		    
			// Start Scheduler
			scheduler.scheduleJob(jobDetail, trigger);
			scheduler.start();	
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
