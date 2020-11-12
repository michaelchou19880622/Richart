package com.bcs.core.richart.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.persistence.EntityRepository;
import com.bcs.core.richart.db.entity.LinePointDetail;

public interface LinePointDetailRepository extends EntityRepository<LinePointDetail, Long>{

    @Transactional(timeout = 30)
    @Query(value = "select x from LinePointDetail x where x.status = ?1 and x.linePointMainId = ?2 order by x.triggerTime desc")
	public List<LinePointDetail> findByStatusAndLinePointMainId(String status, Long linePointMainId);

    @Transactional(timeout = 300)
    @Query(value = "select count(*) from LinePointDetail x where x.linePointMainId = ?1")
    public Long countByLinePointMainId(Long linePointMainId);

    @Transactional(timeout = 300)
    @Query(value = "select count(*) from LinePointDetail x where x.linePointMainId = ?1 and x.status = ?2")
    public Long countByLinePointMainIdAndStatus(Long linePointMainId, String status);
    
    @Transactional(timeout = 300)
    @Query(value = "SELECT "
                + "SUM(CASE WHEN STATUS = 'FAIL' THEN 1 ELSE 0 END) 'FAIL_COUNT', "
                + "SUM(CASE WHEN STATUS = 'SUCCESS' THEN 1 ELSE 0 END) 'SUCCESS_COUNT' "
                + "FROM BCS_LINE_POINT_DETAIL "
                + "WHERE LINE_POINT_MAIN_ID = ?1 ", nativeQuery = true)
    public List<Object[]> getSuccessAndFailCountByLinePointMainId(Long linePointMainId);
    
    
    
//	public LinePointDetail findBySerialId(String serialId);
//	
//	public List<LinePointDetail> findByStatus(String status);
//	
//	public List<LinePointDetail> findByMsgLpId(long msgLpId);
//	
//	@Query(value="SELECT * FROM BCS_LINE_POINT_DETAIL WHERE SERIAL_ID = ?1 AND UID is null", nativeQuery=true)
//	public abstract List<LinePointDetail> findBySerialIdAndEmptyUid(String paramString);
//	 
//	@Query(value = "SELECT * FROM BCS_LINE_POINT_DETAIL WHERE MSG_LP_ID = ?1 AND UID is null", nativeQuery = true)
//	public List<LinePointDetail> findByMsgLpIdAndEmptyUid(long msgLpId);
//	
//	@Modifying
//	@Transactional
//	@Query(value = "update BCS_LINE_POINT_DETAIL set UID=:uid, GET_TIME=GetDate(), SOURCE='API' where MSG_LP_ID = :msgLpId and SERIAL_ID= :serialId", nativeQuery = true)
//	public void updateUidByByMsgLpIdAndSerialId(@Param("msgLpId") long msgLpId, @Param("serialId") String serialId, @Param("uid") String uid);	

//	public List<LinePointSend> findByMsgLpId(Long msgLpId);
//	
//	public List<LinePointSend> findByMsgLpIdAndStatus(Long msgLpId, String status);
//	
//	public List<LinePointSend> findByMainId(Long mainId);
//	
//	public List<LinePointSend> findByMsgLpIdAndUidAndStatus(Long msgLpId, String uid, String status);
//	
//	public List<LinePointSend> findByMsgLpIdAndUid(Long msgLpId, String uid);
//	
//	@Query(value="SELECT COUNT(lp.uid) FROM LinePointSend lp WHERE lp.msgLpId = :msgLpId and lp.status = :status ")
//	public int countByMsgLpIdAndStatus(@Param("msgLpId") long msgLpId, @Param("status")  String status);
//	
//	@Modifying
//	@Transactional
//	@Query("update LinePointSend msg set msg.status = :status, msg.responseCode = :responseCode  where msg.msgLpId = :msgLpId and msg.uid= :uid")
//	public void updateStautsAndRespCodeByUidAndId(@Param("msgLpId") long msgLpId, @Param("status")  String status, @Param("responseCode") int responseCode, @Param("uid") String uid);
//	
//	@Modifying
//	@Transactional
//	@Query("update LinePointSend msg set msg.status = :status, msg.responseCode = :responseCode  where msg.msgLpId = :msgLpId" )
//	public void updateStautsAndRespCode(@Param("msgLpId") long msgLpId, @Param("status")  String status, @Param("responseCode") int responseCode);
}
