package com.picsauditing.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.DBBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.NcmsCategory;

@Transactional
public class NcmsCategoryDAO {

	/**
	 * Get a list of categories and their status for a given contractor.
	 * The reason this is so weird is because of the way the table is built. 
	 * It's a denormalized table with the categories listed as columns. To 
	 * figure out the status you have to query the whole row and then iterate 
	 * over each column to get the "list" of categories.
	 * 
	 * This craziness should go away three years after the last NCMS audit,
	 * which was on 2006-05-10. So June 2009, we can stop supporting NCMS audits.
	 * 
	 * @param conID
	 * @return
	 * @throws Exception
	 */
	public List<NcmsCategory> findCategories(int conID) throws Exception {
		List<NcmsCategory> categories = new ArrayList<NcmsCategory>();
		if (conID == 0)
			return categories;

		String query = "SELECT * FROM NCMS_Desktop WHERE conID=" + conID;
		String[] dontShow = { "conID", "ContractorsName", "remove", "fedTaxID", "lastReview", "approved" };

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

			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnName(i);
				if (!Utilities.arrayContains(dontShow, columnName)) {
					NcmsCategory cat = new NcmsCategory();
					cat.setName(columnName);
					cat.setStatus(result.getString(i));
					categories.add(cat);
				}
			}
		} finally {
			try {
				result.close();
			} catch (Exception e) {}
			try {
				statement.close();
			} catch (Exception e) {}
			try {
				conn.close();
			} catch (Exception e) {}
		}
		return categories;
	}
}
