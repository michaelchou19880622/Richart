package com.bcs.core.db.repository;

import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.db.persistence.EntityRepository;

public interface WinningLetterRecordRepository extends EntityRepository<WinningLetterRecord, String> {

	@Transactional(readOnly = true, timeout = 30)
	WinningLetterRecord findById(Long id);
}
