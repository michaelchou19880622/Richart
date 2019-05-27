package com.bcs.core.richart.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.bcs.core.richart.db.entity.LinePointPushMessageRecord;
import com.bcs.core.db.persistence.EntityRepository;

public interface LinePointPushMessageRecordRepository extends EntityRepository<LinePointPushMessageRecord, Long>{
	@Query(value = "SELECT * FROM BCS_PUSH_LINE_POINT_MESSAGE_RECORD WHERE CREATE_TIME = ?1", nativeQuery = true)
	List<LinePointPushMessageRecord> findByCreateTime(String createTime);
	
	@Query(value = "SELECT * FROM BCS_PUSH_LINE_POINT_MESSAGE_RECORD WHERE ORDER_KEY = ?1", nativeQuery = true)
	List<LinePointPushMessageRecord> findByOrderKey(String orderKey);
}