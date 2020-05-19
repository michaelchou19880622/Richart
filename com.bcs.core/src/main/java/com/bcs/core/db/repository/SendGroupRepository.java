package com.bcs.core.db.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.SendGroup;
import com.bcs.core.db.persistence.EntityRepository;

public interface SendGroupRepository extends EntityRepository<SendGroup, Long>{

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT GROUP_ID, GROUP_TITLE FROM BCS_SEND_GROUP ORDER BY GROUP_ID ASC", nativeQuery = true)
	public List<Object[]> findAllGroupIdAndGroupTitle();

	@Transactional(readOnly = true, timeout = 300)
	@Query(value = "SELECT GROUP_ID, GROUP_TITLE FROM BCS_SEND_GROUP WHERE (GROUP_TYPE IS NULL OR GROUP_TYPE = '') ORDER BY GROUP_ID ASC", nativeQuery = true)
	public List<Object[]> findAllGroupIdAndGroupTitleByGroupTypeNullOrEmpty();
	
	@Transactional(readOnly = true, timeout = 300)
	@Query(value = "SELECT GROUP_ID, GROUP_TITLE FROM BCS_SEND_GROUP WHERE (GROUP_TYPE IS NOT NULL AND GROUP_TYPE <> '') ORDER BY GROUP_ID ASC", nativeQuery = true)
	public List<Object[]> findAllGroupIdAndGroupTitleByGroupTypeNotNullAndNotEmpty();

	@Transactional(readOnly = true, timeout = 30)
	@Query("select x.groupTitle from SendGroup x where x.groupId = ?1")
	public String findGroupTitleByGroupId(Long groupId);
	
	@Transactional(readOnly = true, timeout = 300)
	@Query(value = "SELECT * FROM BCS_SEND_GROUP WHERE (GROUP_TYPE IS NULL OR GROUP_TYPE = '') ORDER BY MODIFY_TIME DESC;", nativeQuery = true)
	public List<SendGroup> findAllByGroupTypeNull();
	
	@Transactional(readOnly = true, timeout = 300)
	@Query(value = "SELECT * FROM BCS_SEND_GROUP WHERE (GROUP_TYPE IS NOT NULL AND GROUP_TYPE <> '') ORDER BY GROUP_ID ASC;", nativeQuery = true)
	public List<SendGroup> findAllByGroupTypeNotNull();
	
	@Transactional(readOnly = true, timeout = 300)
	@Query(value = "SELECT * FROM BCS_SEND_GROUP bsg WHERE (bsg.GROUP_TYPE IS NOT NULL AND bsg.GROUP_TYPE <> '') /*#pageable*/", nativeQuery = true)
	public Page<SendGroup> findAllByGroupTypeNotNull(Pageable pageable);
}
