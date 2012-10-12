package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.math.NumberUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class AutocompleteDAO {
	
	private static final int LIMIT_AMOUNT = 10;
	
	private static final Database database = new Database();
	
	private static final String FIND_USERS_AND_GROUPS = "SELECT IF(u.isGroup = 'YES', 'group', 'user') type, u.id, u.name, a.name AS 'location', " +
			"IF(u.isGroup = 'YES', 'Group', 'User') AS 'resultCategory', u.isGroup, u.accountID " + 
			"FROM users u " +
			"JOIN accounts a ON u.accountID = a.id AND a.type != 'Contractor' " + 
			"LEFT JOIN report_permission_user rpu ON rpu.userID = u.id AND rpu.reportID = :reportId " + 
			"WHERE u.isActive = 'Yes' AND (u.name LIKE '%:searchTerm%' :userId )" +
			"AND rpu.id IS NULL ";
	
	private static final String FIND_ACCOUNTS = "SELECT 'account' type, a.id, a.name, " +
			"CONCAT(a.city, ', ', a.countrySubdivision) as 'location', a.type 'resultCategory', NULL isGroup, a.id accountID " + 
			"FROM accounts a " + 
			"LEFT JOIN report_permission_account rpa ON rpa.accountId = a.id AND rpa.reportID = :reportId " + 
			"WHERE a.status IN ('Active','Demo') AND a.type != 'Contractor' AND (a.name LIKE '%:searchTerm%' :accountId ) " + 
			"AND rpa.id IS NULL ";
	
	private static final String AUTO_COMPLETE_SEARCH = "SELECT * FROM ( %s UNION %s ) t ";
		
	public List<BasicDynaBean> findPeopleToShareWith(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		String query = buildQuery(reportId, searchTerm, permissions);
		return database.select(query, false);
	}
	
	private String buildQuery(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		String queryUsersGroups = setUserOrGroupQueryParameters(reportId, searchTerm);
		String queryAccounts = setAccountQueryParameters(reportId, searchTerm);
		StringBuilder sql = new StringBuilder(String.format(AUTO_COMPLETE_SEARCH, queryUsersGroups, queryAccounts));
		
		if (permissions.isOperatorCorporate()) {
			sql.append(" WHERE accountID IN (");
			sql.append(permissions.getAccountId()).append(",").append(Strings.implode(permissions.getOperatorChildren()));
			sql.append(") ");
		}
		
		sql.append("ORDER BY type, isGroup, name ");
		sql.append("LIMIT ").append(LIMIT_AMOUNT);
		
		return sql.toString();
	}
	
	private String setUserOrGroupQueryParameters(int reportId, String searchTerm) {
		String queryUsersGroups = setQueryParameters(FIND_USERS_AND_GROUPS, reportId, searchTerm);
		return queryUsersGroups.replaceAll(":userId",  
				NumberUtils.isNumber(searchTerm) ? " OR u.id = " + searchTerm : Strings.EMPTY_STRING); 
	}
	
	private String setAccountQueryParameters(int reportId, String searchTerm) {
		String queryAccount = setQueryParameters(FIND_ACCOUNTS, reportId, searchTerm);
		return queryAccount.replaceAll(":accountId",  
				NumberUtils.isNumber(searchTerm) ? " OR a.id = " + searchTerm : Strings.EMPTY_STRING);
	}
	
	private String setQueryParameters(String query, int reportId, String searchTerm) {
		query = query.replaceAll(":reportId", Integer.toString(reportId));
		return query.replaceAll(":searchTerm", searchTerm);
	}

}
