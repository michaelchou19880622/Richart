package com.bcs.core.db.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.SpringTreeCampaignFlow;
import com.bcs.core.db.repository.SpringTreeCampaignFlowRepository;

@Service
public class SpringTreeCampaignFlowService {
	
	@Autowired
	private SpringTreeCampaignFlowRepository springTreeCampaignFlowRepository;
	
    public SpringTreeCampaignFlowService(){
    	
    }

    public SpringTreeCampaignFlow findByUid(String uid) {
    	return springTreeCampaignFlowRepository.findByUid(uid);
    }
    
    public SpringTreeCampaignFlow findByUidAndStatus(String uid, String status) {
    	return springTreeCampaignFlowRepository.findByUidAndStatus(uid, status);
    }
    
    public SpringTreeCampaignFlow save(SpringTreeCampaignFlow springTreeCampaignFlow) {
    	return springTreeCampaignFlowRepository.save(springTreeCampaignFlow);
    }
}
