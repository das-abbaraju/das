package com.picsauditing.report.models;

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions
import com.picsauditing.report.Column
import com.picsauditing.report.Definition
import com.picsauditing.report.Filter
import com.picsauditing.report.Sort
import com.picsauditing.report.fields.Field
import com.picsauditing.report.tables.FieldImportance
import com.picsauditing.report.tables.ReportForeignKey
import com.picsauditing.report.tables.ReportOnClause
import com.picsauditing.report.tables.ReportTable
import com.picsauditing.util.Strings

abstract class AbstractModel {
	protected ReportJoin startingJoin;
	protected Permissions permissions;
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractModel.class);
	
	public AbstractModel(Permissions permissions, ReportTable startingTable) {
		this.permissions = permissions;
		startingJoin = parseSpec(startingTable, getJoinSpec())
		System.out.println("Finished building joins \n" + startingJoin);
	}
	
	ReportJoin parseSpec(ReportTable toTable, Map joinDef) {
		System.out.println("parsingSpec for " + toTable);
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
	
	abstract Map getJoinSpec()
	
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = new HashMap<String, Field>();
		for (Field field : startingJoin.getFields()) {
			fields.put(field.getName(), field)
		}
		return fields;
	}

	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		return "";
	}

	public String getFromClause() {
		return "FROM " + startingJoin.getTableClause();
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
