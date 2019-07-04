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
import com.bcs.core.richmenu.core.db.entity.RichMenuContent;
import com.bcs.core.richmenu.core.db.entity.RichMenuContentDetail;
import com.bcs.core.db.entity.ContentRichMsgDetail;
import com.bcs.core.richmenu.core.db.repository.RichMenuContentDetailRepository;
import com.bcs.core.richmenu.core.db.repository.RichMenuContentRepository;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.utils.DataSyncUtil;
import com.bcs.core.utils.ErrorRecord;

@Service
public class RichMenuContentService {
	public static final String RICHMENU_SYNC = "RICHMENU_SYNC";
	
	@Autowired
	private RichMenuContentRepository contentRichMenuRepository;
	@Autowired
	private RichMenuContentDetailRepository contentRichMenuDetailRepository;
	@Autowired
	private RichMenuContentLinkService contentLinkService;
	@Autowired
	private RichMenuContentFlagService contentFlagService;

	@PersistenceContext(unitName="entityManagerFactory")
    EntityManager entityManager;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(RichMenuContentService.class);

	private Timer flushTimer = new Timer();

	protected LoadingCache<String, Map<String, List<String>>> dataCache;

	private class CustomTask extends TimerTask{
		
		@Override
		public void run() {

			try{
				// Check Data Sync
				Boolean isReSyncData = DataSyncUtil.isReSyncData(RICHMENU_SYNC);
				if(isReSyncData){
					dataCache.invalidateAll();
					DataSyncUtil.syncDataFinish(RICHMENU_SYNC);
				}
			}
			catch(Throwable e){
				logger.error(ErrorRecord.recordError(e));
			}
		}
	}

