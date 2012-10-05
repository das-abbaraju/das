package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.Permissions;
import com.picsauditing.search.Database;

public class AutocompleteDAO {
	
	private static final int LIMIT_AMOUNT = 10;
	
	private static final Database database = new Database();	
	
	public List<BasicDynaBean> findPeopleToShareWith(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		String query = buildQuery(reportId, searchTerm, permissions) + " LIMIT " + LIMIT_AMOUNT; 
		return database.select(query, false);
	}
	
	private String buildQuery(int reportId, String searchTerm, Permissions permissions) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT u.name AS 'result_name', ");
		query.append("IF(u.isGroup = 'Yes', 'Group', 'User') AS 'result_type', ");
		query.append("u.id AS 'result_id', ");
		query.append("a.name as 'result_at', ");
		query.append("IF(u.isGroup = 'Yes', 'user', 'user') AS 'search_type' ");
		query.append("FROM users u ");
		query.append("JOIN accounts a ON a.id = u.accountID ");
		query.append("WHERE u.name LIKE '%").append(searchTerm).append("%' ");
		
		query.append("UNION ");
		
		query.append("select a.id as 'result_id', ");
		query.append("a.name as 'result_name', ");
		query.append("a.type as 'result_type', ");
		query.append("'account' as 'search_type', ");
		query.append("CONCAT(a.city, ', ', a.countrySubdivision) AS 'result_at' ");
		query.append("FROM accounts a ");
		query.append("WHERE a.name like '%").append(searchTerm).append("%' ");
		
		return query.toString();
	}

}
