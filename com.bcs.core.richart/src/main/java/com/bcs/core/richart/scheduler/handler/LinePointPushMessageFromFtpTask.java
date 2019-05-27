package com.bcs.core.richart.scheduler.handler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.bcs.core.richart.akka.service.LinePointPushAkkaService;
import com.bcs.core.bot.pnp.model.FtpTaskModel;
import com.bcs.core.spring.ApplicationContextProvider;

public class LinePointPushMessageFromFtpTask implements Job {
	LinePointPushAkkaService AkkaLinePointPushService = ApplicationContextProvider.getApplicationContext().getBean(LinePointPushAkkaService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			FtpTaskModel ftpTaskModel = (FtpTaskModel) context.getScheduler().getContext().get("FtpTaskModel");
			
			ftpTaskModel.setIsScheduled(true);
			
			AkkaLinePointPushService.tell(ftpTaskModel);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}