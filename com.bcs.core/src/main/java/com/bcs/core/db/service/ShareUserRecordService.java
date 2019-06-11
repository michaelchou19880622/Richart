package com.bcs.core.db.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jcodec.common.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.ShareUserRecord;
import com.bcs.core.db.repository.ShareUserRecordRepository;

@Service
public class ShareUserRecordService {
		
	@Autowired
	private ShareUserRecordRepository shareUserRecordRepository;
	@PersistenceContext
    EntityManager entityManager;
	
	public void save(ShareUserRecord userRecord) {
	    shareUserRecordRepository.save(userRecord);
	}
	
	public ShareUserRecord findOne(String shareUserRecordId) {
	    return shareUserRecordRepository.findOne(shareUserRecordId);
	}
	
	public ShareUserRecord findByCampaignIdAndUid(String campaignId, String uid) {
	    return shareUserRecordRepository.findByCampaignIdAndUid(campaignId, uid);
	}
	
	public Integer countByCampaignId(String campaignId){
	    return shareUserRecordRepository.countByCampaignId(campaignId);
	}
	
	public List<Object[]> findByModifyTimeAndCampaignId(Date start, Date end, String campaignId){
	    return shareUserRecordRepository.findByModifyTimeAndCampaignId(start, end, campaignId);
	}
	
    public String generateShareUserRecordId() {
        String shareUserRecordId = UUID.randomUUID().toString().toLowerCase();
        
        while (shareUserRecordRepository.findOne(shareUserRecordId) != null) {
            shareUserRecordId = UUID.randomUUID().toString().toLowerCase();
        }
        return shareUserRecordId;
    }
    
    public List<Object[]> findCompletedByModifyTimeAndCampaignId(Date start, Date end, String campaignId){
        return shareUserRecordRepository.findCompletedByModifyTimeAndCampaignId(start, end, campaignId);
    }
    
    public List<Object[]> findUncompletedByModifyTimeAndCampaignId(Date start, Date end, String campaignId){
        return shareUserRecordRepository.findUncompletedByModifyTimeAndCampaignId(start, end, campaignId);
    }
    
    @SuppressWarnings("unchecked")
    public  Map<String, List<String>> findLatelyUndoneUsers(){
    	String queryString = 
    			"select SHARE_USER_RECORD_ID, CAMPAIGN_ID, UID " + 
    	        		"from BCS_SHARE_USER_RECORD " + 
    	        		"where COMPLETE_STATUS = 'UNDONE' " + 
    	        		"and MODIFY_TIME >= DATEADD(day, -1, GETDATE()) and MODIFY_TIME < GETDATE() "; // -1 = yesterday
    	
    	Query query = entityManager.createNativeQuery(queryString);
		List<Object[]> list = query.getResultList();

    	Map<String, List<String>> map = new LinkedHashMap<>();
		for (Object[] o : list) {
			for (int i=0, max=o.length; i<max; i++) {
				if(i==0){
					map.put(o[0].toString(), new ArrayList<String>());
					continue;
				}
				List<String> dataList = map.get(o[0]);
				if (o[i] == null) {
					dataList.add("");
				} else {
					dataList.add(o[i].toString());
				}
			}
		}
		return map;
    }

    @SuppressWarnings("unchecked")
    public int checkJudgement(String uid, String stateJudgement){
    	String queryString = 
    			"select count(0) from BCS_LINE_USER where MID = '" + uid + "' " + stateJudgement;
    	Query query = entityManager.createNativeQuery(queryString);
		List<Object[]> list = query.getResultList();
		return (list.toString().contentEquals("[1]")?1:0);
    }
}
