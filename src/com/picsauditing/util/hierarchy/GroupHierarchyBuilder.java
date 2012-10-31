package com.picsauditing.util.hierarchy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.util.CollectionUtils;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.util.Strings;

public class GroupHierarchyBuilder extends AbstractBreadthFirstSearchBuilder<User> {

	private static final Database DATABASE = new Database();
	
	private static final String QUERY_GROUP_IDS_FOR_USER = "SELECT ug.groupID FROM usergroup ug WHERE ug.userID IN ( %s ) ";
	
	@Override
	protected List<Integer> findAllParentEntityIds(int id) {
		return queryResults(Integer.toString(id));
	}

	@Override
	protected List<Integer> getIdsForAllParentEntities(List<Integer> entities) {
		return queryResults(Strings.implodeForDB(entities, ","));
	}
	
	private List<Integer> queryResults(String queryParameter) {
		try {
			String query = String.format(QUERY_GROUP_IDS_FOR_USER, queryParameter);
			List<BasicDynaBean> results = DATABASE.select(query, false);
			return mapResults(results);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	private List<Integer> mapResults(List<BasicDynaBean> results) {
		if (CollectionUtils.isEmpty(results)) {
			return Collections.emptyList();
		}
		
		List<Integer> ids = new ArrayList<Integer>();
		for (BasicDynaBean bean : results) {
			ids.add(Database.toInt(bean, "groupID"));
		}
		
		return ids;
	}

}
