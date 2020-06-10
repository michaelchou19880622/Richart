package com.bcs.core.richmenu.core.db.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.bcs.core.richmenu.core.db.entity.RichMenuContentLink;
import com.bcs.core.richmenu.core.db.entity.RichMenuMsgContentLinkTracing;
import com.bcs.core.richmenu.core.db.repository.RichMenuMsgContentLinkTracingRepository;
import com.bcs.core.resource.UriHelper;


@Service
public class RichMenuMsgContentLinkTracingService {

	/** Logger */
	private static Logger logger = LogManager.getLogger(RichMenuMsgContentLinkTracingService.class);

	@Autowired
	private RichMenuMsgContentLinkTracingRepository msgContentLinkTracingRepository;

	protected LoadingCache<Long, RichMenuMsgContentLinkTracing> dataCache;

	public RichMenuMsgContentLinkTracingService() {

		dataCache = CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(30, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, RichMenuMsgContentLinkTracing>() {
					@Override
					public RichMenuMsgContentLinkTracing load(Long key) throws Exception {
						return new RichMenuMsgContentLinkTracing("-");
					}
				});
	}

	@PreDestroy
	public void cleanUp() {
		logger.info("[DESTROY] ContentLinkTracingService cleaning up...");
		try {
			if (dataCache != null) {
				dataCache.invalidateAll();
				dataCache = null;
			}
		} catch (Throwable e) {
		}

		System.gc();
		logger.info("[DESTROY] ContentLinkTracingService destroyed.");
	}

	private boolean notNull(RichMenuMsgContentLinkTracing result) {
		if (result != null && StringUtils.isNotBlank(result.getLinkId()) && !"-".equals(result.getLinkId())) {
			return true;
		}
		return false;
	}

	public void save(RichMenuMsgContentLinkTracing msgContentLinkTracing) {
		msgContentLinkTracingRepository.save(msgContentLinkTracing);

		if (msgContentLinkTracing != null) {
			dataCache.put(msgContentLinkTracing.getTracingId(), msgContentLinkTracing);
		}
	}

	public RichMenuMsgContentLinkTracing findOne(Long tracingId) {
		try {
			RichMenuMsgContentLinkTracing result = dataCache.get(tracingId);
			if (notNull(result)) {
				return result;
			}
		} catch (Exception e) {}

		RichMenuMsgContentLinkTracing result = msgContentLinkTracingRepository.findOne(tracingId);
		if (result != null) {
			dataCache.put(tracingId, result);
		}
		return result;
	}

	public List<RichMenuMsgContentLinkTracing> findAll() {
		return msgContentLinkTracingRepository.findAll(new Sort(Sort.Direction.DESC, "modifyTime"));
	}

	public List<RichMenuMsgContentLinkTracing> findAll(Sort sort) {
		return msgContentLinkTracingRepository.findAll(sort);
	}
	

	public String generateMsgTracingLink(RichMenuContentLink contentLink) throws Exception{
		
		if(contentLink != null){
			String beCheckedLink = contentLink.getLinkUrl();
			String tracingUrlPre = UriHelper.getTracingUrlPre();
			// 檢查是否為追蹤連結
			if(beCheckedLink.startsWith(tracingUrlPre)){
				return beCheckedLink;
			}
			
			Long tracingId = generateMsgTracingLink(contentLink.getLinkId());
			return UriHelper.getMsgTracingUrl(tracingId);
		}
		
		throw new Exception("ContentLink is null");
		
	}

	public Long generateMsgTracingLink(String contentLinkId) throws Exception {
		logger.debug("generateMsgTracingLink:" + contentLinkId);

		RichMenuMsgContentLinkTracing msgTracingLink = null;
		
		try{
			if (StringUtils.isNotBlank(contentLinkId)) {
				List<RichMenuMsgContentLinkTracing> msgTracingLinks = msgContentLinkTracingRepository.findByLinkId(contentLinkId);
				if(msgTracingLink == null && msgTracingLinks.size() == 0){
					msgTracingLink = new RichMenuMsgContentLinkTracing();
					msgTracingLink.setLinkId(contentLinkId);
					msgTracingLink.setLinkIdBinded(contentLinkId);
					msgTracingLink.setLinkIdUnMobile(contentLinkId);
					msgTracingLink.setModifyTime(new Date());
					msgContentLinkTracingRepository.save(msgTracingLink);
				}else{
					msgTracingLink = msgTracingLinks.get(0); 
				}
				
			} else {
				throw new Exception("ContentLinkId is null");
			}
		}catch(Exception e){
			throw new Exception("查無資料");
		}
		
		return msgTracingLink.getTracingId();
	}

}
