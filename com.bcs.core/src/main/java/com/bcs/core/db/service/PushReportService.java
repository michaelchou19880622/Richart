package com.bcs.core.db.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.PushReport;
import com.bcs.core.db.repository.PushReportRepository;

@Service
public class PushReportService {
	
	/** Logger */
	private static Logger logger = LogManager.getLogger(PushReportService.class);
	
	@Autowired
	private PushReportRepository pushReportRepository;
	
	public PushReport findOne(Long reportId){
		logger.info("findOne:" + reportId);
		return pushReportRepository.findOne(reportId);
	}
}
