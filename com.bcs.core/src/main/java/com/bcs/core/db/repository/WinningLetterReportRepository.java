package com.bcs.core.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.persistence.EntityRepository;

public interface WinningLetterReportRepository extends EntityRepository<WinningLetter, String> {

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "bwl.NAME, "
			+ "bwl.STATUS, "
			+ "(SELECT COUNT(bwlr.ID) FROM BCS_WINNING_LETTER_RECORD bwlr WHERE bwlr.WINNING_LETTER_ID = bwl.ID) AS REPLY, "
			+ "bwl.ID, "
			+ "bwl.END_TIME, "
			+ "bwl.CREATE_TIME, "
			+ "bwl.CREATE_USER, "
			+ "bwl.MODIFY_TIME, "
			+ "bwl.MODIFY_USER "
			+ "FROM BCS_WINNING_LETTER bwl WHERE bwl.NAME LIKE %:name% AND bwl.STATUS = :status", nativeQuery = true)
	List<Object[]> findSummaryReportByLikeNameAndStatus(@Param("name") String name, @Param("status") String status);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "bwl.NAME, "
			+ "bwl.STATUS, "
			+ "(SELECT COUNT(bwlr.ID) FROM BCS_WINNING_LETTER_RECORD bwlr WHERE bwlr.WINNING_LETTER_ID = bwl.ID) AS REPLY, "
			+ "bwl.ID, "
			+ "bwl.END_TIME, "
			+ "bwl.CREATE_TIME, "
			+ "bwl.CREATE_USER, "
			+ "bwl.MODIFY_TIME, "
			+ "bwl.MODIFY_USER "
			+ "FROM BCS_WINNING_LETTER bwl WHERE bwl.STATUS = :status", nativeQuery = true)
	List<Object[]> findSummaryReportByStatus(@Param("status") String status);
}
