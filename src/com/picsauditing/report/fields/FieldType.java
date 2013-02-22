package com.picsauditing.report.fields;

import javax.persistence.EnumType;

import com.picsauditing.actions.autocomplete.AbstractAutocompleteService;
import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.jpa.entities.*;

import com.picsauditing.util.SpringUtils;

import java.util.HashSet;
import java.util.Set;

public enum FieldType {
	AccountID(FilterType.AccountID, DisplayType.Number, SqlFunctionProfile.Boolean, null, null),
	AccountLevel(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, AccountLevel.class, EnumType.STRING),
	AccountStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, AccountStatus.class, EnumType.STRING),
	AccountType(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, AccountType.class, EnumType.STRING),
	AccountUser(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	ApprovalStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, ApprovalStatus.class, EnumType.STRING),
	AuditStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, AuditStatus.class, EnumType.STRING),
	AuditSubStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, AuditSubStatus.class, EnumType.STRING),
	AuditType(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	AuditTypeClass(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, AuditTypeClass.class, EnumType.STRING),
	AuditQuestion(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	AuditCategory(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	Boolean(FilterType.Boolean, DisplayType.Boolean, SqlFunctionProfile.Boolean, null, null),
	Contractor(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	ContractorOperatorNumberType(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, ContractorOperatorNumberType.class, EnumType.STRING),
	Country(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	CountrySubdivision(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	Currency(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, Currency.class, EnumType.STRING),
	Date(FilterType.Date, DisplayType.String, SqlFunctionProfile.Date, null, null),
	DateTime(FilterType.Date, DisplayType.String, SqlFunctionProfile.Date, null, null),
	FeeClass(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, FeeClass.class, EnumType.STRING),
	FlagColor(FilterType.Multiselect, DisplayType.Flag, SqlFunctionProfile.Boolean, FlagColor.class, EnumType.STRING),
	FlagCriteriaOptionCode(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, FlagCriteriaOptionCode.class, EnumType.STRING),
	Float(FilterType.Number, DisplayType.Number, SqlFunctionProfile.Number, null, null),
	Integer(FilterType.Number, DisplayType.Number, SqlFunctionProfile.Number, null, null),
	LowMedHigh(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, LowMedHigh.class, EnumType.ORDINAL),
	MultiYearScope(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, MultiYearScope.class, EnumType.STRING),
	NetworkLevel(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, NetworkLevel.class, EnumType.ORDINAL),
	Number(FilterType.Number, DisplayType.Number, SqlFunctionProfile.Number, null, null),
	Operator(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	OperatorTag(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	OptionGroup(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	OptionValue(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	OshaRateType(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, OshaRateType.class, EnumType.STRING),
	OshaType(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, OshaType.class, EnumType.STRING),
	PaymentMethod(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, PaymentMethod.class, EnumType.STRING),
	String(FilterType.String, DisplayType.String, SqlFunctionProfile.String, null, null),
	Trade(FilterType.Autocomplete, DisplayType.String, SqlFunctionProfile.String, null, null),
	TransactionStatus(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, TransactionStatus.class, EnumType.STRING),
	UserAccountRole(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, UserAccountRole.class, EnumType.STRING),
	UserID(FilterType.UserID, DisplayType.Number, SqlFunctionProfile.Boolean, null, null),
	WaitingOn(FilterType.Multiselect, DisplayType.String, SqlFunctionProfile.String, WaitingOn.class, EnumType.ORDINAL);

	private FilterType filterType;
	private DisplayType displayType;
	private SqlFunctionProfile sqlFunctionProfile;
	private Class<? extends Enum> enumClass;
	private EnumType enumType;

	<E extends Enum<E>> FieldType(FilterType filterType, DisplayType displayType, SqlFunctionProfile sqlFunctionProfile, Class<E> enumClass, EnumType enumType) {
		this.filterType = filterType;
		this.displayType = displayType;
		this.sqlFunctionProfile = sqlFunctionProfile;
		this.enumClass = enumClass;
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

	public SqlFunctionProfile getSqlFunctionProfile() {
		return sqlFunctionProfile;
	}

	public Class<? extends Enum> getEnumClass() {
		return enumClass;
	}

	public EnumType getEnumType() {
		return enumType;
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
