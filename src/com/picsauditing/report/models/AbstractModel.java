package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.report.tables.ReportForeignKey;
import com.picsauditing.report.tables.ReportOnClause;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

public abstract class AbstractModel {
	public static final String ACCOUNT = "Account";

	ReportJoin startingJoin;
	protected Permissions permissions;
	protected PermissionQueryBuilder permissionQueryBuilder;
    private Map<String, String> urls = new HashMap<String, String>();

	private static final Logger logger = LoggerFactory.getLogger(AbstractModel.class);

	public AbstractModel(Permissions permissions, AbstractTable startingTable) {
		this.permissions = permissions;
		ModelSpec joinSpec = getJoinSpec();
		startingJoin = parseSpec(startingTable, joinSpec);
		logger.info("Finished building joins \n" + startingJoin);
	}

	private ReportJoin parseSpec(AbstractTable toTable, ModelSpec modelSpec) {
		if (modelSpec == null) {
			throw new RuntimeException("getJoinSpec() is null on model " + this);
		}
		logger.info("parsingSpec for " + toTable);
		ReportJoin join = new ReportJoin();
		join.setToTable(toTable);
		join.setFromAlias(modelSpec.fromAlias);
		join.setAlias(modelSpec.alias);

		if (modelSpec.category != null) {
			join.setCategory(modelSpec.category);
		}

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

		childJoin.setJoinType(key.getJoinType());
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

    public void addUrl(String fieldName, String url) {
        urls.put(fieldName.toUpperCase(), url);
    }

	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = new HashMap<String, Field>();
		for (Field field : startingJoin.getFields()) {
			if (field.canUserSeeQueryField(permissions)) {
                final String fieldName = field.getName().toUpperCase();
				fields.put(fieldName, field);
                if (urls.containsKey(fieldName)) {
                    field.setUrl(urls.get(fieldName));
                }
                logger.debug(fieldName + " was added to the available fields.");
			}
		}
		return fields;
	}

	public String getWhereClause(List<Filter> filters) {
		permissionQueryBuilder = new PermissionQueryBuilder(permissions);
		permissionQueryBuilder.setAccountAlias(ACCOUNT);

		return Strings.EMPTY_STRING;
	}
}
