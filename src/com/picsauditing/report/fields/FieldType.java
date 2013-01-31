package com.picsauditing.report.fields;

import javax.persistence.EnumType;

import com.picsauditing.actions.autocomplete.AbstractAutocompleteService;
import com.picsauditing.util.SpringUtils;

import java.util.HashSet;
import java.util.Set;

public enum FieldType {
	AccountID(FilterType.AccountID, DisplayType.Number, SqlFunctionProfile.Boolean, null),
	AccountLevel(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	AccountStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	AccountType(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	AccountUser(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	ApprovalStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	AuditStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	AuditSubStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	AuditType(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	AuditTypeClass(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	AuditQuestion(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	AuditCategory(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	Boolean(FilterType.Boolean, DisplayType.Boolean, SqlFunctionProfile.Boolean, null),
	Contractor(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	ContractorOperatorNumberType(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	Country(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	CountrySubdivision(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	Currency(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	Date(FilterType.Date, DisplayType.String, SqlFunctionProfile.Date, null),
	DateTime(FilterType.DateTime, DisplayType.String, SqlFunctionProfile.Date, null),
	FlagColor(FilterType.Multiselect, DisplayType.Flag, SqlFunctionProfile.Boolean, EnumType.STRING),
	FlagCriteriaOptionCode(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	Float(FilterType.Number, DisplayType.Number, SqlFunctionProfile.Number, null),
	Integer(FilterType.Number, DisplayType.Number, SqlFunctionProfile.Number, null),
	LowMedHigh(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.ORDINAL),
	MultiYearScope(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	NetworkLevel(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.ORDINAL),
	Number(FilterType.Number, DisplayType.Number, SqlFunctionProfile.Number, null),
	Operator(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	OperatorTag(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	OptionGroup(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	OptionValue(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	OshaRateType(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	OshaType(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	PaymentMethod(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	String(FilterType.String, DisplayType.String, SqlFunctionProfile.String, null),
	Trade(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null),
	TransactionStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	UserAccountRole(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.STRING),
	UserID(FilterType.UserID, DisplayType.Number, SqlFunctionProfile.Boolean, null),
	WaitingOn(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, EnumType.ORDINAL);

	private FilterType filterType;
	private DisplayType displayType;
	private EnumType enumType;
	private SqlFunctionProfile sqlFunctionProfile;

	FieldType(FilterType filterType, DisplayType displayType, SqlFunctionProfile sqlFunctionProfile, EnumType enumType) {
		this.filterType = filterType;
		this.displayType = displayType;
		this.sqlFunctionProfile = sqlFunctionProfile;
		this.enumType = enumType;
	}

	/**
	 * The Bean Name in the Spring Configuration must match the Enum, otherwise
	 * the bean will not be found.
	 *
	 * @return Reusable instance of an AutocompleteService.
	 * @throws RuntimeException if this FieldType is not a FilterType.Autocomplete
	 */
	public AbstractAutocompleteService<?> getAutocompleteService() {
		if (this.filterType != FilterType.Autocomplete) {
			throw new RuntimeException("This FieldType is not an Autocomplete.");
		}

		return SpringUtils.getBean(this.toString() + "AutocompleteService");
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}

	public EnumType getEnumType() {
		return enumType;
	}

	public SqlFunctionProfile getSqlFunctionProfile() {
		return sqlFunctionProfile;
	}

	public static Set<FieldType> getAllBySqlFunctionProfile(SqlFunctionProfile sqlFunctionProfile) {
		Set<FieldType> fieldTypes = new HashSet<FieldType>();
		for (FieldType fieldType : FieldType.values()) {
			if (fieldType.sqlFunctionProfile == sqlFunctionProfile) {
				fieldTypes.add(fieldType);
			}
		}
		return fieldTypes;
	}

	public Set<SqlFunction> getSqlFunctions() {
		Set<SqlFunction> sqlFunctions = new HashSet<SqlFunction>();
		for (SqlFunctionGroup sqlFunctionGroup : sqlFunctionProfile.getSqlFunctionGroups()) {
			sqlFunctions.addAll(sqlFunctionGroup.getSqlFunctions());
		}
		sqlFunctions = processExceptionCases(sqlFunctions);
		return sqlFunctions;
	}

	private Set<SqlFunction>  processExceptionCases(Set<SqlFunction> sqlFunctions) {
		if (sqlFunctionProfile == SqlFunctionProfile.Date) {
			sqlFunctions.remove(SqlFunction.Length);
		}
		if (this == FieldType.Date) {
			sqlFunctions.remove(SqlFunction.Date);
		}
		if (this == FieldType.Integer) {
			sqlFunctions.remove(SqlFunction.Round);
		}
		return sqlFunctions;

	}
}
