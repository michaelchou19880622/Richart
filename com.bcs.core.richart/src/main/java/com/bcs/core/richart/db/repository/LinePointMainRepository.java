package com.bcs.core.richart.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.db.persistence.EntityRepository;

public interface LinePointMainRepository extends EntityRepository<LinePointMain, Long>{
	public LinePointMain findBySerialId(String serialId);
	public List<LinePointMain> findByStatus(String status);
	public List<LinePointMain> findBySendType(String sendType);
}
