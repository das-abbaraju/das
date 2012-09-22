package com.picsauditing.report.models;

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions
import com.picsauditing.report.Column
import com.picsauditing.report.Definition
import com.picsauditing.report.Filter
import com.picsauditing.report.Sort
import com.picsauditing.report.fields.Field
import com.picsauditing.report.tables.FieldImportance
import com.picsauditing.report.tables.ReportForeignKey
import com.picsauditing.report.tables.ReportOnClause
import com.picsauditing.report.tables.AbstractTable
import com.picsauditing.util.Strings

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
		logger.info("parsingSpec for " + toTable)
		ReportJoin join = new ReportJoin();
		join.setToTable(toTable)

		if (joinDef.alias) {
			join.setAlias(joinDef.alias)
		}

		joinDef.joins.each { childJoinDSL ->
			println this;
			ReportForeignKey key = toTable.getKey(childJoinDSL.key)
			if (key != null) {
				ReportJoin childJoin = parseSpec(key.getTable(), childJoinDSL)

				childJoin.required = key.required
				if (Strings.isEmpty(childJoin.getAlias())) {
					childJoin.setAlias(join.getAlias() + key);
				}

				ReportOnClause onClause = key.onClause
				String onClauseSql = onClause.toSql(join.getAlias(), childJoin.getAlias(), permissions)
				childJoin.setOnClause(onClauseSql)

				childJoin.minimumImportance = key.minimumImportance
				if (childJoinDSL.minimumImportance) {
					logger.debug("Overriding minimum importance " + childJoin.alias + " to " + childJoinDSL.minimumImportance)
					childJoin.minimumImportance = childJoinDSL.minimumImportance
				}

				if (childJoinDSL.category) {
					logger.debug("Overriding category " + childJoin.alias + " to " + childJoinDSL.category)
					childJoin.category = childJoinDSL.category
				}

				join.getJoins().add(childJoin)
			}
		}

		return join
	}

	abstract Map getJoinSpec()

	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = new HashMap<String, Field>();
		for (Field field : startingJoin.getFields()) {
			if (canSee(field)) {
				fields.put(field.getName().toUpperCase(), field)
			}
		}
		return fields;
	}

	public boolean canSee(Field field) {
		if (field.requiredPermission == OpPerms.None)
			return true;

		return permissions.hasPermission(field.requiredPermission)
	}

	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		return "";
	}
}
