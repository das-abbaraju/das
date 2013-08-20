package com.picsauditing.report.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.picsauditing.report.fields.QueryFilterOperator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.JoinType;
import com.picsauditing.report.tables.ReportOnClause;
import com.picsauditing.util.Strings;

public class ReportJoin {
    private String thirdAlias = "";
	private String fromAlias = "";
	private String alias = "";
	private AbstractTable toTable;
	private List<ReportJoin> joins = new ArrayList<ReportJoin>();
	private String onClause;
	private FieldImportance minimumImportance = FieldImportance.Low;
	private FieldCategory category = null;
	private JoinType joinType;

	private static final Logger logger = LoggerFactory.getLogger(ReportJoin.class);

	private boolean importantEnough(Field field) {
		boolean importantEnough = minimumImportance.ordinal() <= field.getImportance().ordinal();
		if (importantEnough) {
			logger.debug("Including " + alias + "." + field.getName());
		} else {
			logger.debug("   Excluding " + alias + "." + field.getName());
		}
		return importantEnough;
	}

    public String getThirdAlias() {
        return thirdAlias;
    }

    public void setThirdAlias(String thirdAlias) {
        this.thirdAlias = thirdAlias;
    }

    public String getFromAlias() {
		return fromAlias;
	}

	public void setFromAlias(String fromAlias) {
		this.fromAlias = fromAlias;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public AbstractTable getToTable() {
		return toTable;
	}

	public void setToTable(AbstractTable toTable) {
		this.toTable = toTable;
	}

	public List<ReportJoin> getJoins() {
		return joins;
	}

	public void setJoins(List<ReportJoin> joins) {
		this.joins = joins;
	}

	public void setOnClause(String onClause) {
		this.onClause = onClause;
	}

	public FieldCategory getCategory() {
		return category;
	}

	public void setCategory(FieldCategory categoryOverride) {
		this.category = categoryOverride;
	}

	public void setMinimumImportance(FieldImportance minimumImportance) {
		this.minimumImportance = minimumImportance;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

	public Collection<Field> getFields() {
		logger.debug("Getting fields for " + alias + " with Importance >= " + minimumImportance);
		Collection<Field> fields = new ArrayList<Field>();
		for (Field field : toTable.getFields()) {
			if (importantEnough(field)) {
				Field fieldCopy = field.clone();

				// Update the aliases
				// TODO This is scary, we should find a better way to update
				// this
				fieldCopy.setName(alias + fieldCopy.getName());
				// if you have the report on clause aliases in string, then
				// don't put the leading toAlias. Instead, replace the existing
				// locations throughout the string
				if (fieldCopy.getDatabaseColumnName().contains(ReportOnClause.Alias)) {
					if (fieldCopy.getDatabaseColumnName().contains(ReportOnClause.ToAlias))
						fieldCopy.setDatabaseColumnName(fieldCopy.getDatabaseColumnName().replace(
								ReportOnClause.ToAlias, alias));
					if (fieldCopy.getDatabaseColumnName().contains(ReportOnClause.FromAlias))
						fieldCopy.setDatabaseColumnName(fieldCopy.getDatabaseColumnName().replace(
								ReportOnClause.FromAlias, fromAlias));
				} else {
					fieldCopy.setDatabaseColumnName(alias + "." + fieldCopy.getDatabaseColumnName());
				}

				if (Strings.isNotEmpty(fieldCopy.getUrl()) && fieldCopy.getUrl().contains(ReportOnClause.ToAlias)) {
					fieldCopy.setUrl(fieldCopy.getUrl().replace(ReportOnClause.ToAlias, alias));
				}

				if (category != null) {
					fieldCopy.setCategory(category);
				}
				fields.add(fieldCopy);
			}
		}

		for (ReportJoin join : joins) {
			fields.addAll(join.getFields());
		}

		return fields;
	}

	public boolean isNeeded(Report report) {
		logger.debug("Is " + alias + " required?");
		if (joinType == JoinType.RequiredJoin)
			return true;

		if (report == null)
			return false;

		for (Field field : getFields()) {
			String fieldName = field.getName();
			for (Column column : report.getColumns()) {
				String columnName = column.getFieldNameWithoutMethod();
				if (columnName.equalsIgnoreCase(fieldName))
					return true;

				if (column.getField() == null) {
					logger.error("ReportJoin.isNeeded() - Column: "  + column + " has a null field");
				} else {
					for (String dependentField : column.getField().getDependentFields()) {
						if (dependentField.equalsIgnoreCase(fieldName))
							return true;
					}

                    String columnRequiredJoin = column.getField().getRequiredJoin();
                    if (alias.equalsIgnoreCase(columnRequiredJoin)) {
                        return true;
                    }
				}
            }

			for (Filter filter : report.getFilters()) {
				String filterName = filter.getFieldNameWithoutMethod();
				if (filterName.equalsIgnoreCase(fieldName)) {
                    if (isFilterUsed(filter))
                        return true;
                }

                if (filter.getField() == null) {
                    logger.error("ReportJoin.isNeeded() - filter: "  + filter + " has a null field");
                } else {
                    String filterRequiredJoin = filter.getField().getRequiredJoin();
                    if (alias.equalsIgnoreCase(filterRequiredJoin)) {
                        if (isFilterUsed(filter))
                            return true;
                    }
                }

                if (filter.getFieldForComparison() != null) {
                    String comparedColumnName = filter.getFieldForComparison().getName();
                    if (comparedColumnName.equalsIgnoreCase(fieldName))
                        return true;
                }
			}

			for (Sort sort : report.getSorts()) {
				String sortName = sort.getFieldNameWithoutMethod();
				if (sortName.equalsIgnoreCase(fieldName))
					return true;
			}
		}

		logger.debug("JOIN to " + alias + " is being excluded");
		return false;
	}

    private boolean isFilterUsed(Filter filter) {
        return !StringUtils.isEmpty(filter.getValue()) || !StringUtils.isEmpty(filter.getColumnCompare())
                || filter.getOperator() == QueryFilterOperator.Empty || filter.getOperator() == QueryFilterOperator.NotEmpty;
    }

    public String getTableClause() {
		if (isAliasDifferent())
			return toTable.toString() + " AS " + alias;

		return toTable.toString();
	}

	private boolean isAliasDifferent() {
		if (Strings.isEmpty(alias))
			return false;
		if (alias.equals(toTable.toString()))
			return false;
		return true;
	}

	public String toJoinClause() {
		String value = (joinType == JoinType.LeftJoin ? "LEFT " : "") + "JOIN " + getTableClause();
		return value + " ON " + onClause;
	}

	public String toString() {
		String value = toJoinClause();
		for (ReportJoin join : joins) {
			value += "\n" + join.toString();
		}
		return value;
	}
}
