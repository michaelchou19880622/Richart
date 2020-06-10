package com.bcs.web.test;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bcs.core.api.test.SpringJUnit4BaseTester;
import com.bcs.core.bot.db.entity.MsgBotReceive;
import com.bcs.core.bot.receive.service.MessageTransmitService;
import com.bcs.core.db.entity.MsgSendMain;
import com.bcs.core.db.service.MsgSendMainService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-base.xml", "classpath:spring/spring-security.xml"})
public class MessageTransmitServiceTester //extends SpringJUnit4BaseTester 
{

	@Autowired
	MessageTransmitService messageTransmitService;
	
	String a = "{\r\n" + 
			"  \"replyToken\": \"nHuyWiB7yP5Zw52FIkcQobQuGDXCTA\",\r\n" + 
			"  \"type\": \"message\",\r\n" + 
			"  \"timestamp\": 1462629479859,\r\n" + 
			"  \"source\": {\r\n" + 
			"    \"type\": \"user\",\r\n" + 
			"    \"userId\": \"U4af4980629...\"\r\n" + 
			"  },\r\n" + 
			"  \"message\": {\r\n" + 
			"    \"id\": \"325708\",\r\n" + 
			"    \"type\": \"text\",\r\n" + 
			"    \"text\": \"Hello, world!\"\r\n" + 
			"  }\r\n" + 
			"}\r\n";
	
	
	/** Logger */
	private static Logger logger = LogManager.getLogger(MessageTransmitServiceTester.class);

	@Test
	public void testTransmitToBOT() throws Exception {
//		messageTransmitService.transmitToBOT("123", "456", "789", "753", "951", "text");
		messageTransmitService.transmitToBOT("1554609602", "Uf01ee9e76b8ec1d65cffe682e0043b83", "nHuyWiB7yP5Zw52FIkcQobQuGDXCTA", null, "325708", "image");
	}
	
	@Test
	public void testTransmitToLiveChat() throws Exception {
		
		MsgBotReceive msg = new MsgBotReceive();
		msg.setMsgId("325708");
		msg.setMsgType("image");
		msg.setChannel("1554609602");
		msg.setSourceId("Uf01ee9e76b8ec1d65cffe682e0043b83");
		msg.setReplyToken("nHuyWiB7yP5Zw52FIkcQobQuGDXCTA");
		messageTransmitService.transmitToLiveChat(msg);//("1554609602", "Uf01ee9e76b8ec1d65cffe682e0043b83", "nHuyWiB7yP5Zw52FIkcQobQuGDXCTA", null, "325708", "image");
	}
	
	
	
}
