package com.picsauditing.gwt.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.sql.DataSource;

public class AppContext {
	
	private PicsConfig config;
	private DataSource dataSource;
	
	public AppContext() {
		String hostName;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		
		if(hostName.startsWith("dev")){
			config = new PicsConfigDev();
		}
		else{
			config = new PicsConfigStaging();
		}
	}

	public DataSource getDataSource() {
		if(dataSource == null){
//			MysqlDataSource ds = new MysqlDataSource();
//			ds.setUrl(config.getJdbcUrl());
		}
		return dataSource;
	}
	
	
	
	

}
