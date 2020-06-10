package com.bcs.core.api.service;

import java.util.Date;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.StringUtils;
import org.springframework.stereotype.Service;

import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.enums.LOG_TARGET_ACTION_TYPE;
import com.bcs.core.log.util.SystemLogUtil;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.InputStreamUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class LineFriendShipStatusService {

	/** Logger */
	private static Logger logger = LogManager.getLogger(LineFriendShipStatusService.class);

	public ObjectNode getFriendShipStatusService(String access_token) throws Exception{
		return this.getFriendShipStatusService(new Date(), access_token, 0);
	}

	public ObjectNode getFriendShipStatusService(Date start, String access_token, int retryCount) throws Exception{
		logger.info("getFriendShipStatusService");
		int status = 0;
		
		try{
			
			//HttpClient httpClient = HttpClientUtil.generateClient();
			
			RequestConfig config = RequestConfig.custom().build();
			try {
				String proxyUrl = CoreConfigReader.getString(CONFIG_STR.RICHART_PROXY_URL.toString(), true);
				logger.info("getFriendShipStatusService proxyUrl : " + proxyUrl);
//				logger.info("callRetrievingAPIforMGM proxy : " + proxy);
				if(!StringUtils.isEmpty(proxyUrl)){
		            HttpHost proxy = new HttpHost(proxyUrl, 80, "http");
		            config = RequestConfig.custom()
							  .setConnectTimeout(5000)
							  .setConnectionRequestTimeout(5000)
							  .setSocketTimeout(5000)
							  .setProxy(proxy)
							  .build();
				}else {
					 config = RequestConfig.custom()
							  .setConnectTimeout(5000)
							  .setConnectionRequestTimeout(5000)
							  .setSocketTimeout(5000)
							  .build();
				}
			} catch (Exception e) {
				logger.info("callRetrievingAPIforMGM proxyUrl : " + e);
				  config = RequestConfig.custom()
						  .setConnectTimeout(5000)
						  .setConnectionRequestTimeout(5000)
						  .setSocketTimeout(5000)
						  .build();
			}
			
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
			
			CloseableHttpClient closeableHttpClient = httpClientBuilder.setDefaultRequestConfig(config).build();
			
			String uri = CoreConfigReader.getString(CONFIG_STR.LINE_OAUTH_FRIENDSHIP_STATUS);
			logger.info("uri : " + uri);
			
			HttpGet requestGet = new HttpGet(uri);
			logger.info("requestGet : " + requestGet);
			
			requestGet.addHeader("Authorization", "Bearer " + access_token);
			logger.info("Authorization : Bearer " + access_token);
			
			// execute Call
			HttpResponse clientResponse = closeableHttpClient.execute(requestGet);
			
			status = clientResponse.getStatusLine().getStatusCode();
			logger.info("clientResponse StatusCode : " + status);
	
			String result = "";
			if(clientResponse != null && clientResponse.getEntity() != null && clientResponse.getEntity().getContent() != null){
				
				result += InputStreamUtil.getInputStr(clientResponse.getEntity().getContent());
			}
			logger.info("clientResponse result : " + result);
			
			requestGet.releaseConnection();

			SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_FriendshipApi, start, status, result, status + "");
			return (ObjectNode)(new ObjectMapper()).readTree(result);
		}
		catch(Exception e){
			String error = ErrorRecord.recordError(e, false);
			logger.info(error);
			SystemLogUtil.saveLogError(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_FriendshipApi, error, e.getMessage());
			SystemLogUtil.timeCheck(LOG_TARGET_ACTION_TYPE.TARGET_LineApi, LOG_TARGET_ACTION_TYPE.ACTION_FriendshipApi_Error, start, status, error, status + "");
			if(retryCount < 5){
				return this.getFriendShipStatusService(start, access_token, retryCount + 1);
			}
			else{
				throw e;
			}
		}
	}
}
