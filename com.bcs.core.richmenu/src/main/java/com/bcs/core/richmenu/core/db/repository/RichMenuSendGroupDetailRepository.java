package com.bcs.core.richmenu.core.db.repository;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.persistence.EntityRepository;
import com.bcs.core.richmenu.core.db.entity.RichMenuSendGroupDetail;

public interface RichMenuSendGroupDetailRepository extends EntityRepository<RichMenuSendGroupDetail, Long>{
	@Transactional(readOnly = true, timeout = 30)
	public List<RichMenuSendGroupDetail> findBySendGroupGroupId(Long groupId);
}
