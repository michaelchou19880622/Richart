package com.bcs.core.richmenu.core.db.service;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.akka.service.AkkaCoreService;
import com.bcs.core.richmenu.core.db.entity.RichMenuSendGroupDetail;
import com.bcs.core.richmenu.core.db.repository.RichMenuGroupGenerateRepository;

@Service
public class RichMenuGroupGenerateService {
	
	/** Logger */
	private static Logger logger = LogManager.getLogger(RichMenuGroupGenerateService.class);
	@Autowired
	private AkkaCoreService akkaService;
	@Autowired
	private RichMenuGroupGenerateRepository groupGenerateRepository;

	public BigInteger findMIDCountBySendGroupDetail(List<RichMenuSendGroupDetail> sendGroupDetails) throws Exception {
		logger.debug("findMIDCountBySendGroupDetail");
		return groupGenerateRepository.findMIDCountBySendGroupDetail(sendGroupDetails);
	}

	public List<String> findMIDBySendGroupDetailGroupId(Long groupId) throws Exception{
		logger.info("findMIDBySendGroupDetailGroupId");
		List<String> list = groupGenerateRepository.findMIDBySendGroupDetailGroupId(groupId);
		logger.info("list:"+list);
		return list;
	}
	
	public Boolean checkMIDBySendGroupDetailGroupId(Long groupId, String mid) throws Exception{
		String result = groupGenerateRepository.checkMIDBySendGroupDetailGroupId(groupId, mid);
		logger.debug("checkMIDBySendGroupDetailGroupId:" + result);
		if(StringUtils.isBlank(result)){
			return false;
		}
		else{
			return true;
		}
	}

	public List<String> findMIDBySendGroupDetail(List<RichMenuSendGroupDetail> sendGroupDetails) throws Exception{
		logger.debug("findMIDBySendGroupDetail");
		return groupGenerateRepository.findMIDBySendGroupDetail(sendGroupDetails);
	}
}
