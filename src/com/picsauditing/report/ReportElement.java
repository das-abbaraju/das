package com.picsauditing.report;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.SqlFunction;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class ReportElement implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(ReportElement.class);

	public static String METHOD_SEPARATOR = "__";

	protected int id;
	protected Report report;
	protected String name;
	protected SqlFunction sqlFunction;
	protected Field field;

	private String originalName;

	public ReportElement() {
	}

	public ReportElement(String fieldName) {
		setName(fieldName);
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "reportID", nullable = false, updatable = false)
	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	@Column(nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		// TODO verify somewhere no quotes exist Strings.escapeQuotes(name)
		this.name = name;
	}

	public void setMethodToFieldName() {
		int startOfMethod = name.lastIndexOf(METHOD_SEPARATOR);
		if (startOfMethod >= 0 || sqlFunction == null)
			return;

		this.name = name + METHOD_SEPARATOR + sqlFunction;
		parseFieldNameMethod();
	}

	private void parseFieldNameMethod() {
		sqlFunction = null;
		originalName = name;

		int startOfMethod = name.lastIndexOf(METHOD_SEPARATOR);
		if (startOfMethod < 0)
			return;

		originalName = name.substring(0, startOfMethod);
		String methodName = name.substring(startOfMethod + 2);
		sqlFunction = SqlFunction.valueOf(methodName);
	}

	@Transient
	public String getFieldNameWithoutMethod() {
		return originalName;
	}

	public SqlFunction getSqlFunction() {
		return sqlFunction;
	}

	public void setSqlFunction(SqlFunction method) {
		this.sqlFunction = method;
		if (method == null)
			name = originalName;
		else
			name = originalName + METHOD_SEPARATOR + method.toString();
	}

	@Transient
	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
		this.field.setName(name);
	}

	@Transient
	public boolean isHasAggregateMethod() {
		if (sqlFunction == null)
			return false;

		return sqlFunction.isAggregate();
	}

	@Transient
	public String getSql() {
		if (field == null) {
			throw new RuntimeException(name + " is missing from available fields");
		}
		String fieldSql = field.getDatabaseColumnName();
		if (sqlFunction == null)
			return fieldSql;

		if (sqlFunction.isAggregate()) {
			field.setUrl(null);
		}

		switch (sqlFunction) {
		case Average:
			return "AVG(" + fieldSql + ")";
		case Count:
			return "COUNT(" + fieldSql + ")";
		case CountDistinct:
			return "COUNT(DISTINCT " + fieldSql + ")";
		case Date:
			return "DATE(" + fieldSql + ")";
		case GroupConcat:
			return "GROUP_CONCAT(" + fieldSql + ")";
		case Hour:
			return "HOUR(" + fieldSql + ")";
		case Length:
			return "LENGTH(" + fieldSql + ")";
		case Left:
			return "LEFT(" + fieldSql + ")";
		case LowerCase:
			return "LOWER(" + fieldSql + ")";
		case Max:
			return "MAX(" + fieldSql + ")";
		case Min:
			return "MIN(" + fieldSql + ")";
		case Month:
			return "MONTH(" + fieldSql + ")";
		case Round:
			return "ROUND(" + fieldSql + ")";
		case StdDev:
			return "STDDEV(" + fieldSql + ")";
		case Sum:
			return "SUM(" + fieldSql + ")";
		case UpperCase:
			return "UPPER(" + fieldSql + ")";
		case WeekDay:
			return "DATE_FORMAT(" + fieldSql + ",'%W')";
		case Year:
			return "YEAR(" + fieldSql + ")";
		case YearMonth:
			return "DATE_FORMAT(" + fieldSql + ",'%Y-%m')";
		}

		return fieldSql;
	}

	public void addFieldCopy(Map<String, Field> availableFields) {
		Field field = availableFields.get(originalName.toUpperCase());

		if (field == null) {
			logger.warn("Failed to find " + originalName + " in availableFields");
			return;
		}

		setField(field.clone());
		this.field.setName(name);
	}

	public String toString() {
		return name;
	}
}