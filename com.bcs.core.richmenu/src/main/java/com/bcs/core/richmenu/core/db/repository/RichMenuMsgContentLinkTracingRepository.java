package com.bcs.core.richmenu.core.db.repository;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.richmenu.core.db.entity.RichMenuMsgContentLinkTracing;
import com.bcs.core.db.persistence.EntityRepository;


public interface RichMenuMsgContentLinkTracingRepository extends EntityRepository<RichMenuMsgContentLinkTracing, Long>{
	
	@Transactional(readOnly = true, timeout = 30)
	public List<RichMenuMsgContentLinkTracing> findByLinkId(String linkId);
}
