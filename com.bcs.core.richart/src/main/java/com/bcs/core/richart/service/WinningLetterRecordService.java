package com.bcs.core.richart.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.db.repository.WinningLetterRecordRepository;

@Service
public class WinningLetterRecordService {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(WinningLetterRecordService.class);

	@Autowired
	private WinningLetterRecordRepository winningLetterRecordRepository;

//	@Transactional(rollbackFor = Exception.class, timeout = 30)
//	public void createWinningLetter(WinningLetterModel winningLetterModel) throws Exception {
//		logger.info("WinningLetterService - createWinningLetter");
//		
//		long startTime = System.nanoTime();
//		logger.info("createWinningLetter : START TIME = {}", startTime);
//		
//		logger.info("winningLetterModel = {}", winningLetterModel.toString());
//		
//		
//
//		long endTime = System.nanoTime();
//		logger.info("createWinningLetter : END TIME = {}", endTime);
//		logger.info("createWinningLetter : ELAPSED TIME = {}\n", (endTime - startTime) / 1_000_000_000);
//	}
	
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
//	
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
//	
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
