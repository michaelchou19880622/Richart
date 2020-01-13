package com.bcs.core.richart.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.repository.WinningLetterRepository;

@Service
public class WinningLetterService {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(WinningLetterService.class);

	@Autowired
	private WinningLetterRepository winningLetterRepository;

	public List<WinningLetter> findAll() {
		long startTime = System.nanoTime();
		logger.info("[ findAll ] Start Time : {}", startTime);
		
		List<WinningLetter> lst_WinningLetter = winningLetterRepository.findAll();

		long endTime = System.nanoTime();
		logger.info("[ findAll ] End Time : {}", endTime);
		logger.info("[ findAll ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findAll ] lst_WinningLetter = {}", lst_WinningLetter);
		
		return lst_WinningLetter;
	}
	
	public List<WinningLetter> findAllByStatusOrderByCreatetimeDesc(String status) {
		long startTime = System.nanoTime();
		logger.info("[ findAllByStatus ] Start Time : {}", startTime);
		
		List<WinningLetter> lst_WinningLetter = winningLetterRepository.findAllByStatusOrderByCreateTimeDesc(status);

		long endTime = System.nanoTime();
		logger.info("[ findAllByStatus ] End Time : {}", endTime);
		logger.info("[ findAllByStatus ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findAllByStatus ] lst_WinningLetter = {}", lst_WinningLetter);
		
		return lst_WinningLetter;
	}

	public Page<WinningLetter> findAllByStatus(String status, Pageable pageable) {
		long startTime = System.nanoTime();
		logger.info("[ findAllByStatus ] Start Time : {}", startTime);
		
		Page<WinningLetter> page_WinningLetter = winningLetterRepository.findAllByStatus(status, pageable);

		long endTime = System.nanoTime();
		logger.info("[ findAllByStatus ] End Time : {}", endTime);
		logger.info("[ findAllByStatus ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findAllByStatus ] page_WinningLetter.getContent() = {}", page_WinningLetter.getContent());
		
		return page_WinningLetter;
	}
	
	public List<WinningLetter> findAllByNameContainingAndStatusOrderByCreateTimeDesc(String name, String status) {
		long startTime = System.nanoTime();
		logger.info("[ findAllByNameContainingAndStatusOrderByCreateTimeDesc ] Start Time : {}", startTime);
		
		List<WinningLetter> lst_WinningLetter = winningLetterRepository.findAllByNameContainingAndStatusOrderByCreateTimeDesc(name, status);

		long endTime = System.nanoTime();
		logger.info("[ findAllByNameContainingAndStatusOrderByCreateTimeDesc ] End Time : {}", endTime);
		logger.info("[ findAllByNameContainingAndStatusOrderByCreateTimeDesc ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findAllByNameContainingAndStatusOrderByCreateTimeDesc ] lst_WinningLetter = {}", lst_WinningLetter);
		
		return lst_WinningLetter;
	}
	
	public Page<WinningLetter> findAllByNameContainingAndStatus(String name, String status, Pageable pageable) {
		long startTime = System.nanoTime();
		logger.info("[ findAllByNameContainingAndStatus ] Start Time : {}", startTime);
		
		Page<WinningLetter> page_WinningLetter = winningLetterRepository.findAllByNameContainingAndStatus(name, status, pageable);

		long endTime = System.nanoTime();
		logger.info("[ findAllByNameContainingAndStatus ] End Time : {}", endTime);
		logger.info("[ findAllByNameContainingAndStatus ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findAllByNameContainingAndStatus ] page_WinningLetter.getContent() = {}", page_WinningLetter.getContent());
		
		return page_WinningLetter;
	}
	
	public WinningLetter findById(Long id) {
		long startTime = System.nanoTime();
		logger.info("[ findById ] Start Time : {}", startTime);
		
		WinningLetter winningLetter = winningLetterRepository.findById(id);

		long endTime = System.nanoTime();
		logger.info("[ findById ] End Time : {}", endTime);
		logger.info("[ findById ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findById ] winningLetter = {}", winningLetter);
		
		return winningLetter;
	}
	
	public WinningLetter findByName(String name) {
		long startTime = System.nanoTime();
		logger.info("[ findByName ] Start Time : {}", startTime);
		
		WinningLetter winningLetter = winningLetterRepository.findByName(name);

		long endTime = System.nanoTime();
		logger.info("[ findByName ] End Time : {}", endTime);
		logger.info("[ findByName ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findByName ] winningLetter = {}", winningLetter);
		
		return winningLetter;
	}
	
	public Long save(WinningLetter winningLetterSrc) {
		long startTime = System.nanoTime();
		logger.info("[ save ] Start Time : {}", startTime);
		
		WinningLetter winningLetter = winningLetterRepository.save(winningLetterSrc);

		long endTime = System.nanoTime();
		logger.info("[ save ] End Time : {}", endTime);
		logger.info("[ save ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ save ] winningLetter = {}", winningLetter);
		
		return winningLetter.getId();
	}

	public Long saveWithUserAccount(WinningLetter winningLetterSrc, String adminUserAccount) {
		long startTime = System.nanoTime();
		logger.info("[ saveWithUserAccount ] Start Time : {}", startTime);
		
		WinningLetter winningLetter = winningLetterRepository.save(winningLetterSrc);

		long endTime = System.nanoTime();
		logger.info("[ saveWithUserAccount ] End Time : {}", endTime);
		logger.info("[ saveWithUserAccount ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ saveWithUserAccount ] winningLetter = {}", winningLetter);
		
		return winningLetter.getId();
	}
}
