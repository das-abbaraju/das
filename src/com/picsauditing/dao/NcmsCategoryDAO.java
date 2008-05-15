package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.NcmsCategory;

public class NcmsCategoryDAO {

	public List<NcmsCategory> findCategories(int conID) throws Exception {
		List<NcmsCategory> categories = new ArrayList<NcmsCategory>();
		if (conID == 0)
			return categories;
		
		String query = "SELECT * FROM NCMS_Desktop WHERE conID="+conID;
		String[] dontShow = {"conID","ContractorsName","remove","fedTaxID","lastReview","approved"};

		Connection conn = null;
		Statement statement = null;
		ResultSet result = null;
		
		try {
			conn = DBBean.getDBConnection();
			statement = conn.createStatement();
			result = statement.executeQuery(query);
			ResultSetMetaData metaData = result.getMetaData();
	
			if (!result.next())
				return categories;
			
			for (int i=1; i<=metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				;
				if (!Utilities.arrayContains(dontShow, columnName)) {
					NcmsCategory cat = new NcmsCategory();
					cat.setName(columnName);
					cat.setStatus(result.getString(i));
					categories.add(cat);
				}
			}
		} finally {
			try {result.close();} catch (Exception e) {}
			try {statement.close();} catch (Exception e) {}
			try {conn.close();} catch (Exception e) {}
		}
		return categories;
	}
}

