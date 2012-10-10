package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.Permissions;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class AutocompleteDAO {
	
	private static final int LIMIT_AMOUNT = 10;
	
	private static final Database database = new Database();
		
	public List<BasicDynaBean> findPeopleToShareWith(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		String query = buildQuery(reportId, searchTerm, permissions);
		return database.select(query, false);
	}
	
	private String buildQuery(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		String query = "SELECT * FROM ( " +
				"SELECT IF(u.isGroup = 'YES', 'group', 'user') type, u.id, u.name, a.name AS 'location', " +
				"IF(u.isGroup = 'YES', 'Group', 'User') AS 'resultCategory', u.isGroup, u.accountID " + 
				"FROM users u " +
				"JOIN accounts a ON u.accountID = a.id AND a.type != 'Contractor' " + 
				"LEFT JOIN report_permission_user rpu ON rpu.userID = u.id AND rpu.reportID = " + reportId + " " + 
				"WHERE u.isActive = 'Yes' AND u.name LIKE '%" + searchTerm + "%' " +
				"AND rpu.id IS NULL " +
				"UNION " + 
				"SELECT 'account' type, a.id, a.name, CONCAT(a.city, ', ', a.countrySubdivision) as 'location', a.type 'resultCategory', NULL isGroup, a.id accountID " + 
				"FROM accounts a " + 
				"LEFT JOIN report_permission_account rpa ON rpa.accountId = a.id AND rpa.reportID = " + reportId + " " + 
				"WHERE a.status IN ('Active','Demo') AND a.type != 'Contractor' AND a.name LIKE '%" + searchTerm + "%' " + 
				"AND rpa.id IS NULL " + 
				") t ";
		if (permissions.isOperatorCorporate()) {
			query += " WHERE accountID IN ("  + permissions.getAccountId() + ","
					+ Strings.implode(permissions.getOperatorChildren()) + ") ";
		}
		
		query += "ORDER BY type, isGroup, name ";
		query += "LIMIT " + LIMIT_AMOUNT;
		
		return query.toString();
	}

}
