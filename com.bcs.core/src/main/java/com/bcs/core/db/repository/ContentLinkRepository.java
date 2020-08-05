package com.bcs.core.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.ContentLink;
import com.bcs.core.db.persistence.EntityRepository;

public interface ContentLinkRepository extends EntityRepository<ContentLink, String>{
	@Query(value = "SELECT BCS_CONTENT_LINK.LINK_URL, BCS_CONTENT_LINK.LINK_TITLE, BCS_CONTENT_LINK.LINK_ID, BCS_CONTENT_LINK.MODIFY_TIME, BCS_CONTENT_LINK.LINK_TAG, BCS_CONTENT_LINK_TRACING.TRACING_ID "
            + "FROM BCS_CONTENT_LINK INNER JOIN BCS_CONTENT_LINK_TRACING ON BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_LINK_TRACING.LINK_ID "
            + "WHERE BCS_CONTENT_LINK.LINK_URL IS NOT NULL AND BCS_CONTENT_LINK.LINK_URL != '' "
            + "ORDER BY BCS_CONTENT_LINK.MODIFY_TIME DESC ", nativeQuery = true)
    public List<Object[]> findAllWithTracingId();
    
    @Query(value = " SELECT " + 
			    		" BCS_CONTENT_LINK.LINK_URL, " + 
			    		" BCS_CONTENT_LINK.LINK_TITLE, " + 
			    		" BCS_CONTENT_LINK.LINK_ID, " + 
			    		" BCS_CONTENT_LINK.MODIFY_TIME, " + 
			    		" BCS_CONTENT_LINK.LINK_TAG, " + 
			    		" (SELECT TRACING_ID FROM BCS_CONTENT_LINK_TRACING WHERE LINK_ID = BCS_CONTENT_LINK.LINK_ID OR LINK_ID_BINDED = BCS_CONTENT_LINK.LINK_ID OR LINK_ID_UNMOBILE = BCS_CONTENT_LINK.LINK_ID) " + 
		    		" FROM " + 
			    		" BCS_CONTENT_LINK " + 
			    		" LEFT JOIN BCS_CONTENT_LINK_TRACING ON " + 
			    		" BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_LINK_TRACING.LINK_ID " + 
		    		" WHERE " + 
			    		" BCS_CONTENT_LINK.LINK_URL IS NOT NULL " + 
			    		" AND BCS_CONTENT_LINK.LINK_URL != '' " + 
		    		" ORDER BY " + 
		    			" BCS_CONTENT_LINK.MODIFY_TIME DESC ", nativeQuery = true)
    public List<Object[]> findAllTracingLink();

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME ,LINK_TAG "
			+ "FROM BCS_CONTENT_LINK "
			+ "WHERE LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrl();

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME ,LINK_TAG "
			+ "FROM BCS_CONTENT_LINK, BCS_CONTENT_FLAG "
			+ "WHERE CONTENT_TYPE = 'LINK' AND FLAG_VALUE = ?1 AND LINK_ID = REFERENCE_ID AND LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrlByFlag(String flag);
	
	@Query(value = "SELECT BCS_CONTENT_LINK.LINK_URL, BCS_CONTENT_LINK.LINK_TITLE, BCS_CONTENT_LINK.LINK_ID, BCS_CONTENT_LINK.MODIFY_TIME, BCS_CONTENT_LINK.LINK_TAG, BCS_CONTENT_LINK_TRACING.TRACING_ID "
            + "FROM BCS_CONTENT_LINK INNER JOIN BCS_CONTENT_LINK_TRACING ON BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_LINK_TRACING.LINK_ID INNER JOIN BCS_CONTENT_FLAG ON BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_FLAG.REFERENCE_ID "
            + "WHERE BCS_CONTENT_FLAG.CONTENT_TYPE = 'LINK' AND BCS_CONTENT_FLAG.FLAG_VALUE = ?1 AND BCS_CONTENT_LINK.LINK_URL IS NOT NULL AND BCS_CONTENT_LINK.LINK_URL != '' "
            + "ORDER BY BCS_CONTENT_LINK.MODIFY_TIME DESC ", nativeQuery = true)
    public List<Object[]> findAllLinkUrlWithTracingIdByFlag(String flag);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME ,LINK_TAG "
			+ "FROM BCS_CONTENT_LINK "
			+ "WHERE LINK_TAG LIKE ?1  AND LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrlByLikeFlag(String flag);
	
