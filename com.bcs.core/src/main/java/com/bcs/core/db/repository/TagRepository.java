package com.bcs.core.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.Tag;
import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.persistence.EntityRepository;

public interface TagRepository extends EntityRepository<Tag, String> {
	
	@Transactional(readOnly = true, timeout = 30)
	Tag findById(Long id);
	
	@Transactional(readOnly = true, timeout = 30)
	Tag findByTagName(String name);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT * FROM BCS_TAG t WHERE t.STATUS = 1 /*#pageable*/ ", nativeQuery = true)
	public Page<Tag> findAllActive(Pageable pageable);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value="SELECT * from BCS_TAG t WHERE (t.TAG_NAME LIKE :search AND t.STATUS = 1) OR (t.MODIFY_USER LIKE :search AND t.STATUS = 1) /*#pageable*/ ", nativeQuery = true)
	public Page<Tag> findSearchResult(@Param("search") String search, Pageable pageable);
}
