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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.ShareDonatorRecord;
import com.bcs.core.db.entity.ShareUserRecord;
import com.bcs.core.db.repository.ShareDonatorRecordRepository;

@Service
public class ShareDonatorRecordService {
	/** Logger */
	private static Logger logger = Logger.getLogger(ShareDonatorRecordService.class);
	@Autowired
	private ShareDonatorRecordRepository shareDonatorRecordRepository;
	@PersistenceContext
    EntityManager entityManager;
	
	public void save(ShareDonatorRecord shareDonatorRecord) {
	    shareDonatorRecordRepository.save(shareDonatorRecord);
	}
	
	public ShareDonatorRecord findOne(String shareUserRecordId) {
	    return shareDonatorRecordRepository.findOne(shareUserRecordId);
	}
	
	public List<ShareDonatorRecord> findByDonatorUid(String donatorUid) {
	    return shareDonatorRecordRepository.findByDonatorUid(donatorUid);
	}
	
	
//	public ShareUserRecord findByCampaignIdAndUid(String campaignId, String uid) {
//	    return shareDonatorRecordRepository.findByCampaignIdAndUid(campaignId, uid);
//	}
//	
//	public Integer countByCampaignId(String campaignId){
//	    return shareDonatorRecordRepository.countByCampaignId(campaignId);
//	}
//	
//	public List<Object[]> findByModifyTimeAndCampaignId(Date start, Date end, String campaignId){
//	    return shareDonatorRecordRepository.findByModifyTimeAndCampaignId(start, end, campaignId);
//	}
//	
//    public String generateShareUserRecordId() {
//        String shareUserRecordId = UUID.randomUUID().toString().toLowerCase();
//        
//        while (shareDonatorRecordRepository.findOne(shareUserRecordId) != null) {
//            shareUserRecordId = UUID.randomUUID().toString().toLowerCase();
//        }
//        return shareUserRecordId;
//    }
//    
//    public List<Object[]> findCompletedByModifyTimeAndCampaignId(Date start, Date end, String campaignId){
//        return shareDonatorRecordRepository.findCompletedByModifyTimeAndCampaignId(start, end, campaignId);
//    }
//    
//    public List<Object[]> findUncompletedByModifyTimeAndCampaignId(Date start, Date end, String campaignId){
//        return shareDonatorRecordRepository.findUncompletedByModifyTimeAndCampaignId(start, end, campaignId);
//    }
//    
//    public List<ShareUserRecord> findLatelyUndoneUsers(){
//    	return shareDonatorRecordRepository.findLatelyUndoneUsers();
//    }
//
//    @SuppressWarnings("unchecked")
//    public boolean checkJudgment(String uid, String stateJudgement){
//    	String queryString = 
//    			"select count(0) from BCS_LINE_USER where MID = '" + uid + "' " + stateJudgement;
//    	logger.info("[checkJudgement] queryString:"+queryString);
//    	
//    	Query query = entityManager.createNativeQuery(queryString);
//		List<Object[]> list = query.getResultList();
//		
//		logger.info("[checkJudgement] check result :"+list.toString());
//		logger.info("[checkJudgement] check bool :"+list.toString().contentEquals("[1]"));
//		return (list.toString().contentEquals("[1]")); // [1] or [0]
//    }
}
