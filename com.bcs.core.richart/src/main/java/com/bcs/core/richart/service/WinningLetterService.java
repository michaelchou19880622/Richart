package com.bcs.core.richart.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.LineUser;
import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.repository.WinningLetterRepository;
import com.bcs.core.richart.api.model.WinningLetterModel;

@Service
public class WinningLetterService {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(WinningLetterService.class);

	@Autowired
	private WinningLetterRepository winningLetterRepository;

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
//		logger.info("createWinningLetter : ELAPSED TIME = {}\n", (endTime - startTime));
//	}
	
	public List<WinningLetter> findAll() {
		return winningLetterRepository.findAll();
	}
	
	public WinningLetter findById(Long id) {
		return winningLetterRepository.findById(id);
	}
	
	public WinningLetter findByName(String name) {
		return winningLetterRepository.findByName(name);
	}
	
	public Long save(WinningLetter winningLetter) {
		WinningLetter winningLetterNew = winningLetterRepository.save(winningLetter);
		
		return winningLetterNew.getId();
	}
}