	public RichMenuContentService(){

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
		logger.info("[DESTROY] ContentRichMsgService cleaning up...");
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
    
	public RichMenuContent getSelectedContentRichMenu(String richId) {
    	return contentRichMenuRepository.findOne(richId);
    }
	
	/**
	 * 取得圖文選單更新前的DetailId與LinkId
     */
	@Transactional(rollbackFor=Exception.class, timeout = 30)
	public List<Map<String, String>> getPreDetailIdAndLinkId(String richId) {
		List<Map<String, String>> list = new ArrayList<>();
		List<RichMenuContentDetail> contentRichMenuDetails = contentRichMenuDetailRepository.findByRichId(richId);
		for (RichMenuContentDetail contentRichMenuDetail : contentRichMenuDetails) {
			Map<String, String> map = new LinkedHashMap<>();
			String linkId = contentRichMenuDetail.getLinkId();
			
			map.put("richDetailId", contentRichMenuDetail.getRichDetailId());
			map.put("linkId", linkId);
			list.add(map);

			contentRichMenuDetail.setStatus(ContentRichMsgDetail.STATUS_DELETE);
			contentRichMenuDetailRepository.save(contentRichMenuDetail);
		}
		
    	return list;
    }
	
	/**
	 * 取得圖文選單 By RichMenuGroupId
     */
	public List<RichMenuContent> getRichMenuListByRichMenuGroupId(Long richMenuGroupId) {
    	return contentRichMenuRepository.findByRichMenuGroupId(richMenuGroupId);
    }
    
	public List<RichMenuContent> getRichMenuListByRichMenuGroupIdAndLevelAndStatus(Long richMenuGroupId, String level, String status) {
    	return contentRichMenuRepository.findByRichMenuGroupIdAndLevelAndStatus(richMenuGroupId, level, status);
    }

	public List<RichMenuContent> getRichMenuListByRichMenuGroupIdAndLevel(Long richMenuGroupId, String level) {
    	return contentRichMenuRepository.findByRichMenuGroupIdAndLevel(richMenuGroupId, level);
    }
	
	/**
	 * 取得圖文選單
     */
    @SuppressWarnings("unchecked")
	public Map<String, List<String>> getContentRichMenu(String richId) {
		try {
			Map<String, List<String>> result = dataCache.get(richId);
			if(result != null && result.get(richId) != null){
				return result;
			}
		} catch (Exception e) {}
		
    	String queryString = 
    			"SELECT BCS_RICH_MENU_CONTENT.RICH_ID, "
        			+ "BCS_RICH_MENU_CONTENT.RICH_TYPE, "
    				+ "BCS_RICH_MENU_CONTENT.RICH_MENU_TITLE, "
					+ "BCS_RICH_MENU_CONTENT.RICH_IMAGE_ID, "
					+ "BCS_RICH_MENU_CONTENT_LINK.LINK_URL, "
					+ "BCS_RICH_MENU_CONTENT_LINK.LINK_TITLE, "
					+ "BCS_RICH_MENU_CONTENT_LINK.LINK_TAG, "
					+ "BCS_RICH_MENU_CONTENT_DETAIL.START_POINT_X, "
					+ "BCS_RICH_MENU_CONTENT_DETAIL.START_POINT_Y, "
					+ "BCS_RICH_MENU_CONTENT_DETAIL.END_POINT_X, "
					+ "BCS_RICH_MENU_CONTENT_DETAIL.END_POINT_Y, "
					+ "BCS_CONTENT_RESOURCE.RESOURCE_HEIGHT, "
					+ "BCS_CONTENT_RESOURCE.RESOURCE_WIDTH, "
					+ "BCS_RICH_MENU_CONTENT_DETAIL.LINK_ID, "
					+ "BCS_RICH_MENU_CONTENT.STATUS, "
					+ "BCS_RICH_MENU_CONTENT_DETAIL.ACTION_TYPE, "
    				+ "BCS_RICH_MENU_CONTENT.RICH_MENU_NAME, "
    				+ "BCS_RICH_MENU_CONTENT.RICH_MENU_SHOW_STATUS, "
					+ "BCS_RICH_MENU_CONTENT.LEVEL, "
					+ "BCS_RICH_MENU_CONTENT.MENU_SIZE, "
					+ "BCS_RICH_MENU_CONTENT.RICH_MENU_START_USING_TIME, "
					+ "BCS_RICH_MENU_CONTENT.RICH_MENU_END_USING_TIME "
    			+ "FROM BCS_RICH_MENU_CONTENT "
	    			+ "LEFT JOIN BCS_RICH_MENU_CONTENT_DETAIL ON BCS_RICH_MENU_CONTENT.RICH_ID = BCS_RICH_MENU_CONTENT_DETAIL.RICH_ID "
	    			+ "LEFT JOIN BCS_RICH_MENU_CONTENT_LINK ON BCS_RICH_MENU_CONTENT_DETAIL.LINK_ID = BCS_RICH_MENU_CONTENT_LINK.LINK_ID "
	    			+ "LEFT JOIN BCS_CONTENT_RESOURCE ON BCS_RICH_MENU_CONTENT.RICH_IMAGE_ID = BCS_CONTENT_RESOURCE.RESOURCE_ID "
    			+ "WHERE BCS_RICH_MENU_CONTENT.RICH_ID = ?1 AND (BCS_RICH_MENU_CONTENT_DETAIL.STATUS <> 'DELETE' OR BCS_RICH_MENU_CONTENT_DETAIL.STATUS IS NULL) "
    			+ "ORDER BY BCS_RICH_MENU_CONTENT_DETAIL.RICH_DETAIL_LETTER";
    	
    	Query query = entityManager.createNativeQuery(queryString).setParameter(1, richId);
		query.setHint("javax.persistence.query.timeout", 30000);
		List<Object[]> list = query.getResultList();
    	
		Map<String, List<String>> map = new LinkedHashMap<>();
		for (Object[] o : list) {
			for (int i=0, max=o.length; i<max; i++) {
				if (i == 0) {
					List<String> dataList = map.get(o[0]);
					if (dataList == null) {
						map.put(o[0].toString(), new ArrayList<String>());
						continue;
					} else { //重覆的richId，因為有多個連結
						for (int j=3; j<=9; j++) {
							String appendValue = (o[j+1] == null ? "null" : o[j+1].toString());
							dataList.set(j, dataList.get(j) + "," + appendValue);
						}
						for (int j=12; j<=12; j++) {
							dataList.set(j, dataList.get(j) + "," + o[j+1].toString());
						}
						for (int j=14; j<=14; j++) {
							String last = dataList.get(j);
							if(StringUtils.isBlank(last)){
								last = RichMsgAction.ACTION_TYPE_WEB;
							}
							String newStr = (String) o[j+1];
							if(StringUtils.isBlank(newStr)){
								newStr = RichMsgAction.ACTION_TYPE_WEB;
							}
							dataList.set(j, last + "," + newStr);
						}
						break;
					}
				}
				
				List<String> dataList = map.get(o[0]);
				if (o[i] == null) {
					dataList.add(null);
				} else {
					dataList.add(o[i].toString());
				}
			}
		}
		
    	logger.debug(map);
		if(map != null){
			dataCache.put(richId, map);
		}
		return map;
    }
	
    /**
	 * 取得圖文選單所有清單
     */
    @SuppressWarnings("unchecked")
	public Map<String, List<String>> getAllContentRichMenuByStatus(String status){
    	
    	String queryString = 
    			"SELECT BCS_RICH_MENU_CONTENT.RICH_ID, "
    					+ "BCS_RICH_MENU_CONTENT.RICH_MENU_NAME, "
    					+ "BCS_RICH_MENU_CONTENT_LINK.LINK_URL, "
    					+ "BCS_RICH_MENU_CONTENT.MODIFY_TIME, "
    					+ "BCS_ADMIN_USER.USER_NAME, "
    					+ "BCS_RICH_MENU_CONTENT.RICH_IMAGE_ID, "
    					+ "BCS_RICH_MENU_CONTENT_LINK.LINK_TITLE, "
    					+ "BCS_RICH_MENU_CONTENT.STATUS, "
    					+ "BCS_RICH_MENU_CONTENT.LEVEL, "
    					+ "BCS_RICH_MENU_CONTENT_DETAIL.ACTION_TYPE, "
    					+ "BCS_RICH_MENU_CONTENT_DETAIL.LINK_ID "
    			+ "FROM BCS_RICH_MENU_CONTENT "
	    			+ "LEFT JOIN BCS_RICH_MENU_CONTENT_DETAIL ON BCS_RICH_MENU_CONTENT.RICH_ID = BCS_RICH_MENU_CONTENT_DETAIL.RICH_ID "
	    			+ "LEFT JOIN BCS_ADMIN_USER ON BCS_RICH_MENU_CONTENT.MODIFY_USER = BCS_ADMIN_USER.ACCOUNT "
	    			+ "LEFT JOIN BCS_RICH_MENU_CONTENT_LINK ON BCS_RICH_MENU_CONTENT_DETAIL.LINK_ID = BCS_RICH_MENU_CONTENT_LINK.LINK_ID "
	    		+ "WHERE BCS_RICH_MENU_CONTENT.STATUS = ?1 AND (BCS_RICH_MENU_CONTENT_DETAIL.STATUS <> 'DELETE' OR BCS_RICH_MENU_CONTENT_DETAIL.STATUS IS NULL)"
    			+ "ORDER BY BCS_RICH_MENU_CONTENT.MODIFY_TIME DESC, BCS_RICH_MENU_CONTENT_DETAIL.RICH_DETAIL_LETTER";
    	
    	Query query = entityManager.createNativeQuery(queryString).setParameter(1, status);
		query.setHint("javax.persistence.query.timeout", 30000);
		List<Object[]> list = query.getResultList();
		
		Map<String, List<String>> map = new LinkedHashMap<>();
		for (Object[] o : list) {
			
			for (int i = 0, max = o.length; i < max; i++) {
		
				if (i == 0) {
					List<String> dataList = map.get(o[0]);
					if (dataList == null) {
						map.put(o[0].toString(), new ArrayList<String>());
						continue;
					} else { //重覆的richId，因為有多個連結
						// Link Url
						if(o[2] != null){
							dataList.set(1, dataList.get(1) + "," + o[2].toString());
						}
						else{
							dataList.set(1, dataList.get(1) + ",null");
						}
						// Link Title
						if(o[6] != null){
							dataList.set(5, dataList.get(5) + "," + o[6].toString());
						}
						else{
							dataList.set(5, dataList.get(5) + ",null");
						}
						
						// ACTION_TYPE
						String last = dataList.get(7);
						if(StringUtils.isBlank(last)){
							last = RichMsgAction.ACTION_TYPE_WEB;
						}
						String newStr = (String) o[8];
						if(StringUtils.isBlank(newStr)){
							newStr = RichMsgAction.ACTION_TYPE_WEB;
						}
						dataList.set(7, last + "," + newStr);

						// Link ID
						dataList.set(8, dataList.get(8) + "," + o[9].toString());
						break;
					}
				}
				
				List<String> dataList = map.get(o[0]);
				if (o[i] == null) {
					dataList.add(null);
				} else {
					dataList.add(o[i].toString());
				}
			}
		}
		
    	logger.debug("getAllContentRichMenuByStatus map:" + map);
    	
		return map;
	}
    
    /**
     * 取得圖文選單所有清單
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getAllContentRichMenu(String name, int page, int size, boolean isAsc, String status){
        
        int rowStart = page * size;
        int rowEnd = rowStart + size;
        
        String queryTitleStr = "";
        if(StringUtils.isNotBlank(name)) {
            queryTitleStr = "and BCS_RICH_MENU_CONTENT.RICH_MENU_NAME like ?4 ";
        }
        
        String sortStr = "DESC";
        if(isAsc) {
            sortStr = "ASC";
        }
        
        String queryString = 
                "select * from "
                + "(SELECT BCS_RICH_MENU_CONTENT.RICH_ID, "
                        + "BCS_RICH_MENU_CONTENT.RICH_MENU_NAME, "
                        + "BCS_RICH_MENU_CONTENT_LINK.LINK_URL, "
                        + "BCS_RICH_MENU_CONTENT.MODIFY_TIME, "
                        + "BCS_RICH_MENU_CONTENT.MODIFY_USER, "
                        + "BCS_RICH_MENU_CONTENT.RICH_IMAGE_ID, "
                        + "BCS_RICH_MENU_CONTENT_LINK.LINK_TITLE, "
                        + "BCS_RICH_MENU_CONTENT.STATUS, "
                        + "BCS_RICH_MENU_CONTENT.LEVEL, "
                        + "BCS_RICH_MENU_CONTENT_DETAIL.ACTION_TYPE, "
                        + "BCS_RICH_MENU_CONTENT_DETAIL.LINK_ID, "
                        + "BCS_RICH_MENU_CONTENT_DETAIL.RICH_DETAIL_LETTER, "
                        + "DENSE_RANK() OVER ( ORDER BY BCS_RICH_MENU_CONTENT.MODIFY_TIME "+ sortStr +", BCS_RICH_MENU_CONTENT.RICH_ID) AS RowNum, "
                        + "BCS_RICH_MENU_CONTENT.RICH_MENU_ID, "
                        + "BCS_RICH_MENU_CONTENT.RICH_MENU_START_USING_TIME, "
                        + "BCS_RICH_MENU_CONTENT.RICH_MENU_END_USING_TIME "
                + "FROM BCS_RICH_MENU_CONTENT "
                    + "LEFT JOIN BCS_RICH_MENU_CONTENT_DETAIL ON BCS_RICH_MENU_CONTENT.RICH_ID = BCS_RICH_MENU_CONTENT_DETAIL.RICH_ID "
                    + "LEFT JOIN BCS_ADMIN_USER ON BCS_RICH_MENU_CONTENT.MODIFY_USER = BCS_ADMIN_USER.ACCOUNT "
                    + "LEFT JOIN BCS_RICH_MENU_CONTENT_LINK ON BCS_RICH_MENU_CONTENT_DETAIL.LINK_ID = BCS_RICH_MENU_CONTENT_LINK.LINK_ID "
                + "WHERE BCS_RICH_MENU_CONTENT.STATUS = ?1 AND (BCS_RICH_MENU_CONTENT_DETAIL.STATUS <> 'DELETE' OR BCS_RICH_MENU_CONTENT_DETAIL.STATUS IS NULL) "
                + queryTitleStr + ") as RowConstrainedResult "
                + "where RowNum > ?2 AND RowNum <= ?3 "
                + "order by MODIFY_TIME "+ sortStr +", RICH_DETAIL_LETTER";
        
        Query query = entityManager.createNativeQuery(queryString).setParameter(1, status).setParameter(2, rowStart).setParameter(3, rowEnd);

        if(StringUtils.isNotBlank(name)) {
            query.setParameter(4, "%" + name + "%");
        }

        query.setHint("javax.persistence.query.timeout", 30000);
        List<Object[]> list = query.getResultList();
        
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (Object[] o : list) {
            for (int i = 0, max = o.length; i < max; i++) {
                if (i == 0) {
                    List<String> dataList = map.get(o[0]);
                    if (dataList == null) {
                        map.put(o[0].toString(), new ArrayList<String>());
                        continue;
                    } else { //重覆的richId，因為有多個連結
                        // Link Url
                        if(o[2] != null){
                            dataList.set(1, dataList.get(1) + "," + o[2].toString());
                        }
                        else{
                            dataList.set(1, dataList.get(1) + ",null");
                        }
                        // Link Title
                        if(o[6] != null){
                            dataList.set(5, dataList.get(5) + "," + o[6].toString());
                        }
                        else{
                            dataList.set(5, dataList.get(5) + ",null");
                        }

                        // ACTION_TYPE
                        dataList.set(8, dataList.get(8) + "," + o[9].toString());
                        
                        // LINK_ID
                        dataList.set(9, dataList.get(9) + "," + o[10].toString());
                        
                        // RICH_DETAIL_LETTER
                        dataList.set(10, dataList.get(10) + "," + o[11].toString());
                        break;
                    }
                }
                
                List<String> dataList = map.get(o[0]);
                if (o[i] == null) {
                    dataList.add(null);
                } else {
                    dataList.add(o[i].toString());
                }
            }
        }
        
        logger.debug("getAllContentRichMenu map:" + map);
        
        return map;
    }
    
    /**
	 *  檢查有無重覆使用到UUID
     */
    public Boolean checkDuplicateUUID(String queryType, String uuid) {
    	if (queryType == "1") {
    		RichMenuContent contentRichMenu = contentRichMenuRepository.findOne(uuid);
    		if (contentRichMenu == null) return false;
    	} else if (queryType == "2") {
    		RichMenuContentDetail contentRichMenuDetail = contentRichMenuDetailRepository.findOne(uuid);
    		if (contentRichMenuDetail == null) return false;
    	} else {
    		RichMenuContentLink contentLink = contentLinkService.findOne(uuid);
    		if (contentLink == null) return false;
    	}
    	
		return true;
    }
    
    /**
	 * 新增圖文訊息
     */
    @Transactional(rollbackFor=Exception.class, timeout = 30)
	public void createRichMsg(
			RichMenuContent contentRichMenu,  
			List<RichMenuContentDetail> contentRichMenuDetails, 
			List<RichMenuContentLink> contentLinks, 
			Map<String, List<String>> contentFlagMap){
		contentRichMenuRepository.save(contentRichMenu);
    	contentRichMenuDetailRepository.save(contentRichMenuDetails);
    	contentLinkService.save(contentLinks);

    	// Save ContentFlag
    	for (Map.Entry<String, List<String>> entry : contentFlagMap.entrySet()) {
    		contentFlagService.save(
    				entry.getKey(), 
    				RichMenuContentFlag.CONTENT_TYPE_LINK, 
    				entry.getValue());
		}
    	
		dataCache.invalidate(contentRichMenu.getRichId());
		DataSyncUtil.settingReSync(RICHMENU_SYNC);
	}
    
    /**
	 * 停用圖文選單
     */
    @Transactional(rollbackFor=Exception.class, timeout = 30)
	public void disableRichMenu(String richId, String account){
		// 只改變狀態
		RichMenuContent contentRichMenu = contentRichMenuRepository.findOne(richId);
		contentRichMenu.setStatus(RichMenuContent.STATUS_DISABLE);
		contentRichMenu.setRichMenuId("");
		contentRichMenu.setModifyUser(account);
		contentRichMenu.setModifyTime(new Date());
		
		contentRichMenuRepository.save(contentRichMenu);

		dataCache.invalidate(contentRichMenu.getRichId());
		DataSyncUtil.settingReSync(RICHMENU_SYNC);
	}
    
    /**
	 * 刪除圖文選單
     */
    @Transactional(rollbackFor=Exception.class, timeout = 30)
	public void deleteRichMenu(String richId, String account){
		// 只改變狀態
		RichMenuContent contentRichMenu = contentRichMenuRepository.findOne(richId);
		contentRichMenu.setStatus(RichMenuContent.STATUS_DELETE);
		contentRichMenu.setRichMenuId("");
		contentRichMenu.setModifyUser(account);
		contentRichMenu.setModifyTime(new Date());
		
		contentRichMenuRepository.save(contentRichMenu);

		dataCache.invalidate(contentRichMenu.getRichId());
		DataSyncUtil.settingReSync(RICHMENU_SYNC);
	}
    
    public Long countTotal(){
        return contentRichMenuRepository.countTotal();
    }
    
    public Long countTotalByLikeTitle(String title) {
        return contentRichMenuRepository.countTotalByLikeName(title);
    }
    
	public RichMenuContent findByStatusAndCondition(String status, String condition){
		return contentRichMenuRepository.findByStatusAndCondition(status, condition);
	}
    
    @Transactional(rollbackFor=Exception.class, timeout = 30)
	public void activeRichMenu(String richId, String richMenuId,String adminUserAccount) throws BcsNoticeException{
    	RichMenuContent richMenuContent = contentRichMenuRepository.findOne(richId);
    	richMenuContent.setRichMenuId(richMenuId);
    	richMenuContent.setStatus(RichMenuContent.STATUS_ACTIVE);
    	richMenuContent.setModifyTime(new Date());
    	richMenuContent.setModifyUser(adminUserAccount);
		contentRichMenuRepository.save(richMenuContent);
		
		dataCache.invalidate(richMenuContent.getRichId());
	}
}
