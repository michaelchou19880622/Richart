package com.bcs.core.richmenu.core.db.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.persistence.EntityRepository;
import com.bcs.core.richmenu.core.db.entity.RichMenuContentLink;

public interface RichMenuContentLinkRepository extends EntityRepository<RichMenuContentLink, String>{
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK "
			+ "WHERE LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrl();

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_RICH_MENU_CONTENT_FLAG "
			+ "WHERE CONTENT_TYPE = 'LINK' AND FLAG_VALUE = ?1 AND LINK_ID = REFERENCE_ID AND LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrlByFlag(String flag);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_RICH_MENU_CONTENT_FLAG "
			+ "WHERE CONTENT_TYPE = 'LINK' AND FLAG_VALUE LIKE ?1 AND LINK_ID = REFERENCE_ID AND LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrlByLikeFlag(String flag);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK "
			+ "WHERE LINK_TITLE LIKE ?1 AND LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrlByLikeTitle(String title);

	@Transactional(readOnly = true, timeout = 30)
	public List<RichMenuContentLink> findByLinkUrl(String linkUrl);

	@Transactional(readOnly = true, timeout = 30)
	@Query("select x from ContentLink x where x.linkId in ( ?1 )")
	public List<RichMenuContentLink> findByLinkIdIn(List<String> linkIds);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "           COUNT('x') AS allCount, "
			+ "           COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY < ?3  ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrlAndTime(String linkUrl, String start, String end);

	@Transactional(readOnly = true, timeout = 300)
	@Query(value = "SELECT "
			+ "           COUNT('x') AS allCount, "
			+ "           COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY = ?2 ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrlAndTime(String linkUrl, String day);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "      BCS_USER_TRACE_LOG.MODIFY_DAY AS Day, "
			+ "      COUNT('x') AS allCount, "
			+ "      COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 "
			+ "group by BCS_USER_TRACE_LOG.MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrl(String linkUrl);

	@Transactional(readOnly = true, timeout = 600)
	@Query(value = "SELECT "
			+ "      BCS_USER_TRACE_LOG.MODIFY_DAY AS Day, "
			+ "      COUNT('x') AS allCount, "
			+ "      COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1  AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 "
			+ "group by BCS_USER_TRACE_LOG.MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrl(String linkUrl, String start);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "          COUNT('x') AS allCount, "
			+ "          COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_ID = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY < ?3  ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkIdAndTime(String linkId, String start, String end);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "      BCS_USER_TRACE_LOG.MODIFY_DAY AS Day, "
			+ "      COUNT('x') AS allCount, "
			+ "      COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_ID = ?1 "
			+ "group by BCS_USER_TRACE_LOG.MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkId(String LinkId);

	@Transactional(readOnly = true, timeout = 300)
	@Query(value = "SELECT "
			+ "      BCS_USER_TRACE_LOG.MODIFY_DAY AS Day, "
			+ "      COUNT('x') AS allCount, "
			+ "      COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_ID = ?1  AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 "
			+ "group by BCS_USER_TRACE_LOG.MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkId(String LinkId, String start);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT distinct BCS_USER_TRACE_LOG.MODIFY_USER "
			+ "FROM BCS_RICH_MENU_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY < ?3  ", nativeQuery = true)
	public List<String> findClickMidByLinkUrlAndTime(String linkUrl, String start, String end);

	@Transactional(readOnly = true, timeout = 30)
    @Query(value = "select count(x) from ContentLink x "
            + "where x.linkUrl is not null and x.linkUrl != ''")
    Long countUrl();
	
	@Transactional(readOnly = true, timeout = 30)
    @Query(value = "select count(x) FROM ContentLink x "
            + "join RichMenuContentFlag y on x.linkId = y.referenceId and y.contentType = 'LINK' "
            + "where x.linkUrl is not null and x.linkUrl != '' and (y.flagValue like ?1 or x.linkTitle like ?1)")
    Long countUrlByLikeFlagOrTitle(String flag);
	
    @Query(value = "select x from RichMenuContentLink x "
            + "where x.linkUrl is not null and x.linkUrl != ''")
	Page<RichMenuContentLink> findAllLink(Pageable pageable);
	
    @Query(value = "select x FROM RichMenuContentLink x "
            + "left join ContentFlag y on x.linkId = y.referenceId and y.contentType = 'LINK' "
            + "where x.linkUrl is not null and x.linkUrl != '' and (y.flagValue like ?1 or x.linkTitle like ?1)")
	Page<RichMenuContentLink> findByLikeFlagOrTitle(String flag, Pageable pageable);
    
    @Query(value = "select x FROM RichMenuContentLink x "
            + "join RichMenuContentDetail y on x.linkId = y.linkId and y.actionType = 'web' and y.richId = ?1 "
            + "where x.linkUrl is not null and x.linkUrl != '' "
            + "order by y.richDetailLetter")
	List<RichMenuContentLink> findContentLinkByRichId(String richId);
}
