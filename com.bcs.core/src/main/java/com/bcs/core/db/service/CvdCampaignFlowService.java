package com.bcs.core.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.CvdCampaignFlow;
import com.bcs.core.db.repository.CvdCampaignFlowRepository;

@Service
public class CvdCampaignFlowService {
	
	@Autowired
	private CvdCampaignFlowRepository cvdCampaignFlowRepository;
	
    public CvdCampaignFlowService(){
    	
    }

    public CvdCampaignFlow findByUid(String uid) {
    	return cvdCampaignFlowRepository.findByUid(uid);
    }
    
    public CvdCampaignFlow findByUidAndStatus(String uid, String status) {
    	return cvdCampaignFlowRepository.findByUidAndStatus(uid, status);
    }
    
    public CvdCampaignFlow save(CvdCampaignFlow springTreeCampaignFlow) {
    	return cvdCampaignFlowRepository.save(springTreeCampaignFlow);
    }
}
