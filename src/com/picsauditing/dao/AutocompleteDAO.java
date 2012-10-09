package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class AutocompleteDAO {
	
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	
	private static final int LIMIT_AMOUNT = 10;
	
	private static final Database database = new Database();
		
	public List<BasicDynaBean> findPeopleToShareWith(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		List<BasicDynaBean> users = findUsersToShareWith(reportId, searchTerm, permissions);
		List<BasicDynaBean> accounts = findAccountsToShareWith(reportId, searchTerm, permissions);
		return mergeResults(users, accounts);
	}
	
	private List<BasicDynaBean> findUsersToShareWith(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		StringBuilder query = new StringBuilder();
		query.append("SELECT u.name AS 'result_name', ");
		query.append("IF(u.isGroup = 'Yes', 'Group', 'User') AS 'result_type', ");
		query.append("u.id AS 'result_id', ");
		query.append("a.name AS 'result_at', ");
		query.append("'user' AS 'search_type' ");
		query.append("FROM users u ");
		query.append("JOIN accounts a ON a.id = u.accountID ");
		query.append("WHERE u.name LIKE '%").append(searchTerm).append("%' ");
		
		List<OperatorAccount> childOperators = findAllChildOperators(permissions);
		if (CollectionUtils.isNotEmpty(childOperators)) {
			query.append("AND a.id IN (").append(Strings.implodeIDs(childOperators)).append(") ");
		}
		
		query.append("LIMIT ").append(LIMIT_AMOUNT);
		
		return database.select(query.toString(), false);
	}
	
	private List<BasicDynaBean> findAccountsToShareWith(int reportId, String searchTerm, Permissions permissions) throws SQLException {
		StringBuilder query = new StringBuilder();
		query.append("SELECT a.id AS 'result_id', ");
		query.append("a.name AS 'result_name', ");
		query.append("a.type AS 'result_type', ");
		query.append("'account' AS 'search_type', ");
		query.append("CONCAT(a.city, ', ', a.countrySubdivision) AS 'result_at' ");
		query.append("FROM accounts a ");
		query.append("WHERE a.name LIKE '%").append(searchTerm).append("%' ");
		
		List<OperatorAccount> childOperators = findAllChildOperators(permissions);
		if (CollectionUtils.isNotEmpty(childOperators)) {
			query.append("AND a.id IN (").append(Strings.implodeIDs(childOperators)).append(") ");
		}
		
		query.append("LIMIT ").append(LIMIT_AMOUNT);
		
		return database.select(query.toString(), false);
	}
	
	private List<OperatorAccount> findAllChildOperators(Permissions permissions) {
		try {
			return operatorAccountDAO.findWhere(true, null, permissions, LIMIT_AMOUNT);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	private List<BasicDynaBean> mergeResults(List<BasicDynaBean> users, List<BasicDynaBean> accounts) {
		if (CollectionUtils.isEmpty(users) && CollectionUtils.isEmpty(accounts)) {
			return Collections.emptyList();
		}
		
		List<BasicDynaBean> mergedList = new ArrayList<BasicDynaBean>();
		
		Iterator<BasicDynaBean> userIterator = users.iterator();
		Iterator<BasicDynaBean> accountIterator = accounts.iterator();
		while (continueMerging(mergedList, userIterator, accountIterator)) {
			BasicDynaBean bean = null;
			
			if (userIterator.hasNext() && ((bean = userIterator.next()) != null)) {
				mergedList.add(bean);
			}
			
			if (accountIterator.hasNext() && ((bean = accountIterator.next()) != null)) {
				mergedList.add(bean);
			}
		}
		
		return mergedList;
	}

	private boolean continueMerging(List<BasicDynaBean> mergedList, Iterator<BasicDynaBean> userIterator,
			Iterator<BasicDynaBean> accountIterator) {
		return ((userIterator != null && userIterator.hasNext()) 
				|| (accountIterator != null && accountIterator.hasNext())) 
				&& mergedList.size() < LIMIT_AMOUNT;
	}

}
