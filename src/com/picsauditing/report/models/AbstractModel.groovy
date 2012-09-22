package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.Column
import com.picsauditing.report.Definition
import com.picsauditing.report.Filter
import com.picsauditing.report.Sort
import com.picsauditing.report.fields.Field
import com.picsauditing.report.tables.ReportForeignKey
import com.picsauditing.report.tables.ReportOnClause
import com.picsauditing.report.tables.ReportTable
import com.picsauditing.util.Strings

abstract class AbstractModel {
	protected ReportJoin startingJoin;
	protected Permissions permissions;
	// protected Map<String, Field> availableFields = new HashMap<String, Field>();
	
	public AbstractModel(Permissions permissions, ReportTable startingTable) {
		this.permissions = permissions;
		startingJoin = parse(startingTable, getJoinSpec())
		System.out.println("Finished building joins \n" + join);
	}
	
	abstract Map getJoinSpec()
	
	protected ReportTable join(ReportTable table, Closure cl) {
		cl.delegate = table
		ReportForeignKey join = cl()
		addJoin(join, permissions);
	}

	protected ReportJoin from(String alias) {
		System.out.println("Starting table = " + alias);
		startingJoin = new ReportJoin(toTable: fromTable, alias: alias);
	}

	protected ReportJoin parse(ReportTable toTable, Map joinDef) {
		System.out.println("From " + toTable);
		ReportJoin join = new ReportJoin();
		join.setToTable(toTable)
		join.setAlias(joinDef.alias)
		// availableFields.putAll(fromTable.getAvailableFields(permissions));
		joinDef.joins.each { childJoinDSL ->
			ReportForeignKey key = toTable.getKey(childJoinDSL.key)
			if (key != null) {
				ReportJoin childJoin = parse(key.getTable(), childJoinDSL)
				if (Strings.isEmpty(childJoin.getAlias())) {
					childJoin.setAlias(join.getAlias() + key);
				}
				ReportOnClause onClause = key.getOnClause()
				String onClauseSql = onClause.toSql(join.getAlias(), childJoin.getAlias(), permissions)
				childJoin.setOnClause(onClauseSql)
				join.getJoins().add(childJoin)
			}
		}
		
		return join
	}
	
	public Map<String, Field> getAvailableFields() {
		return availableFields;
	}

	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		return "";
	}

	public String getFromClause() {
		return fromTable.toString();
	}

	public Collection<ReportForeignKey> getJoins() {
		return joins.values();
	}

	public boolean isJoinNeeded(ReportForeignKey join, Definition definition) {
		if (join.isRequired())
			return true;

		//		for (ReportJoin otherJoin : joins.values()) {
		//			if (join != otherJoin && otherJoin.requires(join))
		//				return true;
		//		}

		if (definition == null)
			return false;

		for (Field field : availableFields.values()) {
			String fieldName = field.getName();
			for (Column column : definition.getColumns()) {
				String columnName = column.getFieldName();
				if (columnName.equals(fieldName))
					return true;
			}

			for (Filter filter : definition.getFilters()) {
				String filterName = filter.getFieldName();
				if (filterName.equals(fieldName))
					return true;
			}

			for (Sort sort : definition.getSorts()) {
				String sortName = sort.getFieldName();
				if (sortName.equals(fieldName))
					return true;
			}
		}

		return false;
	}
}
