package com.bcs.core.record.akke.handler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.bcs.core.db.entity.PushMessageRecord;
import com.bcs.core.db.service.PushMessageRecordService;
import com.bcs.core.spring.ApplicationContextProvider;

import akka.actor.UntypedActor;

public class PushMessageRecordActor extends UntypedActor {

	/** Logger **/
	private static Logger logger = LogManager.getLogger(PushMessageRecordActor.class);
	
	@Override
	public void onReceive(Object object) throws Exception {
		logger.info("---------- PushMessageRecordActor ----------");
		
		if (object instanceof PushMessageRecord) {
			logger.info("[ onReceive ] object instanceof PushMessageRecord");
			PushMessageRecordService pushMessageRecordService = ApplicationContextProvider.getApplicationContext().getBean(PushMessageRecordService.class);
			PushMessageRecord pushMessageRecord = (PushMessageRecord) object;
			logger.info("[ onReceive ] pushMessageRecord = {}", pushMessageRecord);

			try {
				pushMessageRecordService.save(pushMessageRecord);
				logger.info("[ onReceive ] PushMessageRecordService save completed.");
			} catch (Exception e) {
				logger.info("[ onReceive ] PushMessageRecordService save error. Exception : {}", e);
			}

			return;
		}

		logger.error("[ onReceive ] object not instanceof PushMessageRecord");
	}
}