	@Query(value = "SELECT BCS_CONTENT_LINK.LINK_URL, BCS_CONTENT_LINK.LINK_TITLE, BCS_CONTENT_LINK.LINK_ID, BCS_CONTENT_LINK.MODIFY_TIME, BCS_CONTENT_LINK.LINK_TAG, BCS_CONTENT_LINK_TRACING.TRACING_ID "
            + "FROM BCS_CONTENT_LINK INNER JOIN BCS_CONTENT_LINK_TRACING ON BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_LINK_TRACING.LINK_ID INNER JOIN BCS_CONTENT_FLAG ON BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_FLAG.REFERENCE_ID "
            + "WHERE BCS_CONTENT_FLAG.CONTENT_TYPE = 'LINK' AND BCS_CONTENT_LINK.LINK_TAG LIKE ?1 AND BCS_CONTENT_LINK.LINK_URL IS NOT NULL AND BCS_CONTENT_LINK.LINK_URL != '' "
            + "ORDER BY BCS_CONTENT_LINK.MODIFY_TIME DESC ", nativeQuery = true)
     public List<Object[]> findAllLinkUrlWithTracingIdByLikeTag(String flag);
     
 	@Query(value = " SELECT " + 
			 			" BCS_CONTENT_LINK.LINK_URL, " + 
			 			" BCS_CONTENT_LINK.LINK_TITLE, " + 
			 			" BCS_CONTENT_LINK.LINK_ID, " + 
			 			" BCS_CONTENT_LINK.MODIFY_TIME, " + 
			 			" BCS_CONTENT_LINK.LINK_TAG, " + 
			 			" (SELECT TRACING_ID FROM BCS_CONTENT_LINK_TRACING WHERE LINK_ID = BCS_CONTENT_LINK.LINK_ID OR LINK_ID_BINDED = BCS_CONTENT_LINK.LINK_ID OR LINK_ID_UNMOBILE = BCS_CONTENT_LINK.LINK_ID) " + 
		 			" FROM " + 
			 			" BCS_CONTENT_LINK  " + 
			 			" LEFT JOIN BCS_CONTENT_LINK_TRACING ON BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_LINK_TRACING.LINK_ID  " + 
			 			" LEFT JOIN BCS_CONTENT_FLAG ON BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_FLAG.REFERENCE_ID " + 
		 			" WHERE " + 
			 			" BCS_CONTENT_FLAG.CONTENT_TYPE = 'LINK' " + 
			 			" AND BCS_CONTENT_LINK.LINK_TAG LIKE %:linkTag% " + 
			 			" AND BCS_CONTENT_LINK.LINK_URL IS NOT NULL " + 
			 			" AND BCS_CONTENT_LINK.LINK_URL != '' " + 
		 			" ORDER BY " + 
		 				" BCS_CONTENT_LINK.MODIFY_TIME DESC ", nativeQuery = true)
      public List<Object[]> findAllTracingLinkByLikeTag(@Param("linkTag") String linkTag);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME, LINK_TAG "
			+ "FROM BCS_CONTENT_LINK "
			+ "WHERE LINK_TITLE LIKE ?1 AND LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrlByLikeTitle(String title);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT LINK_URL, LINK_TITLE, LINK_ID, MODIFY_TIME, LINK_TAG "
			+ "FROM BCS_CONTENT_LINK "
			+ "WHERE MODIFY_TIME BETWEEN ?1 AND  ?2 AND LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrlByLikeTime(String startTime , String endTime);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT BCS_CONTENT_LINK.LINK_URL, BCS_CONTENT_LINK.LINK_TITLE, BCS_CONTENT_LINK.LINK_ID, BCS_CONTENT_LINK.MODIFY_TIME, BCS_CONTENT_LINK.LINK_TAG, BCS_CONTENT_LINK_TRACING.TRACING_ID "
			+ "FROM BCS_CONTENT_LINK INNER JOIN BCS_CONTENT_LINK_TRACING ON BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_LINK_TRACING.LINK_ID "
			+ "WHERE BCS_CONTENT_LINK.MODIFY_TIME BETWEEN ?1 AND ?2 AND BCS_CONTENT_LINK.LINK_URL IS NOT NULL AND LINK_URL != '' "
			+ "ORDER BY MODIFY_TIME DESC ", nativeQuery = true)
	public List<Object[]> findAllLinkUrlWithTracingIdByLikeTime(String startTime , String endTime);
	
    @Query(value = " SELECT " + 
			    		" BCS_CONTENT_LINK.LINK_URL, " + 
			    		" BCS_CONTENT_LINK.LINK_TITLE, " + 
			    		" BCS_CONTENT_LINK.LINK_ID, " + 
			    		" BCS_CONTENT_LINK.MODIFY_TIME, " + 
			    		" BCS_CONTENT_LINK.LINK_TAG, " + 
			    		" (SELECT TRACING_ID FROM BCS_CONTENT_LINK_TRACING WHERE LINK_ID = BCS_CONTENT_LINK.LINK_ID OR LINK_ID_BINDED = BCS_CONTENT_LINK.LINK_ID OR LINK_ID_UNMOBILE = BCS_CONTENT_LINK.LINK_ID) " + 
		    		" FROM " + 
			    		" BCS_CONTENT_LINK " + 
			    		" LEFT JOIN BCS_CONTENT_LINK_TRACING ON " + 
			    		" BCS_CONTENT_LINK.LINK_ID = BCS_CONTENT_LINK_TRACING.LINK_ID " + 
		    		" WHERE " + 
		    			" BCS_CONTENT_LINK.MODIFY_TIME BETWEEN ?1 AND ?2 " + 
			    		" AND BCS_CONTENT_LINK.LINK_URL IS NOT NULL " + 
			    		" AND BCS_CONTENT_LINK.LINK_URL != '' " + 
		    		" ORDER BY " + 
		    			" BCS_CONTENT_LINK.MODIFY_TIME DESC ", nativeQuery = true)
    public List<Object[]> findAllTracingLinkByDateTime(String startTime , String endTime);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT BCS_USER_TRACE_LOG.MODIFY_USER ,BCS_CONTENT_LINK.MODIFY_TIME " 
				 + "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
				 + "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink'  and LINK_URL = ?1 "
				 + "ORDER BY MODIFY_TIME DESC, LINK_URL ", nativeQuery = true)
	public List<Object[]> findAllLinkUrlForallUID(String linkUrl);
	
	@Transactional(readOnly = true, timeout = 30)
	public List<ContentLink> findByLinkUrl(String linkUrl);
	
	@Transactional(readOnly = true, timeout = 30)
	public List<ContentLink> findByLinkId(String linkId);

	@Transactional(readOnly = true, timeout = 30)
	@Query("select x from ContentLink x where x.linkId in (?1) ")
