package com.bcs.core.db.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.LineUser;
import com.bcs.core.db.entity.UserUploadList;
import com.bcs.core.db.persistence.EntityRepository;


public interface UserUploadListRepository extends EntityRepository<UserUploadList, Long>{

	@Transactional(readOnly = true, timeout = 30)
	public Page<UserUploadList> findByReferenceId(String referenceId, Pageable pageable);

	@Transactional(readOnly = true, timeout = 60)
	public Page<UserUploadList> findByReferenceIdAndIsExist(String referenceId, int isExist, Pageable pageable);
	
	@Transactional(readOnly = true, timeout = 30)
	public Long countByReferenceId(String referenceId);
	
	@Transactional(readOnly = true, timeout = 30)
	public Long countByReferenceIdAndIsExist(String referenceId, int isExist);
	
	public Long deleteByReferenceId(String referenceId);

    @Modifying
    @Transactional(timeout = 600)	
    @Query(value = " UPDATE BCS_USER_UPLOAD_LIST SET IS_EXIST = 1 " 
              +    " WHERE EXISTS (SELECT * FROM BCS_LINE_USER WHERE UPPER(BCS_LINE_USER.MID) = UPPER(BCS_USER_UPLOAD_LIST.MID) "
              +    " AND BCS_USER_UPLOAD_LIST.REFERENCE_ID = ?1) ", nativeQuery = true)
	public void updateUserIsExist(String referenceId);	
	
			   
}
