package com.bcs.core.record.akke.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.bcs.core.record.akke.model.RecordMsg;
import com.bcs.core.utils.AkkaRouterFactory;
import com.bcs.core.utils.ObjectUtil;

public class RecordHandlerMaster extends UntypedActor {

	/** Logger */
	private static Logger logger = LogManager.getLogger(RecordHandlerMaster.class);

    private final ActorRef routerActor;
    
	public RecordHandlerMaster(){
	    routerActor = new AkkaRouterFactory<RecordHandlerWork>(getContext(), RecordHandlerWork.class, true).routerActor;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		logger.info("RecordHandlerMaster onReceive");
		if (message instanceof RecordMsg) {
			RecordMsg msg = (RecordMsg)message;
			if(msg.isSuccess()){
				// Record Success
				logger.info("RecordHandlerMaster onReceive Record Success");
			}
			else{
				int retry = msg.getRetry();
				if(retry < 5){
					logger.error("RecordHandlerMaster onReceive retry:" + retry);
					retry++;
					msg.setRetry(retry);
					routerActor.tell(msg, getSelf());
				}
				else{
					logger.error("RecordHandlerMaster onReceive Fail:" + ObjectUtil.objectToJsonStr(msg));
				}
			}
		}
		else{
			RecordMsg msg = new RecordMsg(message);
			routerActor.tell(msg, getSelf());
		}
	}
}