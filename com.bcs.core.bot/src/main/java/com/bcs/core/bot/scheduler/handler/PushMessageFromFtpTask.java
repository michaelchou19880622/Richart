package com.bcs.core.bot.scheduler.handler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.bcs.core.bot.akka.service.PNPService;
import com.bcs.core.bot.pnp.model.FtpTaskModel;
import com.bcs.core.spring.ApplicationContextProvider;

public class PushMessageFromFtpTask implements Job {
	PNPService PNPService = ApplicationContextProvider.getApplicationContext().getBean(PNPService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			FtpTaskModel ftpTaskModel = (FtpTaskModel) context.getScheduler().getContext().get("FtpTaskModel");
			
			ftpTaskModel.setIsScheduled(true);
			
			PNPService.tell(ftpTaskModel);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}