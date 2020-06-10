package com.bcs.core.db.connection;

import net.sourceforge.jtds.jdbcx.JtdsDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BcsDataSource extends JtdsDataSource {

	/** Logger */
	private static Logger logger = LogManager.getLogger(BcsDataSource.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BcsDataSource() {
		super();
		logger.info("BcsDataSource public");
	}
}
