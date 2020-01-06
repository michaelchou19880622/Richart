package com.bcs.core.db.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.db.persistence.EntityRepository;

public interface WinningLetterRecordRepository extends EntityRepository<WinningLetterRecord, String> {

	@Transactional(readOnly = true, timeout = 30)
	WinningLetterRecord findById(Long id);

	@Transactional(readOnly = true, timeout = 300)
	List<WinningLetterRecord> findAllByWinningLetterIdOrderByIdAsc(Long winningLetterId);

	@Transactional(readOnly = true, timeout = 300)
	List<WinningLetterRecord> findAllByNameContainingAndWinningLetterIdOrderByIdAsc(String name, Long winningLetterId);

	@Transactional(readOnly = true, timeout = 300)
	Page<WinningLetterRecord> findAllByNameContainingAndWinningLetterIdOrderByIdAsc(String name, Long winningLetterId, Pageable pageable);

	@Transactional(readOnly = true, timeout = 300)
	@Query(value = "SELECT COUNT(*) AS REPLY_PEOPLE_COUNT FROM BCS_WINNING_LETTER_RECORD WHERE WINNING_LETTER_ID = ?1", nativeQuery = true)
	Integer countByWinningLetterId(String winningLetterId);
}
