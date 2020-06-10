
package com.bcs.core.bot.akka.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.bcs.core.bot.receive.akka.handler.ReceivingMsgHandlerMaster;
import com.bcs.core.bot.send.akka.handler.SendingMsgHandlerMaster;
import com.bcs.core.receive.model.ReceivedModelOriginal;
import com.bcs.core.send.akka.model.AsyncSendingModel;
import com.bcs.core.utils.AkkaSystemFactory;
import com.bcs.core.utils.ErrorRecord;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

@Service
public class AkkaBotService {
	
	/** Logger */
	private static Logger logger = LogManager.getLogger(AkkaBotService.class);

	private List<ActorSystem> systemSending = new ArrayList<ActorSystem>();
	private List<ActorSystem> systemReceiving = new ArrayList<ActorSystem>();
	private List<ActorRef> sendingMaster = new ArrayList<ActorRef>();
	private List<ActorRef> receivingMaster = new ArrayList<ActorRef>();
	
	private AkkaBotService(){

		new AkkaSystemFactory<SendingMsgHandlerMaster>(systemSending, sendingMaster, SendingMsgHandlerMaster.class, "systemSending", "SendingMsgHandlerMaster");
		new AkkaSystemFactory<ReceivingMsgHandlerMaster>(systemReceiving, receivingMaster, ReceivingMsgHandlerMaster.class, this.getClass().getSimpleName(), ReceivingMsgHandlerMaster.class.getSimpleName());
	}
	
	public void sendingMsgs(AsyncSendingModel msgs){
		try{
			ActorRef master = randomMaster(sendingMaster);
			master.tell(msgs, master);
		}
		catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
		}
	}
	
	private ActorRef randomMaster(List<ActorRef> masters){
		logger.debug("randomMaster Size:" + masters.size());

        int index = new Random().nextInt(masters.size());
        return masters.get(index);
	}
	
	public void receivingMsgs(ReceivedModelOriginal msgs){
		try{
			ActorRef master = randomMaster(receivingMaster);
			master.tell(msgs, master);
		}
		catch(Exception e){
			logger.error(ErrorRecord.recordError(e));
		}
	}

	@PreDestroy
	public void shutdownNow(){
		logger.debug("[DESTROY] AkkaBotService shutdownNow cleaning up...");

		try{
			int count = 0;
			for(ActorSystem system : systemSending){
				system.stop(sendingMaster.get(count));
				count++;
				
				system.shutdown();
				system = null;
			}
		}
		catch(Throwable e){}

		try{
			int count = 0;
			for(ActorSystem system : systemReceiving){
				system.stop(receivingMaster.get(count));
				count++;
				
				system.shutdown();
				system = null;
			}
		}
		catch(Throwable e){}
		
		System.gc();
		logger.debug("[DESTROY] AkkaBotService shutdownNow destroyed");
	}
}
