package com.bcs.core.richart.db.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.richart.db.entity.LinePointDetail;
import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.richart.db.repository.LinePointDetailRepository;
import com.bcs.core.richart.db.repository.LinePointMainRepository;

@Service
public class LinePointDetailService {
	/** Logger */
	private static Logger logger = Logger.getLogger(LinePointDetailService.class);
	@Autowired
	private LinePointDetailRepository linePointDetailRepository;

    @PersistenceContext
    EntityManager entityManager;
    
	public void delete(Long msgId){
		LinePointDetail main = linePointDetailRepository.findOne(msgId);
		main.setStatus(LinePointMain.MESSAGE_STATUS_DELETE);
		this.save(main);
	}
    
	public void save(LinePointDetail linePoint){
		linePointDetailRepository.save(linePoint);
	}
    
	public LinePointDetail findOne(Long msgId){
		return linePointDetailRepository.findOne(msgId);
	}
    
//	public LinePointDetail findBySerialId(String serialId){
//		return linePointDetailRepository.findBySerialId(serialId);
//	}
//		
//	public List<LinePointDetail> findByStatus(String status){
//		return linePointDetailRepository.findByStatus(status);
//	}	
//
//	public List<LinePointDetail> findByMsgLpId(long msgLpId){
//		return linePointDetailRepository.findByMsgLpId(msgLpId);
//	}	
//	
//	public List<LinePointDetail> findByMsgLpIdAndEmptyUid(long msgLpId){
//		return linePointDetailRepository.findByMsgLpIdAndEmptyUid(msgLpId);
//	}
//	
//	public List<LinePointDetail> findBySerialIdAndEmptyUid(String serialId)
//	{
//	   return this.linePointDetailRepository.findBySerialIdAndEmptyUid(serialId);
//	}
//	  
//	public void updateUID(Long msgLpId,String serialID, String uid){
//		linePointDetailRepository.updateUidByByMsgLpIdAndSerialId(msgLpId, serialID, uid );
//	}
}
