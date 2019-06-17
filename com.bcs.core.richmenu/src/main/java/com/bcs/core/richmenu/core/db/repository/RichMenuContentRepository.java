package com.bcs.core.richmenu.core.db.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.richmenu.core.db.entity.RichMenuContent;
import com.bcs.core.db.persistence.EntityRepository;

public interface RichMenuContentRepository extends EntityRepository<RichMenuContent, String>{

    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select count(x) from RichMenuContent x "
            + "where x.status = 'ACTIVE'")
    Long countTotal();
    
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select count(x) from RichMenuContent x "
            + "where x.status = 'ACTIVE' "
            + "and x.richMenuName like ?1")
    Long countTotalByLikeName(String title);
    
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select x from RichMenuContent x "
            + "where x.status = ?1 "
            + "and x.level = ?2 "
            + "and x.richMenuStartUsingTime < ?3 "
            + "and x.richMenuEndUsingTime >= ?3")
    RichMenuContent findByStatusAndCondition(String status, String condition, Date now);

    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select x from RichMenuContent x "
            + "where x.richMenuGroupId = ?1 and x.status <> 'DELETE' order by x.modifyTime desc ")
    List<RichMenuContent> findByRichMenuGroupId(String richMenuGroupId);

    List<RichMenuContent> findByRichMenuGroupIdAndLevel(String richMenuGroupId, String level);
    
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select x from RichMenuContent x "
            + "where x.status = ?1 "
            + "and x.level = ?2 "
            + "and x.richMenuStartUsingTime < ?4 "
            + "and x.richMenuEndUsingTime > ?3")
    List<RichMenuContent> findByStatusAndConditionAndUsingTime(String status, String condition, Date richMenuStartUsingTime, Date richMenuEndUsingTime);
}
