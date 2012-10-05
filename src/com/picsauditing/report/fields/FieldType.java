package com.picsauditing.report.fields;

import javax.persistence.EnumType;

import com.picsauditing.actions.autocomplete.AbstractAutocompleteService;
import com.picsauditing.util.SpringUtils;

public enum FieldType {
	AccountID,
	AccountLevel(EnumType.STRING),
	AccountStatus(EnumType.STRING),
	ApprovalStatus(EnumType.STRING),
	AuditStatus(EnumType.STRING),
	AuditSubStatus(EnumType.STRING),
	AuditType(FilterType.Autocomplete),
	AuditTypeClass(EnumType.STRING),
	AuditQuestion(FilterType.Autocomplete),
	AuditCategory(FilterType.Autocomplete), 
	Boolean,
	Contractor(FilterType.Autocomplete), 
	ContractorOperatorNumberType(EnumType.STRING),
	Country(FilterType.Autocomplete),
	CountrySubdivision(FilterType.Autocomplete),
	Currency(EnumType.STRING),
	Date,
	DateTime,
	FlagColor(EnumType.STRING, DisplayType.Flag),
	Float,
	Integer,
	LowMedHigh(EnumType.ORDINAL),
	Number(FilterType.Integer, DisplayType.Number),
	Operator(FilterType.Autocomplete),
	OperatorTag(FilterType.Autocomplete), 
	OptionGroup(FilterType.Autocomplete),
	OptionValue(FilterType.Autocomplete),
	PaymentMethod(EnumType.STRING),
	String,
	Trade(FilterType.Autocomplete), 
	TransactionStatus(EnumType.STRING),
	UserID,
	WaitingOn(EnumType.ORDINAL);

	private FilterType filterType;
	private DisplayType displayType;
	private EnumType enumType;

	private FieldType() {
		setFilterType(FilterType.valueOf(this.toString()));
	}

	private FieldType(FilterType filterType) {
		setFilterType(filterType);
	}

	private FieldType(FilterType filterType, DisplayType displayType) {
		setFilterType(filterType);
		this.displayType = displayType;
	}

	private FieldType(EnumType enumType) {
		setEnumType(enumType);
	}

	private FieldType(EnumType enumType, DisplayType displayType) {
		setEnumType(enumType);
		this.displayType = displayType;
	}

	private void setEnumType(EnumType enumType) {
		this.enumType = enumType;
		setFilterType(FilterType.ShortList);
	}

	private void setFilterType(FilterType filterType) {
		this.filterType = filterType;
		this.displayType = filterType.getDisplayType();
	}

	public void setDisplayType(DisplayType displayType) {
		this.displayType = displayType;
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
}
