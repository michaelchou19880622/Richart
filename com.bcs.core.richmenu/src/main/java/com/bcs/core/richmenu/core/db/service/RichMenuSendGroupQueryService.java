package com.bcs.core.richmenu.core.db.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.richmenu.core.db.entity.RichMenuSendGroupQuery;
import com.bcs.core.richmenu.core.db.repository.RichMenuSendGroupQueryRepository;


@Service
public class RichMenuSendGroupQueryService {
	/** Logger */
	private static Logger logger = LogManager.getLogger(RichMenuSendGroupQueryService.class);
	
	@Autowired
	private RichMenuSendGroupQueryRepository sendGroupQueryRepository;

	public boolean exists(String queryFieldId) {
		return sendGroupQueryRepository.exists(queryFieldId);
	}
	
	public List<RichMenuSendGroupQuery> findAll(){
		return sendGroupQueryRepository.findAll();
	}
	
	public RichMenuSendGroupQuery findOne(String queryFieldId){
		return sendGroupQueryRepository.findOne(queryFieldId);
	}
	
	public void save(RichMenuSendGroupQuery sendGroupQuery){
		sendGroupQueryRepository.save(sendGroupQuery);
	}
	
	@Transactional(rollbackFor=Exception.class, timeout = 30)
	public void delete(String queryFieldId){
		logger.debug("queryFieldId:" + queryFieldId);
		RichMenuSendGroupQuery sendGroupQuery = sendGroupQueryRepository.findOne(queryFieldId);
		
		sendGroupQueryRepository.delete(sendGroupQuery);
	}
}
