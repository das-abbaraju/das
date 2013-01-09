package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.search.IndexValueType;
import com.picsauditing.search.IndexableField;
import com.picsauditing.util.Strings;

@MappedSuperclass
public abstract class ReportElement {

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
	@IndexableField(type = IndexValueType.STRINGTYPE, weight = 10)
	@ReportField(type = FieldType.Integer)
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

	@Column(length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		
		if (originalName == null) {
			originalName = name;
		}
	}

	public void setMethodToFieldName() {
		int startOfMethod = name.lastIndexOf(METHOD_SEPARATOR);
		if (startOfMethod >= 0 || sqlFunction == null) {
			return;
		}

		this.name = name + METHOD_SEPARATOR + sqlFunction;
		sqlFunction = null;

		originalName = name.substring(0, startOfMethod);
		String methodName = name.substring(startOfMethod + 2);
		sqlFunction = SqlFunction.valueOf(methodName);
	}

	@Transient
	public String getFieldNameWithoutMethod() {
//		return originalName;
		if (Strings.isEmpty(name)) {
			return Strings.EMPTY_STRING;
		}
		
		int index = name.indexOf(METHOD_SEPARATOR);
		if (index == -1) {
			return name;
		} else {
			return name.substring(0, index);
		}
	}

	@Enumerated(EnumType.STRING)
	public SqlFunction getSqlFunction() {
		return sqlFunction;
	}

	public void setSqlFunction(SqlFunction method) {
		this.sqlFunction = method;
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
//		Field field = availableFields.get(getFieldNameWithoutMethod());

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