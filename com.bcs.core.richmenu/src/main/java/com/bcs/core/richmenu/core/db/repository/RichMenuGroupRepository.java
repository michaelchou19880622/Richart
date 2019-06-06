package com.bcs.core.richmenu.core.db.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.richmenu.core.db.entity.RichMenuGroup;
import com.bcs.core.db.persistence.EntityRepository;

public interface RichMenuGroupRepository extends EntityRepository<RichMenuGroup, Long>{

    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select x from RichMenuGroup x "
            + "where x.richMenuGroupName = ?1 "
            + "and x.status = 'ACTIVE' ")
    List<RichMenuGroup> findByRichMenuGroupName(String richMenuGroupName);
    
    
    // unused
    
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select count(x) from RichMenuGroup x "
            + "where x.status = 'ACTIVE'")
    Long countTotal();
    
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select count(x) from RichMenuGroup x "
            + "where x.status = 'ACTIVE' "
            + "and x.richMenuGroupName like ?1")
    Long countTotalByLikeName(String title);

    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select x from RichMenuGroup x "
            + "where x.status = ?1 ")
    List<RichMenuGroup> findByStatus(String status);
  
    
//    @Transactional(readOnly = true, timeout = 30)
//    @Query(value = "select x from RichMenuGroup x "
//            + "where x.status = ?1 "
//            + "and x.condition = ?2 "
//            + "and x.richMenuStartUsingTime < ?3 "
//            + "and x.richMenuEndUsingTime >= ?3")
//    RichMenuGroup findByStatusAndCondition(String status, String condition, Date now);
//    
//    @Transactional(readOnly = true, timeout = 30)
//    @Query(value = "select x from RichMenuGroup x "
//            + "where x.status = ?1 "
//            + "and x.condition = ?2 "
//            + "and x.richMenuStartUsingTime < ?4 "
//            + "and x.richMenuEndUsingTime > ?3")
//    List<RichMenuGroup> findByStatusAndConditionAndUsingTime(String status, String condition, Date richMenuStartUsingTime, Date richMenuEndUsingTime);
}
