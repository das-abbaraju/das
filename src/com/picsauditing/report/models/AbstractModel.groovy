package com.picsauditing.report.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.report.tables.ReportForeignKey;
import com.picsauditing.report.tables.ReportOnClause;
import com.picsauditing.util.Strings;

abstract class AbstractModel {
	ReportJoin startingJoin;
	protected Permissions permissions;

	private static final Logger logger = LoggerFactory.getLogger(AbstractModel.class);

	public AbstractModel(Permissions permissions, AbstractTable startingTable) {
		this.permissions = permissions;
		startingJoin = parseSpec(startingTable, getJoinSpec())
		logger.info("Finished building joins \n" + startingJoin)
	}

	ReportJoin parseSpec(AbstractTable toTable, Map joinDef) {
		if (joinDef == null) {
			throw new Exception("getJoinSpec() is null on model " + this);
		}
		logger.info("parsingSpec for " + toTable);
		ReportJoin join = new ReportJoin();
		join.setToTable(toTable);

		if (joinDef.alias) {
			join.alias = joinDef.alias
		}
		
		joinDef.joins.each { childJoinDSL ->
			System.out.println(this);
			ReportForeignKey key = toTable.getKey(childJoinDSL.key);
			if (key == null) {
				throw new Exception("key property is missing on a child join from " + join.alias);
			}
			ReportJoin childJoin = parseSpec(key.getTable(), childJoinDSL);

			childJoin.required = key.required;
			if (Strings.isEmpty(childJoin.getAlias())) {
				childJoin.alias = join.alias + key;
			}

			ReportOnClause onClause = key.onClause;
			String onClauseSql = onClause.toSql(join.getAlias(), childJoin.getAlias(), permissions);
			childJoin.setOnClause(onClauseSql)

			childJoin.minimumImportance = key.minimumImportance;
			if (childJoinDSL.minimumImportance) {
				logger.debug("Overriding minimum importance " + childJoin.alias + " to " + childJoinDSL.minimumImportance);
				childJoin.minimumImportance = childJoinDSL.minimumImportance;
			}

			if (childJoinDSL.category) {
				logger.debug("Overriding category " + childJoin.alias + " to " + childJoinDSL.category);
				childJoin.setCategory(childJoinDSL.category);
			}

			join.getJoins().add(childJoin)
		}

		return join;
	}
	
	abstract Map getJoinSpec();

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
