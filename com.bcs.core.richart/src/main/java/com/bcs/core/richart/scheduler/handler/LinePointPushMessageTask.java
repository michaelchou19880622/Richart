package com.bcs.core.richart.scheduler.handler;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.jcodec.common.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import com.bcs.core.richart.api.model.LinePointPushModel;
import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.richart.db.entity.LinePointPushMessageRecord;
import com.bcs.core.db.entity.PushMessageRecord;
import com.bcs.core.richart.db.repository.LinePointMainRepository;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.LINE_HEADER;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.RestfulUtil;

import com.bcs.core.richart.akka.service.LinePointPushAkkaService;

public class LinePointPushMessageTask implements Job {	
	LinePointPushAkkaService AkkaLinePointPushService = ApplicationContextProvider.getApplicationContext().getBean(LinePointPushAkkaService.class);
	
	@Autowired
	LinePointMainRepository linePointMainRepository;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LinePointPushModel pushApiModel = null;
		try {
			pushApiModel = (LinePointPushModel) context.getScheduler().getContext().get("PushApiModel");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
		LinePointMain linePointMain = linePointMainRepository.findOne(pushApiModel.getEventId());
	
		String url = CoreConfigReader.getString(CONFIG_STR.LINE_POINT_MESSAGE_PUSH_URL.toString()); // https://api.line.me/pointConnect/v1/issue
		String accessToken = CoreConfigReader.getString(CONFIG_STR.Default.toString(), CONFIG_STR.ChannelToken.toString(), true); // Richart.LinePoint.ChannelToken
		
		JSONObject requestBody = new JSONObject();
		LinePointPushMessageRecord record = null;
		
		/* 設定 request headers */
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		
		requestBody.put("clientId", pushApiModel.getClientId());
		//requestBody.put("applicationTime", pushApiModel.getApplicationTime());
		requestBody.put("amount", pushApiModel.getAmount());
		
		JSONArray uids = pushApiModel.getUid();
		for(Integer i = 0; i < uids.length(); i++) {
			if(linePointMain.getSuccessfulCount() >= linePointMain.getTotalCount()) return;
			
			requestBody.put("memberId", uids.get(i));
			requestBody.put("orderKey", linePointMain.getSuccessfulCount().toString());

			record = new LinePointPushMessageRecord();
				
			/* 將 headers 跟 body 塞進 HttpEntity 中  */
			HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody.toString(), headers);
			
			try {
				RestfulUtil restfulUtil = new RestfulUtil(HttpMethod.POST, url, httpEntity);
				restfulUtil.execute();
				
				record.setEventId(pushApiModel.getEventId());
				record.setMemberId(uids.get(i).toString());
				record.setOrderKey(linePointMain.getSuccessfulCount().toString()); // need fix
				record.setAmount(pushApiModel.getAmount());
				record.setStatus(LinePointPushMessageRecord.SYSTEM_SUCCESS);
				record.setSendTime(new Date());
				record.setCreateTime(pushApiModel.getTriggerTime());
				
				linePointMain.setSuccessfulCount(linePointMain.getSuccessfulCount() + 1);
			} catch (KeyManagementException | NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				Logger.info("LinePointError:" + e.toString());
				if(e instanceof HttpClientErrorException) {
					record.setMemberId(uids.get(i).toString());
					record.setOrderKey("NULL");
					record.setStatus(LinePointPushMessageRecord.SYSTEM_FAIL);
					record.setSendTime(new Date());
					record.setCreateTime(pushApiModel.getTriggerTime());
				}
				linePointMain.setFailedCount(linePointMain.getFailedCount() + 1);
			}
			AkkaLinePointPushService.tell(record);
		}
	}
}