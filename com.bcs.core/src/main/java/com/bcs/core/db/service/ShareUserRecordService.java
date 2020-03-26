package com.bcs.core.db.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.ShareUserRecord;
import com.bcs.core.db.repository.ShareUserRecordRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public List<Object[]> findByModifyTimeAndCampaignIdWithDonateStatus(Date start, Date end, String campaignId){
	    return shareUserRecordRepository.findByModifyTimeAndCampaignIdWithDonateStatus(start, end, campaignId);
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
    
    public List<ShareUserRecord> findLatelyUndoneUsers(){
    	return shareUserRecordRepository.findLatelyUndoneUsers();
    }

    @SuppressWarnings("unchecked")
    public boolean checkJudgment(String uid, String stateJudgement){
		log.info("[checkJudgement] uid = {}", uid);
		log.info("[checkJudgement] stateJudgement = {}", stateJudgement);
    	
		String queryString = "select count(0) from BCS_LINE_USER where MID = '" + uid + "' " + stateJudgement;
		log.info("[checkJudgement] queryString = {}", queryString);

		Query query = entityManager.createNativeQuery(queryString);
		
		List<Object[]> list = query.getResultList();
		log.info("[checkJudgement] check list = {}", list);
		log.info("[checkJudgement] check list.toString() = {}", list.toString());
		log.info("[checkJudgement] check list.toString().contentEquals(\"[1]\") = {}", list.toString().contentEquals("[1]"));
		
		return (list.toString().contentEquals("[1]")); // [1] or [0]
    }
}
