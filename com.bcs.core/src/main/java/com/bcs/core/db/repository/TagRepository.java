package com.bcs.core.db.repository;

import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.Tag;
import com.bcs.core.db.persistence.EntityRepository;

public interface TagRepository extends EntityRepository<Tag, String> {
	
	@Transactional(readOnly = true, timeout = 30)
	Tag findById(Long id);
	
	@Transactional(readOnly = true, timeout = 30)
	Tag findByTagName(String name);
}
