package com.bcs.core.bot.send.akka.handler;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bcs.core.api.service.model.PushApiModel;
import com.bcs.core.bot.pnp.model.FtpTaskModel;
import com.bcs.core.bot.scheduler.service.PushMessageTaskService;
import com.bcs.core.db.entity.PushMessageRecord;
import com.bcs.core.record.akke.handler.PushMessageRecordActor;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.AkkaRouterFactory;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class PNPMasterActor extends UntypedActor {
	
	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(PNPMasterActor.class);
	
	private final ActorRef pushMessageRouterActor;
	private final ActorRef pushMessageRecordRouterActor;
	private final ActorRef ftpTaskRouterActor;

	public PNPMasterActor() {
		pushMessageRouterActor = new AkkaRouterFactory<PushMessageActor>(getContext(), PushMessageActor.class, true).routerActor;
		pushMessageRecordRouterActor = new AkkaRouterFactory<PushMessageRecordActor>(getContext(), PushMessageRecordActor.class, true).routerActor;
		ftpTaskRouterActor = new AkkaRouterFactory<FtpTaskActor>(getContext(), FtpTaskActor.class, true).routerActor;
	}

	@Override
	public void onReceive(Object object) throws Exception {
		logger.info("---------- PNPMasterActor ----------");
		
		if (object instanceof PushApiModel) {
			logger.info("[ onReceive ] object instanceof PushApiModel");
			
			PushApiModel pushApiModel = (PushApiModel) object;
			logger.info("[ onReceive ] pushApiModel = {}", pushApiModel);
			
			Integer buffer = 100;
			
			JSONArray uids = pushApiModel.getUid();
			logger.info("[ onReceive ] uids = {}", uids);
			
			Integer arrayLength = uids.length();
			logger.info("[ onReceive ] arrayLength = {}", arrayLength);
			
			JSONArray partition = null;
			Integer pointer = 0;

			while (pointer < arrayLength) {
				Integer counter = 0;
				partition = new JSONArray();

				for (; (counter < buffer) && (pointer < arrayLength); counter++, pointer++) {
					partition.put(uids.get(pointer));
				}
				logger.info("[ onReceive ] partition = {}", partition);

				PushApiModel pushApiModel_clone = (PushApiModel) pushApiModel.clone();

				pushApiModel_clone.setUid(partition);
				
				logger.info("[ onReceive ] pushApiModel_clone = {}", pushApiModel_clone);

				pushMessageRouterActor.tell(pushApiModel_clone, this.getSelf());
			}
		} else if (object instanceof FtpTaskModel) {
			logger.info("[ onReceive ] object instanceof FtpTaskModel");
			
			FtpTaskModel ftpTaskModel = (FtpTaskModel) object;
			logger.info("[ onReceive ] ftpTaskModel = {}", ftpTaskModel);

			if (ftpTaskModel.getFileHead().getMessageSendType().equals(PushApiModel.SEND_TYPE_IMMEDIATE)) { // 立即發送
				ftpTaskRouterActor.tell(object, this.getSelf());
			} else if (ftpTaskModel.getFileHead().getMessageSendType().equals(PushApiModel.SEND_TYPE_DELAY)) { // 延遲發送
				if (ftpTaskModel.getIsScheduled() != null && ftpTaskModel.getIsScheduled())
					ftpTaskRouterActor.tell(object, this.getSelf());
				else {
					ApplicationContextProvider.getApplicationContext().getBean(PushMessageTaskService.class).startTaskFromFtp(ftpTaskModel);
				}
			}
		} else if (object instanceof PushMessageRecord) {
			logger.info("[ onReceive ] object instanceof PushMessageRecord");
			
			PushMessageRecord pushMessageRecord = (PushMessageRecord) object;
			logger.info("[ onReceive ] pushMessageRecord = {}", pushMessageRecord);
			
			pushMessageRecordRouterActor.tell(pushMessageRecord, this.getSelf());
		}
	}
}