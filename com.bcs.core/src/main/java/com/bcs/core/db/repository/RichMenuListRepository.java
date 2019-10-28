package com.bcs.core.db.repository;

import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.RichMenuList;
import com.bcs.core.db.persistence.EntityRepository;

public interface RichMenuListRepository extends EntityRepository<RichMenuList, Long>{

//	@Transactional(readOnly = true, timeout = 30)
//	@Query(value = "SELECT * FROM richmenu_list WHERE customId = ?1", nativeQuery = true)

	@Transactional(readOnly = true, timeout = 30)
	public RichMenuList findByCustomeId(Long customeId);
	
}
