package com.bcs.core.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.db.persistence.EntityRepository;

public interface WinningLetterRecordRepository extends EntityRepository<WinningLetterRecord, String> {

	@Transactional(readOnly = true, timeout = 30)
	WinningLetterRecord findById(Long id);

	@Transactional(readOnly = true, timeout = 300)
	List<WinningLetterRecord> findAllByWinningLetterId(Long winningLetterId);

	@Transactional(readOnly = true, timeout = 300)
	List<WinningLetterRecord> findAllByNameContainingAndWinningLetterId(String name, Long winningLetterId);

//	@Transactional(readOnly = true, timeout = 300)
//	@Query(value = "SELECT COUNT(*) AS REPLY_PEOPLE_COUNT FROM BCS_WINNING_LETTER_RECORD_TEST WHERE WINNING_LETTER_ID = ?1", nativeQuery = true)
//	List<WinningLetterRecord> findAllByNameContainingAndWinningLetterId(String name, Long winningLetterId);
//	
	@Transactional(readOnly = true, timeout = 300)
	@Query(value = "SELECT COUNT(*) AS REPLY_PEOPLE_COUNT FROM BCS_WINNING_LETTER_RECORD_TEST WHERE WINNING_LETTER_ID = ?1", nativeQuery = true)
	Integer countByWinningLetterId(String winningLetterId);
}
