package com.bcs.core.richmenu.core.db.repository;

import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.persistence.EntityRepository;
import com.bcs.core.richmenu.core.db.entity.RichMenuSendGroupQueryTag;

public interface RichMenuSendGroupQueryTagRepository extends EntityRepository<RichMenuSendGroupQueryTag, Long>{

	@Transactional(readOnly = true, timeout = 30)
	public long countBySendGroupQueryQueryFieldIdAndQueryFieldTagValue(String queryFieldId, String queryFieldTagValue);
}
