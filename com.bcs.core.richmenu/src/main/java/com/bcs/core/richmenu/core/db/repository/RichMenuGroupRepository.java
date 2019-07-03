package com.bcs.core.richmenu.core.db.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.richmenu.core.db.entity.RichMenuGroup;
import com.bcs.core.db.persistence.EntityRepository;

public interface RichMenuGroupRepository extends EntityRepository<RichMenuGroup, Long>{

    // get All Active RichMenuGroup (order by modifyTime desc) 
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select x from RichMenuGroup x where x.status = 'ACTIVE' order by x.modifyTime desc")
    List<RichMenuGroup> findAll();
    
    // get All Active RichMenuGroup by GroupName
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select x from RichMenuGroup x where x.richMenuGroupName = ?1 and x.status = 'ACTIVE' order by x.modifyTime desc")
    List<RichMenuGroup> findByRichMenuGroupName(String richMenuGroupName);
    
    // get All Active RichMenuGroup like GroupName
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select x from RichMenuGroup x where x.richMenuGroupName like ('%' + ?1 + '%') and x.status = 'ACTIVE' order by x.modifyTime desc")
    List<RichMenuGroup> findLikeRichMenuGroupName(String richMenuGroupName);
}
