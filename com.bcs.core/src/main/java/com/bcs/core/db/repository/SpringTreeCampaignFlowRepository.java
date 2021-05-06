package com.bcs.core.db.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.SpringTreeCampaignFlow;
import com.bcs.core.db.persistence.EntityRepository;

@Repository
public interface SpringTreeCampaignFlowRepository extends EntityRepository<SpringTreeCampaignFlow, String> {

	@Transactional(readOnly = true, timeout = 30)
	SpringTreeCampaignFlow findByUid(String uid);

	@Transactional(readOnly = true, timeout = 30)
	SpringTreeCampaignFlow findByUidAndStatus(String uid, String status);
}
