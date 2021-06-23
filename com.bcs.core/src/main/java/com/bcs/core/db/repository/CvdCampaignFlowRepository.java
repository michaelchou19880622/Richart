package com.bcs.core.db.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.CvdCampaignFlow;
import com.bcs.core.db.persistence.EntityRepository;

@Repository
public interface CvdCampaignFlowRepository extends EntityRepository<CvdCampaignFlow, String> {

	@Transactional(readOnly = true, timeout = 30)
	CvdCampaignFlow findByUid(String uid);

	@Transactional(readOnly = true, timeout = 30)
	CvdCampaignFlow findByUidAndStatus(String uid, String status);
}
