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
			+ "bwlt.NAME, "
			+ "bwlt.STATUS, "
			+ "(SELECT COUNT(bwlrt.ID) FROM BCS_WINNING_LETTER_RECORD_TEST bwlrt WHERE bwlrt.WINNING_LETTER_ID = bwlt.ID) AS REPLY, "
			+ "bwlt.ID, "
			+ "bwlt.END_TIME, "
			+ "bwlt.CREATE_TIME, "
			+ "bwlt.CREATE_USER, "
			+ "bwlt.MODIFY_TIME, "
			+ "bwlt.MODIFY_USER "
			+ "FROM BCS_WINNING_LETTER_TEST bwlt WHERE bwlt.NAME LIKE '%:name%' AND bwlt.STATUS = :status", nativeQuery = true)
	List<Object[]> findSummaryReportByLikeNameAndStatus(@Param("name") String name, @Param("status") String status);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "bwlt.NAME, "
			+ "bwlt.STATUS, "
			+ "(SELECT COUNT(bwlrt.ID) FROM BCS_WINNING_LETTER_RECORD_TEST bwlrt WHERE bwlrt.WINNING_LETTER_ID = bwlt.ID) AS REPLY, "
			+ "bwlt.ID, "
			+ "bwlt.END_TIME, "
			+ "bwlt.CREATE_TIME, "
			+ "bwlt.CREATE_USER, "
			+ "bwlt.MODIFY_TIME, "
			+ "bwlt.MODIFY_USER "
			+ "FROM BCS_WINNING_LETTER_TEST bwlt WHERE bwlt.STATUS = :status", nativeQuery = true)
	List<Object[]> findSummaryReportByStatus(@Param("status") String status);
}
