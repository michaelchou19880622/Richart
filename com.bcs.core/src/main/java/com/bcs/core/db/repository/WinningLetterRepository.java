//package com.bcs.core.db.repository;
//
//import java.util.List;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.bcs.core.db.entity.WinningLetter;
//import com.bcs.core.db.persistence.EntityRepository;
//
//public interface WinningLetterRepository extends EntityRepository<WinningLetter, String> {
//
//	@Transactional(readOnly = true, timeout = 30)
//	WinningLetter findById(Long id);
//	
//	@Transactional(readOnly = true, timeout = 30)
//	WinningLetter findByName(String name);
//
//	@Transactional(readOnly = true, timeout = 30)
//	List<WinningLetter> findAllByNameContainingAndStatusOrderByCreateTimeDesc(String name, String status);
//
//	@Transactional(readOnly = true, timeout = 30)
//	Page<WinningLetter> findAllByNameContainingAndStatus(String name, String status, Pageable pageable);
//	
//	@Transactional(readOnly = true, timeout = 30)
//	List<WinningLetter> findAllByStatusOrderByCreateTimeDesc(String status);
//	
//	@Transactional(readOnly = true, timeout = 30)
//	Page<WinningLetter> findAllByStatus(String status, Pageable pageable);
//}
