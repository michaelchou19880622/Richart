package com.bcs.core.richart.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.db.repository.WinningLetterRecordRepository;

@Service
public class WinningLetterRecordService {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(WinningLetterRecordService.class);

	@Autowired
	private WinningLetterRecordRepository winningLetterRecordRepository;

	public List<WinningLetterRecord> findAll() {
		long startTime = System.nanoTime();
		logger.info("[ findAll ] Start Time : {}", startTime);
		
		List<WinningLetterRecord> lst_WinningLetterRecord = winningLetterRecordRepository.findAll();

		long endTime = System.nanoTime();
		logger.info("[ findAll ] End Time : {}", endTime);
		logger.info("[ findAll ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findAll ] lst_WinningLetterRecord = {}", lst_WinningLetterRecord);
		
		return lst_WinningLetterRecord;
	}
	
	public List<WinningLetterRecord> findAllByWinningLetterId(Long winningLetterId) {
		long startTime = System.nanoTime();
		logger.info("[ findAllByWinningLetterId ] Start Time : {}", startTime);
		
		List<WinningLetterRecord> lst_WinningLetterRecord = winningLetterRecordRepository.findAllByWinningLetterId(winningLetterId);

		long endTime = System.nanoTime();
		logger.info("[ findAllByWinningLetterId ] End Time : {}", endTime);
		logger.info("[ findAllByWinningLetterId ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findAllByWinningLetterId ] lst_WinningLetterRecord = {}", lst_WinningLetterRecord);
		
		return lst_WinningLetterRecord;
	}
	
	public List<WinningLetterRecord> findAllByNameContainingAndWinningLetterId(String name, Long winningLetterId) {
		long startTime = System.nanoTime();
		logger.info("[ findAllByNameContainingAndWinningLetterId ] Start Time : {}", startTime);
		
		List<WinningLetterRecord> lst_WinningLetterRecord = winningLetterRecordRepository.findAllByNameContainingAndWinningLetterId(name, winningLetterId);

		long endTime = System.nanoTime();
		logger.info("[ findAllByNameContainingAndWinningLetterId ] End Time : {}", endTime);
		logger.info("[ findAllByNameContainingAndWinningLetterId ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ findAllByNameContainingAndWinningLetterId ] lst_WinningLetter = {}", lst_WinningLetterRecord);
		
		return lst_WinningLetterRecord;
	}
	
//	public WinningLetter findById(Long id) {
//		long startTime = System.nanoTime();
//		logger.info("[ findById ] Start Time : {}", startTime);
//		
//		WinningLetter winningLetter = winningLetterRepository.findById(id);
//
//		long endTime = System.nanoTime();
//		logger.info("[ findById ] End Time : {}", endTime);
//		logger.info("[ findById ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);
//
//		logger.info("[ findById ] winningLetter = {}", winningLetter);
//		
//		return winningLetter;
//	}

//	public WinningLetter findByName(String name) {
//		long startTime = System.nanoTime();
//		logger.info("[ findByName ] Start Time : {}", startTime);
//		
//		WinningLetter winningLetter = winningLetterRepository.findByName(name);
//
//		long endTime = System.nanoTime();
//		logger.info("[ findByName ] End Time : {}", endTime);
//		logger.info("[ findByName ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);
//
//		logger.info("[ findByName ] winningLetter = {}", winningLetter);
//		
//		return winningLetter;
//	}
	
	public Long save(WinningLetterRecord winningLetterRecordSrc) {
		long startTime = System.nanoTime();
		logger.info("[ save ] Start Time : {}", startTime);
		
		WinningLetterRecord winningLetterRecord = winningLetterRecordRepository.save(winningLetterRecordSrc);

		long endTime = System.nanoTime();
		logger.info("[ save ] End Time : {}", endTime);
		logger.info("[ save ] Elapsed Time : {} seconds\n", (endTime - startTime) / 1_000_000_000);

		logger.info("[ save ] winningLetterRecord = {}", winningLetterRecord);
		
		return winningLetterRecord.getId();
	}
	
	public Integer countByWinningLetterId(String winningLetterId){
	    return winningLetterRecordRepository.countByWinningLetterId(winningLetterId);
	}
}
