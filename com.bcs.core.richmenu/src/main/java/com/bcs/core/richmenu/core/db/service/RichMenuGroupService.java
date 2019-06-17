package com.bcs.core.richmenu.core.db.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.bcs.core.api.msg.model.RichMsgAction;
import com.bcs.core.richmenu.core.db.entity.RichMenuContentFlag;
import com.bcs.core.richmenu.core.db.entity.RichMenuContentLink;
import com.bcs.core.richmenu.core.db.entity.RichMenuGroup;
import com.bcs.core.richmenu.core.db.entity.RichMenuContent;
import com.bcs.core.richmenu.core.db.entity.RichMenuContentDetail;
import com.bcs.core.db.entity.ContentRichMsgDetail;
import com.bcs.core.richmenu.core.db.repository.RichMenuContentDetailRepository;
import com.bcs.core.richmenu.core.db.repository.RichMenuContentRepository;
import com.bcs.core.richmenu.core.db.repository.RichMenuGroupRepository;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.utils.DataSyncUtil;
import com.bcs.core.utils.ErrorRecord;

@Service
public class RichMenuGroupService {
	
	public static final String RICHMENUGROUP_SYNC = "RICHMENUGROUP_SYNC";

	/** Logger */
	private static Logger logger = Logger.getLogger(RichMenuGroupService.class);
	@Autowired
	private RichMenuGroupRepository richMenuGroupRepository;
	@PersistenceContext(unitName="entityManagerFactory")
    EntityManager entityManager;

	
	private Timer flushTimer = new Timer();
	protected LoadingCache<String, Map<String, List<String>>> dataCache;
	private class CustomTask extends TimerTask{
		@Override
		public void run() {
			try{
				// Check Data Sync
				Boolean isReSyncData = DataSyncUtil.isReSyncData(RICHMENUGROUP_SYNC);
				if(isReSyncData){
					dataCache.invalidateAll();
					DataSyncUtil.syncDataFinish(RICHMENUGROUP_SYNC);
				}
			}
			catch(Throwable e){
				logger.error(ErrorRecord.recordError(e));
			}
		}
	}
	
	public RichMenuGroupService(){
		flushTimer.schedule(new CustomTask(), 120000, 30000);
		dataCache = CacheBuilder.newBuilder()
				.concurrencyLevel(1)
				.expireAfterAccess(30, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Map<String, List<String>>>() {
					@Override
					public Map<String, List<String>> load(String key) throws Exception {
						return new HashMap<String, List<String>>();
					}
				});
	}
	
	@PreDestroy
	public void cleanUp() {
		logger.info("[DESTROY] RichMenuGroupService cleaning up...");
		try{
			if(dataCache != null){
				dataCache.invalidateAll();
				dataCache = null;
			}
		}
		catch(Throwable e){}
		flushTimer.cancel();
		logger.info("[DESTROY] ContentRichMsgService destroyed.");
	}

	
	// get All Active RichMenuGroup List (Time Desc)
  	public List<RichMenuGroup> findAllActiveListTimeDesc(){
		return richMenuGroupRepository.findAll();
  	}

  	// find Duplicate by Group Name
	public List<RichMenuGroup> findByRichMenuGroupName(String richMenuGroupName) {
		return richMenuGroupRepository.findByRichMenuGroupName(richMenuGroupName);
	}
	
  	// save
	public void save(RichMenuGroup richMenuGroup){
		richMenuGroupRepository.save(richMenuGroup);
	}
    
	// findOne
	public RichMenuGroup findOne(Long richMenuGroupId) {
    	return richMenuGroupRepository.findOne(richMenuGroupId);
    }
}
