package com.picsauditing.securitysession.util.hierarchy;

import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GroupHierarchyBuilder extends AbstractBreadthFirstSearchBuilder {

	private static final String QUERY_GROUP_IDS_FOR_USER = "SELECT ug.groupID FROM usergroup ug WHERE ug.userID IN ( ? ) ";

	private JdbcTemplate jdbcTemplate;

	@Override
	protected List<Integer> findAllParentEntityIds(int id) {
		return queryResults(Integer.toString(id));
	}

	@Override
	protected List<Integer> getIdsForAllParentEntities(List<Integer> entities) {
		return queryResults(Strings.implodeForDB(entities));
	}

	private List<Integer> queryResults(String queryParameter) {
		// TODO Rewrite this using Database.java or discuss
		// with Architects to convert everything to JDBCTemplate
		return jdbcTemplate.query(QUERY_GROUP_IDS_FOR_USER, buildRowMapper(), queryParameter);
	}

	private RowMapper<Integer> buildRowMapper() {
		RowMapper<Integer> rowMapper = new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt("groupID");
			}

		};

		return rowMapper;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
