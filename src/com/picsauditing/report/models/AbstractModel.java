package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.report.tables.ReportForeignKey;
import com.picsauditing.report.tables.ReportOnClause;
import com.picsauditing.util.Strings;

public abstract class AbstractModel {
	ReportJoin startingJoin;
	protected Permissions permissions;

	private static final Logger logger = LoggerFactory.getLogger(AbstractModel.class);

	public AbstractModel(Permissions permissions, AbstractTable startingTable) {
		this.permissions = permissions;
		startingJoin = parseSpec(startingTable, getJoinSpec());
		logger.info("Finished building joins \n" + startingJoin);
	}

	ReportJoin parseSpec(AbstractTable toTable, ModelSpec modelSpec) {
		if (modelSpec == null) {
			throw new RuntimeException("getJoinSpec() is null on model " + this);
		}
		logger.info("parsingSpec for " + toTable);
		ReportJoin join = new ReportJoin();
		join.setToTable(toTable);

		for (ModelSpec childSpec : modelSpec.joins) {
			ReportForeignKey key = getKey(toTable, childSpec.key);
			ReportJoin childJoin = appendToJoin(join.getAlias(), childSpec, key);
			join.getJoins().add(childJoin);
		}

		return join;
	}

	private ReportForeignKey getKey(AbstractTable toTable, String keyName) {
		ReportForeignKey key = toTable.getKey(keyName);
		if (key == null) {
			throw new RuntimeException("key property is missing on a child join");
		}
		return key;
	}

	private ReportJoin appendToJoin(String fromAlias, ModelSpec childSpec, ReportForeignKey key) {
		ReportJoin childJoin = parseSpec(key.getTable(), childSpec);

		childJoin.setRequired(key.isRequired());
		childJoin.setAlias(childSpec.alias);

		ReportOnClause onClause = key.getOnClause();
		String onClauseSql = onClause.toSql(fromAlias, childJoin.getAlias(), permissions);
		childJoin.setOnClause(onClauseSql);
		
		childJoin.setMinimumImportance(key.getMinimumImportance());
		if (childSpec.minimumImportance != null) {
			logger.debug("Overriding minimum importance " + childJoin.getAlias() + " to " + childSpec.minimumImportance);
			childJoin.setMinimumImportance(childSpec.minimumImportance);
		}

		if (key.getCategory() != null) {
			logger.debug("Overriding category from ForeignKey " + childJoin.getAlias() + " to " + key.getCategory());
			childJoin.setCategory(key.getCategory());
		}
		
		if (childSpec.category != null) {
			logger.debug("Overriding category from ModelSpec " + childJoin.getAlias() + " to " + childSpec.category);
			childJoin.setCategory(childSpec.category);
		}
		return childJoin;
	}

	abstract ModelSpec getJoinSpec();
	
	public ReportJoin getStartingJoin() {
		return startingJoin;
	}

	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = new HashMap<String, Field>();
		for (Field field : startingJoin.getFields()) {
			if (field.canUserSeeQueryField(permissions)) {
				fields.put(field.getName().toUpperCase(), field);
			}
		}
		return fields;
	}

	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		return "";
	}
}
