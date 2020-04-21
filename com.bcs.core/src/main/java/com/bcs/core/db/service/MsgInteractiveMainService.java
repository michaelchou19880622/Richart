package com.bcs.core.db.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.MsgDetail;
import com.bcs.core.db.entity.MsgInteractiveMain;
import com.bcs.core.db.repository.MsgInteractiveMainRepository;
import com.bcs.core.utils.DataSyncUtil;
import com.bcs.core.utils.ErrorRecord;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class MsgInteractiveMainService {
	public static final String INTERACTIVE_MAIN_SYNC = "INTERACTIVE_MAIN_SYNC";
	
	private static final String INIT_FLAG = "INIT_FLAG";
	/** Logger */
	private static Logger logger = Logger.getLogger(MsgInteractiveMainService.class);
	@Autowired
	private MsgInteractiveMainRepository msgInteractiveMainRepository;

	protected LoadingCache<Long, MsgInteractiveMain> dataCache;
	
	private ConcurrentMap<Long, AtomicLong> increaseMap = new ConcurrentHashMap<Long, AtomicLong>();

	private Timer flushTimer = new Timer();
	
    @PersistenceContext
    EntityManager entityManager;

	public MsgInteractiveMainService(){

		dataCache = CacheBuilder.newBuilder()
				.concurrencyLevel(1)
				.expireAfterAccess(30, TimeUnit.MINUTES)
				.build(new CacheLoader<Long, MsgInteractiveMain>() {
					@Override
					public MsgInteractiveMain load(Long key) throws Exception {
						return new MsgInteractiveMain();
					}
				});
		
		flushTimer.schedule(new CustomTask(), 120000, 30000);
	}
	
	@PreDestroy
	public void cleanUp() {
		logger.debug("[DESTROY] MsgInteractiveMainService cleaning up...");
		try{
			if(dataCache != null){
				dataCache.invalidateAll();
				dataCache = null;
			}
		}
		catch(Throwable e){}
		
		flushTimer.cancel();
		
		System.gc();
		logger.debug("[DESTROY] MsgInteractiveMainService destroyed.");
	}
	
	private boolean notNull(MsgInteractiveMain result){
		if(result != null && result.getiMsgId() != null){
			return true;
		}
		return false;
	}
    
	public MsgInteractiveMain findOne(Long iMsgId){
		try {
			MsgInteractiveMain result = dataCache.get(iMsgId);
			if(notNull(result)){
				return result;
			}
		} catch (Exception e) {}
		
		MsgInteractiveMain result = msgInteractiveMainRepository.findOne(iMsgId);
		if(result != null){
			dataCache.put(iMsgId, result);
		}
		return result;
	}
    
	public List<MsgInteractiveMain> findByInteractiveTypeAndInteractiveStatus(String interactiveType, String interactiveStatus){
		return msgInteractiveMainRepository.findByInteractiveTypeAndInteractiveStatus(interactiveType, interactiveStatus);
	}
    
	public void save(MsgInteractiveMain msgInteractiveMain){
		msgInteractiveMainRepository.save(msgInteractiveMain);

		if(msgInteractiveMain != null){
			dataCache.put(msgInteractiveMain.getiMsgId(), msgInteractiveMain);
			DataSyncUtil.settingReSync(INTERACTIVE_MAIN_SYNC);
		}
	}

	private class CustomTask extends TimerTask{
		
		@Override
		public void run() {

			try{
				flushIncrease();
				
				// Check Data Sync
				Boolean isReSyncData = DataSyncUtil.isReSyncData(INTERACTIVE_MAIN_SYNC);
				if(isReSyncData){
					dataCache.invalidateAll();
					DataSyncUtil.syncDataFinish(INTERACTIVE_MAIN_SYNC);
				}
			}
			catch(Throwable e){
				logger.error(ErrorRecord.recordError(e));
			}
		}
	}
	
	public void increaseSendCountByMsgInteractiveId(Long iMsgId){
		synchronized (INIT_FLAG) {
			if(increaseMap.get(iMsgId) == null){
				increaseMap.put(iMsgId, new AtomicLong(1L));
			}
			else{
				increaseMap.get(iMsgId).addAndGet(1);
			}
		}
	}
	
	public void flushIncrease(){
		synchronized (INIT_FLAG) {
			logger.debug("MsgInteractiveMainService flushTimer execute");
			for(Map.Entry<Long, AtomicLong> map : increaseMap.entrySet()){
				if(map.getValue().longValue() != 0){
					logger.debug("MsgInteractiveMainService flushTimer execute:" + map.getKey() + "," + map.getValue().longValue());
					this.increaseSendCountByMsgInteractiveId(map.getKey(), map.getValue().longValue());
					map.getValue().set(0);
				}
			}
			logger.debug("MsgInteractiveMainService flushTimer end");
		}
	}

	private void increaseSendCountByMsgInteractiveId(Long msgSendId, Long increase ){
		msgInteractiveMainRepository.increaseSendCountByMsgInteractiveId(msgSendId, increase);
	}
	
	@SuppressWarnings("unchecked")
	public Map<MsgInteractiveMain, List<MsgDetail>> queryGetMsgInteractiveMainDetailByMsgId(Long iMsgId){
		Query query = entityManager.createNamedQuery("queryGetMsgInteractiveMainDetailByMsgId").setParameter(1, iMsgId);
		query.setHint("javax.persistence.query.timeout", 30000);
		List<Object[]> list = query.getResultList();
		
		Map<MsgInteractiveMain, List<MsgDetail>> map = parseListToMap(list);
    	logger.debug(map);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<MsgInteractiveMain, List<MsgDetail>> queryGetMsgInteractiveMainDetailByType(String type){
		Query query = entityManager.createNamedQuery("queryGetMsgInteractiveMainDetailByType").setParameter(1, type);
		query.setHint("javax.persistence.query.timeout", 30000);
		List<Object[]> list = query.getResultList();
		
		Map<MsgInteractiveMain, List<MsgDetail>> map = parseListToMap(list);
    	logger.debug(map);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<MsgInteractiveMain, List<MsgDetail>> queryGetMsgInteractiveMainDetailByTypeAndStatus(String type, String status){
		Query query = entityManager.createNamedQuery("queryGetMsgInteractiveMainDetailByTypeAndStatus").setParameter(1, type).setParameter(2, status);
		query.setHint("javax.persistence.query.timeout", 30000);
		List<Object[]> list = query.getResultList();
		
		Map<MsgInteractiveMain, List<MsgDetail>> map = parseListToMap(list);
    	logger.debug(map);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<MsgInteractiveMain, List<MsgDetail>> queryGetMsgInteractiveMainDetailByMultiConditions(String type, String status, String keyword, String pushDate){
		StringBuilder dataQuerySQL = new StringBuilder();
		StringBuilder countQuerySQL = new StringBuilder();
		dataQuerySQL.append("SELECT BCS_MSG_INTERACTIVE_MAIN.MSG_INTERACTIVE_ID, INTERACTIVE_STATUS, INTERACTIVE_TYPE, USER_STATUS, MAIN_KEYWORD, STATUS_NOTICE, INTERACTIVE_INDEX, INTERACTIVE_START_TIME, INTERACTIVE_END_TIME, INTERACTIVE_TIME_TYPE, OTHER_ROLE, SERIAL_ID, SEND_COUNT, MODIFY_USER, MODIFY_TIME, MSG_DETAIL_ID, BCS_MSG_DETAIL.MSG_ID, MSG_TYPE, REFERENCE_ID, TEXT, MSG_PARENT_TYPE, BCS_MSG_DETAIL.EVENT_TYPE FROM BCS_MSG_INTERACTIVE_MAIN LEFT OUTER JOIN BCS_MSG_DETAIL ON BCS_MSG_INTERACTIVE_MAIN.MSG_INTERACTIVE_ID = BCS_MSG_DETAIL.MSG_ID  LEFT OUTER JOIN BCS_MSG_INTERACTIVE_DETAIL ON BCS_MSG_INTERACTIVE_MAIN.MSG_INTERACTIVE_ID = BCS_MSG_INTERACTIVE_DETAIL.MSG_INTERACTIVE_ID WHERE BCS_MSG_DETAIL.MSG_PARENT_TYPE = 'BCS_MSG_INTERACTIVE_MAIN'");
		if(StringUtils.isNotBlank(type)) {
			dataQuerySQL.append(" AND INTERACTIVE_TYPE = :interactiveType");
		}
		if(StringUtils.isNotBlank(status)) {
			if (!status.equalsIgnoreCase("DISABLE")) {
				status = "ACTIVE";
			}
			dataQuerySQL.append(" AND INTERACTIVE_STATUS = :status");
		}
		if(StringUtils.isNotBlank(keyword)) {
			dataQuerySQL.append(" AND (MAIN_KEYWORD LIKE CONCAT('%', :keyword,'%') OR OTHER_KEYWORD LIKE CONCAT('%', :keyword,'%'))");
		}
		
		countQuerySQL.append("SELECT COUNT(*) FROM (" + dataQuerySQL.toString() + ") AS COUNT");
		dataQuerySQL.append(" ORDER BY CASE WHEN INTERACTIVE_INDEX IS NULL THEN 1 ELSE 0 END, INTERACTIVE_INDEX, MAIN_KEYWORD, USER_STATUS, MODIFY_TIME, MSG_DETAIL_ID ");
		logger.debug("dataQuery=" + dataQuerySQL.toString());
		Query dataQuery = entityManager.createNativeQuery(dataQuerySQL.toString(), "MsgInteractiveMainDetails");
		if(StringUtils.isNotBlank(type)) {
		    dataQuery.setParameter("interactiveType", type);
		}
		if(StringUtils.isNotBlank(status)) {
			dataQuery.setParameter("status", status);
		}
		if(StringUtils.isNotBlank(keyword)) {
			dataQuery.setParameter("keyword", keyword);
		}
		dataQuery.setHint("javax.persistence.query.timeout", 30000);
		List<Object[]> list = dataQuery.getResultList();
		Map<MsgInteractiveMain, List<MsgDetail>> map = parseListToMap(list);
    	logger.debug(map);
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<MsgInteractiveMain, List<MsgDetail>> queryGetMsgInteractiveMainDetailAll(){
		Query query = entityManager.createNamedQuery("queryGetMsgInteractiveMainDetailAll");
		query.setHint("javax.persistence.query.timeout", 30000);
		List<Object[]> list = query.getResultList();
		
		Map<MsgInteractiveMain, List<MsgDetail>> map = parseListToMap(list);
    	logger.debug(map);
		
		return map;
	}
	
	private Map<MsgInteractiveMain, List<MsgDetail>> parseListToMap(List<Object[]> list){

		Map<MsgInteractiveMain, List<MsgDetail>> map = new LinkedHashMap<MsgInteractiveMain, List<MsgDetail>>();

	    for(Object[] o : list){
	    	logger.debug("length:" + o.length);
	    	logger.debug(o[0]);
	    	if(o[0] !=null){
	    		List<MsgDetail> details = map.get(o[0]);
	    		if(details == null){
	    			map.put((MsgInteractiveMain) o[0], new ArrayList<MsgDetail>());
	    		}
	    	}
	    	logger.debug(o[1]);
	    	if(o[1] != null){
	    		List<MsgDetail> details = map.get(o[0]);
	    		details.add((MsgDetail) o[1]);
	    	}
	    }
	    
	    return map;
	}
	
	public List<String> findIMsgIdByKeyword(String keyword){
	    return msgInteractiveMainRepository.findIMsgIdByKeyword(keyword);
	}
}
