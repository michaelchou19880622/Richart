package com.bcs.core.richart.db.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.richart.db.entity.LinePointDetail;
import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.richart.db.repository.LinePointDetailRepository;

@Service
public class LinePointDetailService {
	/** Logger */
	private static Logger logger = LogManager.getLogger(LinePointDetailService.class);
	@Autowired
	private LinePointDetailRepository linePointDetailRepository;

    @PersistenceContext
    EntityManager entityManager;
    
	public void delete(Long msgId){
		LinePointDetail main = linePointDetailRepository.findOne(msgId);
		main.setStatus(LinePointMain.STATUS_DELETE);
		this.save(main);
	}
    
	public void save(LinePointDetail linePoint){
		linePointDetailRepository.save(linePoint);
	}
    
	public void save(List<LinePointDetail> linePoint){
		linePointDetailRepository.save(linePoint);
	}
	
	public LinePointDetail findOne(Long msgId){
		return linePointDetailRepository.findOne(msgId);
	}
    
	public List<LinePointDetail> findSuccess(Long linePointMainId){
		return linePointDetailRepository.findByStatusAndLinePointMainId(LinePointDetail.STATUS_SUCCESS, linePointMainId);
	}
	
	public List<LinePointDetail> findFail(Long linePointMainId){
		return linePointDetailRepository.findByStatusAndLinePointMainId(LinePointDetail.STATUS_FAIL, linePointMainId);
	}
	
	public Long getCount(Long linePointMainId) {
	    return linePointDetailRepository.countByLinePointMainId(linePointMainId);
	}
    
    public Long getCountByMainIdAndStatus(Long linePointMainId, String Status) {
        return linePointDetailRepository.countByLinePointMainIdAndStatus(linePointMainId, Status);
    }
    
    public Map<String, Long> getSuccessAndFailCountByLinePointMainId (Long linePointMainId) {
        List<Object[]> result = linePointDetailRepository.getSuccessAndFailCountByLinePointMainId(linePointMainId);
        Long failCount = 0L;
        Long successCount = 0L;
        Map<String, Long> map = new HashedMap<String, Long>();
        if (result != null && result.size() != 0) {
            Object[] objStatusCount = result.get(0);
            failCount = Long.parseLong(objStatusCount[0].toString());
            successCount = Long.parseLong(objStatusCount[1].toString());
        }
        map.put("fail", failCount);
        map.put("success", successCount);
        return map;
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
