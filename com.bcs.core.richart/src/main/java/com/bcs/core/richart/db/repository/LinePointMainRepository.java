package com.bcs.core.richart.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.db.persistence.EntityRepository;

public interface LinePointMainRepository extends EntityRepository<LinePointMain, Long>{
	
	@Transactional(timeout = 30)
	public LinePointMain findBySerialId(String serialId);
    @Transactional(timeout = 30)
    public List<LinePointMain> findAllBySerialId(String serialId);
	@Transactional(timeout = 30)
	public List<LinePointMain> findByStatus(String status);
    
    @Transactional(timeout = 30)
    @Query(value = "select x from LinePointMain x order by x.modifyTime desc")	
	public List<LinePointMain> findAll();
    
    @Transactional(timeout = 30)
    @Query(value = "select x from LinePointMain x where x.sendType = ?1 order by x.modifyTime desc")	
	public List<LinePointMain> findBySendType(String sendType);

    // with searchText
    @Transactional(timeout = 30)
    @Query(value = "select x from LinePointMain x where "
    	+ "x.title like ('%' + ?1 + '%') or x.serialId like ('%' + ?1 + '%') order by x.modifyTime desc")	
	public List<LinePointMain> findAll(String searchText);
    
    @Transactional(timeout = 30)
    @Query(value = "select x from LinePointMain x where x.sendType = ?1 "
    	+ "and (x.title like ('%' + ?2 + '%') or x.serialId like ('%' + ?2 + '%')) order by x.modifyTime desc")	
	public List<LinePointMain> findBySendType(String sendType, String searchText);
    
    // find undone 
    @Transactional(timeout = 30)
    @Query(value = "select x from LinePointMain x where x.status <> 'COMPLETE' and x.sendType = ?1 order by x.modifyTime desc")
	public List<LinePointMain> findUndoneBySendType(String sendType);
    
    // find all linePoint with send type = Auto and not used.
    @Transactional(timeout = 30)
    @Query(value = " SELECT blpm.* FROM BCS_LINE_POINT_MAIN blpm " + 
                    " LEFT JOIN BCS_SHARE_CAMPAIGN bsc ON bsc.LINE_POINT_SERIAL_ID = blpm.SERIAL_ID " + 
                    " WHERE bsc.LINE_POINT_SERIAL_ID IS NULL ORDER BY blpm.MODIFY_TIME DESC", nativeQuery = true)    
    public List<LinePointMain> findAutoTypeNotUsed();
    
    
    
}
