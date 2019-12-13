package com.bcs.core.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.persistence.EntityRepository;

public interface WinningLetterRepository extends EntityRepository<WinningLetter, String> {

	@Transactional(readOnly = true, timeout = 30)
	WinningLetter findById(Long id);
	
	@Transactional(readOnly = true, timeout = 30)
	WinningLetter findByName(String name);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT * FROM BCS_WINNING_LETTER_TEST WHERE STATUS = ?1", nativeQuery = true)
	List<WinningLetter> findAllByStatus(String status);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT * FROM BCS_WINNING_LETTER_TEST WHERE NAME LIKE ?1 AND STATUS = ?2", nativeQuery = true)
	List<WinningLetter> findByLikeNameAndStatus(String name, String status);

//	@Query("select x.mid from LineUser x where x.mid in ( ?1 ) and (x.status = 'BINDED' or x.status = 'UNBIND')")
//	List<String> findMidByMidInAndActive(List<String> mids);

//	@Transactional(readOnly = true, timeout = 30)
//	Long countByStatus(String status);

//	@Transactional(readOnly = true, timeout = 30)
//	@Query(value = "SELECT COUNT(DISTINCT MID) FROM BCS_LINE_USER WHERE  STATUS = ?1 AND CREATE_TIME >= ?2 AND CREATE_TIME < ?3", nativeQuery = true)
//	public Long countByStatus(String status, String start, String end);
}
