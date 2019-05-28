package com.bcs.core.richmenu.core.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.richmenu.core.db.entity.RichMenuContentDetail;
import com.bcs.core.db.persistence.EntityRepository;

public interface RichMenuContentDetailRepository extends EntityRepository<RichMenuContentDetail, String>{
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT * FROM BCS_RICH_MENU_CONTENT_DETAIL "
			+ "WHERE RICH_ID = ?1 AND (BCS_RICH_MENU_CONTENT_DETAIL.STATUS <> 'DELETE' OR BCS_RICH_MENU_CONTENT_DETAIL.STATUS IS NULL)  "
			+ "ORDER BY START_POINT_X", nativeQuery = true)
	public List<RichMenuContentDetail> findByRichId(String richId);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT RICH_ID FROM BCS_RICH_MENU_CONTENT_DETAIL WHERE LINK_ID = ?1 AND (BCS_RICH_MENU_CONTENT_DETAIL.STATUS <> 'DELETE' OR BCS_RICH_MENU_CONTENT_DETAIL.STATUS IS NULL) ", nativeQuery = true)
	public List<String> getRichIdByLinkId(String linkId);
}