//	@Query("select x from ContentLink x where x.linkId in ?1 ") // MYSQL Difference
	public List<ContentLink> findByLinkIdIn(List<String> linkIds);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "           COUNT('x') AS allCount, "
			+ "           COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY < ?3  ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrlAndTime(String linkUrl, String start, String end);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "           COUNT('x') AS allCount, "
			+ "           COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY < ?3  AND LINK_ID = ?4", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrlAndTime(String linkUrl, String start, String end , String LinkId);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "           COUNT('x') AS allCount, "
			+ "           COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 "
			+ "AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY < ?3  AND LINK_ID = ?4", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrlAndLinkIdAndTime(String linkUrl, String start, String end, String LinkId);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "      MODIFY_DAY AS Day, "
			+ "      COUNT('x') AS allCount, "
			+ "      COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 "
			+ "group by MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrl(String linkUrl);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "      MODIFY_DAY AS Day, "
			+ "      COUNT('x') AS allCount, "
			+ "      COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1  AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 "
			+ "group by MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrl(String linkUrl, String start);
	
	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "      MODIFY_DAY AS Day, "
			+ "      COUNT('x') AS allCount, "
			+ "      COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 AND LINK_ID = ?2 "
			+ "AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?3 "
			+ "group by MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkUrlAndLinkId(String linkUrl, String linkId, String start);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "          COUNT('x') AS allCount, "
			+ "          COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_ID = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY < ?3  ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkIdAndTime(String linkId, String start, String end);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "      MODIFY_DAY AS Day, "
			+ "      COUNT('x') AS allCount, "
			+ "      COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_ID = ?1 "
			+ "group by MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkId(String LinkId);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT "
			+ "      MODIFY_DAY AS Day, "
			+ "      COUNT('x') AS allCount, "
			+ "      COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_ID = ?1  AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 "
			+ "group by MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkId(String LinkId, String start);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT distinct BCS_USER_TRACE_LOG.MODIFY_USER "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_URL = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY < ?3  ", nativeQuery = true)
	public List<String> findClickMidByLinkUrlAndTime(String linkUrl, String start, String end);

	@Transactional(readOnly = true, timeout = 30)
	@Query(value = "SELECT " + 
						" distinct BCS_USER_TRACE_LOG.MODIFY_USER " + 
					" FROM " + 
						" BCS_CONTENT_LINK, " + 
						" BCS_USER_TRACE_LOG " + 
					" WHERE " + 
						" LINK_ID = REFERENCE_ID  " + 
						" AND ACTION = 'ClickLink' " + 
						" AND LINK_URL = ?1 " + 
						" AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 " + 
						" AND BCS_USER_TRACE_LOG.MODIFY_DAY < ?3 " + 
						" AND LINK_ID = ?4 ", nativeQuery = true)
	public List<String> findClickMidByLinkUrlAndTimeAndLinkId(String linkUrl, String start, String end, String linkId);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
	               "SELECT" + 
			       "    bclt.TRACING_ID," + 
			       "	bcl.LINK_ID," + 
			       "	bcl.LINK_TITLE," + 
			       "	bcl.LINK_URL," + 
			       "	bcl.MODIFY_TIME," +
			       "	bcl.LINK_TAG," + 
			       "	(SELECT COUNT(MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS CLICK_COUNT, " + 
			       "	(SELECT COUNT(DISTINCT MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS USER_COUNT, " +
			       "    ROW_NUMBER() OVER (ORDER BY TRACING_ID DESC, LINK_TITLE) AS ROW_ID " +
			       "FROM BCS_CONTENT_LINK bcl " +
			       "LEFT JOIN BCS_CONTENT_LINK_TRACING bclt ON (bcl.LINK_ID = bclt.LINK_ID_BINDED OR bcl.LINK_ID = bclt.LINK_ID OR bcl.LINK_ID = bclt.LINK_ID_UNMOBILE) " +
			       "WHERE bcl.MODIFY_TIME >= ?1 AND bcl.MODIFY_TIME <= ?2 " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?5 AND ROW_ID < (?5 + ?6) " +
			       "ORDER BY TRACING_ID DESC, LINK_TITLE", nativeQuery = true)
	public List<Object[]> findListByModifyDateOrderByTracingID(String startDate, String endDate, String dataStartDate, String dataEndDate, int offset, int recordNum);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
	               "SELECT" + 
			       "    bclt.TRACING_ID," + 
			       "	bcl.LINK_ID," + 
			       "	bcl.LINK_TITLE," + 
			       "	bcl.LINK_URL," + 
			       "	bcl.MODIFY_TIME," + 
			       "	bcl.LINK_TAG," + 
			       "	(SELECT COUNT(MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS CLICK_COUNT, " + 
			       "	(SELECT COUNT(DISTINCT MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS USER_COUNT, " +
			       "    ROW_NUMBER() OVER (ORDER BY TRACING_ID DESC, LINK_TITLE) AS ROW_ID " +
			       "FROM BCS_CONTENT_LINK bcl " +
			       "LEFT JOIN BCS_CONTENT_LINK_TRACING bclt ON (bcl.LINK_ID = bclt.LINK_ID_BINDED OR bcl.LINK_ID = bclt.LINK_ID OR bcl.LINK_ID = bclt.LINK_ID_UNMOBILE) " +
			       "WHERE bcl.MODIFY_TIME >= ?1 AND bcl.MODIFY_TIME <= ?2 " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?5 " +
			       "ORDER BY TRACING_ID DESC, LINK_TITLE", nativeQuery = true)
	public List<Object[]> findListByModifyDateOrderByTracingID(String startDate, String endDate, String dataStartDate, String dataEndDate, int offset);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
	               "SELECT" + 
			       "    bclt.TRACING_ID," + 
			       "	bcl.LINK_ID," + 
			       "	bcl.LINK_TITLE," + 
			       "	bcl.LINK_URL," + 
			       "	bcl.MODIFY_TIME," + 
			       "	bcl.LINK_TAG," + 
			       "	(SELECT COUNT(MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS CLICK_COUNT, " + 
			       "	(SELECT COUNT(DISTINCT MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS USER_COUNT, " +
			       "    ROW_NUMBER() OVER (ORDER BY LINK_URL, LINK_TITLE) AS ROW_ID " +
			       "FROM BCS_CONTENT_LINK bcl " +
			       "LEFT JOIN BCS_CONTENT_LINK_TRACING bclt ON (bcl.LINK_ID = bclt.LINK_ID_BINDED OR bcl.LINK_ID = bclt.LINK_ID OR bcl.LINK_ID = bclt.LINK_ID_UNMOBILE) " +
			       "WHERE bcl.MODIFY_TIME >= ?1 AND bcl.MODIFY_TIME <= ?2 " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?5 AND ROW_ID < (?5 + ?6) " +
			       "ORDER BY LINK_URL, LINK_TITLE", nativeQuery = true)
	public List<Object[]> findListByModifyDateOrderByLinkUrl(String startDate, String endDate, String dataStartDate, String dataEndDate, int offset, int recordNum);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
	               "SELECT" + 
			       "    bclt.TRACING_ID," + 
			       "	bcl.LINK_ID," + 
			       "	bcl.LINK_TITLE," + 
			       "	bcl.LINK_URL," + 
			       "	bcl.MODIFY_TIME," + 
			       "	bcl.LINK_TAG," + 
			       "	(SELECT COUNT(MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS CLICK_COUNT, " + 
			       "	(SELECT COUNT(DISTINCT MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS USER_COUNT, " +
			       "    ROW_NUMBER() OVER (ORDER BY LINK_URL, LINK_TITLE) AS ROW_ID " +
			       "FROM BCS_CONTENT_LINK bcl " +
			       "LEFT JOIN BCS_CONTENT_LINK_TRACING bclt ON (bcl.LINK_ID = bclt.LINK_ID_BINDED OR bcl.LINK_ID = bclt.LINK_ID OR bcl.LINK_ID = bclt.LINK_ID_UNMOBILE) " +
			       "WHERE bcl.MODIFY_TIME >= ?1 AND bcl.MODIFY_TIME <= ?2 " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?5 " +
			       "ORDER BY LINK_URL, LINK_TITLE", nativeQuery = true)
	public List<Object[]> findListByModifyDateOrderByLinkUrl(String startDate, String endDate, String dataStartDate, String dataEndDate, int offset);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
	               "SELECT" + 
			       "    bclt.TRACING_ID," + 
			       "	bcl.LINK_ID," + 
			       "	bcl.LINK_TITLE," + 
			       "	bcl.LINK_URL," + 
			       "	bcl.MODIFY_TIME," + 
			       "	bcl.LINK_TAG," + 
			       "	(SELECT COUNT(MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS CLICK_COUNT, " + 
			       "	(SELECT COUNT(DISTINCT MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS USER_COUNT, " +
			       "    ROW_NUMBER() OVER (ORDER BY TRACING_ID DESC, LINK_TITLE) AS ROW_ID " +
			       "FROM BCS_CONTENT_LINK bcl " +
			       "LEFT JOIN BCS_CONTENT_LINK_TRACING bclt ON (bcl.LINK_ID = bclt.LINK_ID_BINDED OR bcl.LINK_ID = bclt.LINK_ID OR bcl.LINK_ID = bclt.LINK_ID_UNMOBILE) " +
			       "WHERE bcl.MODIFY_TIME >= ?1 AND bcl.MODIFY_TIME <= ?2 " + 
			       "AND (bcl.LINK_ID IN (SELECT REFERENCE_ID FROM BCS_CONTENT_FLAG bcf WHERE CONTENT_TYPE='LINK' AND FLAG_VALUE LIKE ?5) " +
			       "OR bcl.LINK_TITLE LIKE ?5 OR bcl.LINK_TAG LIKE ?5) " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?6 AND ROW_ID < (?6 + ?7) " +
	               "ORDER BY TRACING_ID DESC, LINK_TITLE", nativeQuery = true)
	public List<Object[]> findListByModifyDateAndFlagOrderByTracingID(String startDate, String endDate, String dataStartDate, String dataEndDate, String flag, int offset, int recordNum);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
	               "SELECT" + 
			       "    bclt.TRACING_ID," + 
			       "	bcl.LINK_ID," + 
			       "	bcl.LINK_TITLE," + 
			       "	bcl.LINK_URL," + 
			       "	bcl.MODIFY_TIME," + 
			       "	bcl.LINK_TAG," + 
			       "	(SELECT COUNT(MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS CLICK_COUNT, " + 
			       "	(SELECT COUNT(DISTINCT MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS USER_COUNT, " +
			       "    ROW_NUMBER() OVER (ORDER BY TRACING_ID DESC, LINK_TITLE) AS ROW_ID " +
			       "FROM BCS_CONTENT_LINK bcl " +
			       "LEFT JOIN BCS_CONTENT_LINK_TRACING bclt ON (bcl.LINK_ID = bclt.LINK_ID_BINDED OR bcl.LINK_ID = bclt.LINK_ID OR bcl.LINK_ID = bclt.LINK_ID_UNMOBILE) " +
			       "WHERE bcl.MODIFY_TIME >= ?1 AND bcl.MODIFY_TIME <= ?2 " + 
			       "AND (bcl.LINK_ID IN (SELECT REFERENCE_ID FROM BCS_CONTENT_FLAG bcf WHERE CONTENT_TYPE='LINK' AND FLAG_VALUE LIKE ?5) " +
			       "OR bcl.LINK_TITLE LIKE ?5 OR bcl.LINK_TAG LIKE ?5) " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?6 " +
	               "ORDER BY TRACING_ID DESC, LINK_TITLE", nativeQuery = true)
	public List<Object[]> findListByModifyDateAndFlagOrderByTracingID(String startDate, String endDate, String dataStartDate, String dataEndDate, String flag, int offset);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
	               "SELECT" + 
			       "    bclt.TRACING_ID," + 
			       "	bcl.LINK_ID," + 
			       "	bcl.LINK_TITLE," + 
			       "	bcl.LINK_URL," + 
			       "	bcl.MODIFY_TIME," + 
			       "	bcl.LINK_TAG," + 
			       "	(SELECT COUNT(MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS CLICK_COUNT, " + 
			       "	(SELECT COUNT(DISTINCT MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS USER_COUNT, " +
			       "    ROW_NUMBER() OVER (ORDER BY LINK_URL, LINK_TITLE) AS ROW_ID " +
			       "FROM BCS_CONTENT_LINK bcl " +
			       "LEFT JOIN BCS_CONTENT_LINK_TRACING bclt ON (bcl.LINK_ID = bclt.LINK_ID_BINDED OR bcl.LINK_ID = bclt.LINK_ID OR bcl.LINK_ID = bclt.LINK_ID_UNMOBILE) " +
			       "WHERE bcl.MODIFY_TIME >= ?1 AND bcl.MODIFY_TIME <= ?2 " + 
			       "AND (bcl.LINK_ID IN (SELECT REFERENCE_ID FROM BCS_CONTENT_FLAG bcf WHERE CONTENT_TYPE='LINK' AND FLAG_VALUE LIKE ?5) " +
			       "OR bcl.LINK_TITLE LIKE ?5 OR bcl.LINK_TAG LIKE ?5) " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?6 AND ROW_ID < (?6 + ?7) " +
	               "ORDER BY LINK_URL, LINK_TITLE", nativeQuery = true)
	public List<Object[]> findListByModifyDateAndFlagOrderByLinkUrl(String startDate, String endDate, String dataStartDate, String dataEndDate, String flag, int offset, int recordNum);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
	               "SELECT" + 
			       "    bclt.TRACING_ID," + 
			       "	bcl.LINK_ID," + 
			       "	bcl.LINK_TITLE," + 
			       "	bcl.LINK_URL," + 
			       "	bcl.MODIFY_TIME," + 
			       "	bcl.LINK_TAG," + 
			       "	(SELECT COUNT(MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS CLICK_COUNT, " + 
			       "	(SELECT COUNT(DISTINCT MODIFY_USER) FROM BCS_USER_TRACE_LOG WHERE ACTION = 'ClickLink' AND REFERENCE_ID = bcl.LINK_ID AND MODIFY_DAY >= ?3 AND MODIFY_DAY <= ?4) AS USER_COUNT, " +
			       "    ROW_NUMBER() OVER (ORDER BY LINK_URL, LINK_TITLE) AS ROW_ID " +
			       "FROM BCS_CONTENT_LINK bcl " +
			       "LEFT JOIN BCS_CONTENT_LINK_TRACING bclt ON (bcl.LINK_ID = bclt.LINK_ID_BINDED OR bcl.LINK_ID = bclt.LINK_ID OR bcl.LINK_ID = bclt.LINK_ID_UNMOBILE) " +
			       "WHERE bcl.MODIFY_TIME >= ?1 AND bcl.MODIFY_TIME <= ?2 " + 
			       "AND (bcl.LINK_ID IN (SELECT REFERENCE_ID FROM BCS_CONTENT_FLAG bcf WHERE CONTENT_TYPE='LINK' AND FLAG_VALUE LIKE ?5) " +
			       "OR bcl.LINK_TITLE LIKE ?5 OR bcl.LINK_TAG LIKE ?5) " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?6 " +
	               "ORDER BY LINK_URL, LINK_TITLE", nativeQuery = true)
	public List<Object[]> findListByModifyDateAndFlagOrderByLinkUrl(String startDate, String endDate, String dataStartDate, String dataEndDate, String flag, int offset);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT DISTINCT BCS_USER_TRACE_LOG.MODIFY_USER "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_ID = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY <= ?3  ", nativeQuery = true)
	public List<String> findClickMidByLinkIdAndTime(String linkId, String start, String end);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT "
			+ "     BCS_USER_TRACE_LOG.MODIFY_DAY AS Day, "
			+ "     COUNT('x') AS allCount, "
			+ "     COUNT(distinct BCS_USER_TRACE_LOG.MODIFY_USER) AS allDistinctCount "
			+ "FROM BCS_CONTENT_LINK, BCS_USER_TRACE_LOG "
			+ "WHERE LINK_ID = REFERENCE_ID AND ACTION = 'ClickLink' AND LINK_ID = ?1 AND BCS_USER_TRACE_LOG.MODIFY_DAY >= ?2 AND BCS_USER_TRACE_LOG.MODIFY_DAY <= ?3 GROUP BY MODIFY_DAY ", nativeQuery = true)
	public List<Object[]> countClickCountByLinkIdAndTimeNew(String linkId, String start, String end);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
	               "SELECT" + 
	               "	bcl.LINK_URL," +
			       "	COUNT(butl.MODIFY_USER) AS CLICK_COUNT, " + 
			       "	COUNT(DISTINCT butl.MODIFY_USER) AS USERT_COUNT, " +
			       "    ROW_NUMBER() OVER (ORDER BY bcl.LINK_URL) AS ROW_ID " +
			       "FROM BCS_CONTENT_LINK bcl " +
			       "LEFT JOIN BCS_USER_TRACE_LOG butl ON bcl.LINK_ID = butl.REFERENCE_ID " +
			       "WHERE bcl.LINK_URL IS NOT NULL AND butl.ACTION = 'ClickLink' AND butl.MODIFY_DAY >= ?1 AND butl.MODIFY_DAY <= ?2 " +
			       "GROUP BY LINK_URL" +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?3 AND ROW_ID < (?3 + ?4) " +
			       "ORDER BY LINK_URL", nativeQuery = true)
	public List<Object[]> findListByModifyDateGroupByUrl(String dataStartDate, String dataEndDate, int offset, int recordNum);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
			       "SELECT" + 
                   "	bcl.LINK_URL," +
        	       "	COUNT(butl.MODIFY_USER) AS CLICK_COUNT, " + 
		           "	COUNT(DISTINCT butl.MODIFY_USER) AS USERT_COUNT, " +
		           "    ROW_NUMBER() OVER (ORDER BY bcl.LINK_URL) AS ROW_ID " +
		           "FROM BCS_CONTENT_LINK bcl " +
		           "LEFT JOIN BCS_USER_TRACE_LOG butl ON bcl.LINK_ID = butl.REFERENCE_ID " +
		           "WHERE bcl.LINK_URL IS NOT NULL AND butl.ACTION = 'ClickLink' AND butl.MODIFY_DAY >= ?1 AND butl.MODIFY_DAY <= ?2 " +
		           "GROUP BY LINK_URL" +
		           ") AS NewTable " +
			       "WHERE ROW_ID >= ?3 " +
			       "ORDER BY LINK_URL", nativeQuery = true)
	public List<Object[]> findListByModifyDateGroupByUrl(String dataStartDate, String dataEndDate, int offset);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
			       "SELECT" + 
                   "	bcl.LINK_URL," +
		           "	COUNT(butl.MODIFY_USER) AS CLICK_COUNT, " + 
		           "	COUNT(DISTINCT butl.MODIFY_USER) AS USERT_COUNT, " +
		           "    ROW_NUMBER() OVER (ORDER BY bcl.LINK_URL) AS ROW_ID " +
		           "FROM BCS_CONTENT_LINK bcl " +
		           "LEFT JOIN BCS_USER_TRACE_LOG butl ON bcl.LINK_ID = butl.REFERENCE_ID " +
		           "WHERE bcl.LINK_URL IS NOT NULL AND butl.ACTION = 'ClickLink' AND butl.MODIFY_DAY >= ?1 AND butl.MODIFY_DAY <= ?2 " +
			       "AND (bcl.LINK_ID IN (SELECT REFERENCE_ID FROM BCS_CONTENT_FLAG bcf WHERE CONTENT_TYPE='LINK' AND FLAG_VALUE LIKE ?3) " +
			       "OR bcl.LINK_TITLE LIKE ?3) " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?4 AND ROW_ID < (?4 + ?5) " +
			       "ORDER BY LINK_URL", nativeQuery = true)
	public List<Object[]> findListByModifyDateAndFlagGroupByUrl(String dataStartDate, String dataEndDate, String flag, int offset, int recordNum);
	
	@Transactional(readOnly = true, timeout = 60)
	@Query(value = "SELECT * FROM (" +
			       "SELECT" + 
                   "	bcl.LINK_URL," +
		           "	COUNT(butl.MODIFY_USER) AS CLICK_COUNT, " + 
		           "	COUNT(DISTINCT butl.MODIFY_USER) AS USERT_COUNT, " +
		           "    ROW_NUMBER() OVER (ORDER BY bcl.LINK_URL) AS ROW_ID " +
		           "FROM BCS_CONTENT_LINK bcl " +
		           "LEFT JOIN BCS_USER_TRACE_LOG butl ON bcl.LINK_ID = butl.REFERENCE_ID " +
		           "WHERE bcl.LINK_URL IS NOT NULL AND butl.ACTION = 'ClickLink' AND butl.MODIFY_DAY >= ?1 AND butl.MODIFY_DAY <= ?2 " +
			       "AND (bcl.LINK_ID IN (SELECT REFERENCE_ID FROM BCS_CONTENT_FLAG bcf WHERE CONTENT_TYPE='LINK' AND FLAG_VALUE LIKE ?3) " +
			       "OR bcl.LINK_TITLE LIKE ?3) " +
			       ") AS NewTable " +
			       "WHERE ROW_ID >= ?4 " +
			       "ORDER BY LINK_URL", nativeQuery = true)
	public List<Object[]> findListByModifyDateAndFlagGroupByUrl(String dataStartDate, String dataEndDate, String flag, int offset);
}
