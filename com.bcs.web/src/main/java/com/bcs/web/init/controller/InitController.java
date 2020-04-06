package com.bcs.web.init.controller;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bcs.core.bot.record.service.CatchRecordReceive;
import com.bcs.core.bot.scheduler.service.LiveChatTaskService;
import com.bcs.core.bot.scheduler.service.PushMessageTaskService;
import com.bcs.core.bot.scheduler.service.SchedulerService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.interactive.service.InteractiveService;
import com.bcs.core.record.service.CatchHandleMsgReceiveTimeout;
import com.bcs.core.record.service.CatchRecordBinded;
import com.bcs.core.record.service.CatchRecordOpAddReceive;
import com.bcs.core.record.service.CatchRecordOpBlockedReceive;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.richart.scheduler.service.LinePointAMSchedulerService;
import com.bcs.core.richart.scheduler.service.LinePointPMSchedulerService;
import com.bcs.core.richart.scheduler.service.MGMTaskService;
import com.bcs.core.utils.DataSyncUtil;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.enums.CONFIG_STR;

@Controller
@RequestMapping("/init")
public class InitController {

	@Autowired
	private SchedulerService schedulerService;
	
	@Autowired
	private InteractiveService interactiveService;
	
	@Autowired
	private CatchRecordBinded catchRecordBinded;
	
	@Autowired
	private CatchRecordOpAddReceive catchRecordOpAddReceive;
	
	@Autowired
	private CatchRecordOpBlockedReceive catchRecordOpBlockedReceive;
	
	@Autowired
	private CatchHandleMsgReceiveTimeout catchHandleMsgReceiveTimeout;
	
	@Autowired
	private CatchRecordReceive catchRecordReceive;
	
	@Autowired
	private LiveChatTaskService liveChatTaskService;
	
	@Autowired
	private LinePointAMSchedulerService linePointAMSchedulerService;
	
//	@Autowired
//	private LinePointPMSchedulerService linePointPMSchedulerService;
//	
//	@Autowired
//	private MGMTaskService mgmTaskService;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(InitController.class);

	public InitController(){
		logger.info("Constructor InitController");
	}
	
	@PostConstruct
	public void init(){

		try {
			logger.info("[init] DataSyncUtil registerServer");
			DataSyncUtil.registerServer();
		} catch (Throwable e) {
			logger.error(ErrorRecord.recordError(e));
		}
		
		try {
			logger.info("[init] SchedulerService loadScheduleFromDB");
			schedulerService.loadScheduleFromDB();
		} catch (Throwable e) {
			logger.error(ErrorRecord.recordError(e));
		}
		
		try {
			logger.info("[init] InteractiveService loadInteractiveMap");
			interactiveService.loadInteractiveMap();
		} catch (Throwable e) {
			logger.error(ErrorRecord.recordError(e));
		}

		try {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					logger.info("[init] CatchRecordBinded loadInitData");
					catchRecordBinded.loadInitData();

					logger.info("[init] CatchRecordOpAddReceive loadInitData");
					catchRecordOpAddReceive.loadInitData();

					logger.info("[init] CatchRecordOpBlockedReceive loadInitData");
					catchRecordOpBlockedReceive.loadInitData();

					logger.info("[init] CatchRecordReceive loadInitData");
					catchRecordReceive.loadInitData();

					logger.info("[init] CatchHandleMsgReceiveTimeout loadInitData");
					catchHandleMsgReceiveTimeout.loadInitData();
				}
			});
			
			thread.start();
		} catch (Throwable e) {
			logger.error(ErrorRecord.recordError(e));
		}
		
		/* 定期檢查 User 的 status，避免卡在真人客服頻道 */
		try {
			logger.info("[init] LiveChatTaskService checkUserStatus");
			liveChatTaskService.checkUserStatus();
		} catch(Exception e) {
			logger.error(ErrorRecord.recordError(e));
		}

		// MGM CheckLinePoint Task
//		try {
//			mgmTaskService.mgmCheckLinePoint();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		
		// LinePoint AM Push flow
		try {
			logger.info("[init] LinePointAMSchedulerService startCircle");
			linePointAMSchedulerService.startCircle();
		} catch (Throwable e) {
			logger.error(ErrorRecord.recordError(e));
		}
		
//		// LinePoint PM Push flow
//		try {
//			logger.info("init LinePoint PM Push flow");
//			linePointPMSchedulerService.startCircle();
//		} catch (Throwable e) {
//			logger.error(ErrorRecord.recordError(e));
//		}
		
		/* 定期查找 FTP 有沒有需要發送的訊息檔案 */
		/*if(CoreConfigReader.getBoolean(CONFIG_STR.IS_MAIN_SYSTEM)) {	// 判斷是否為 BC 後台
			try {
				pushMessageTaskService.findNewTaskFromFtp();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}*/
	}

	/**
	 * cleanUp
	 */
	@PreDestroy
	public void cleanUp() {
		logger.info("[DESTROY] InitController cleaning up...");
		
		System.gc();
		logger.info("[DESTROY] InitController destroyed.");
	}
}
