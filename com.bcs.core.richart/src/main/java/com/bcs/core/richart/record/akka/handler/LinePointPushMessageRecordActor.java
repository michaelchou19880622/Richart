package com.bcs.core.richart.record.akka.handler;

import com.bcs.core.richart.db.entity.LinePointPushMessageRecord;
import com.bcs.core.richart.db.service.LinePointPushMessageRecordService;
import com.bcs.core.spring.ApplicationContextProvider;

import akka.actor.UntypedActor;

public class LinePointPushMessageRecordActor extends UntypedActor {
	@Override
	public void onReceive(Object object) throws Exception {
		if(object instanceof LinePointPushMessageRecord) {
			LinePointPushMessageRecordService pushMessageRecordService = ApplicationContextProvider.getApplicationContext().getBean(LinePointPushMessageRecordService.class);
			LinePointPushMessageRecord pushMessageRecord = (LinePointPushMessageRecord) object;
			pushMessageRecordService.save(pushMessageRecord);
			return;
		}
	}
}