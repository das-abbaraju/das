package com.picsauditing.report.fields;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Transient;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.util.Strings;

public class Field {

	private static final Pattern FIELD_VARIABLE_PATTERN = Pattern.compile("\\{(\\w+)\\}");

	private FieldType type = FieldType.String;
	private String name;
	private FieldCategory category = FieldCategory.General;
	private String categoryTranslation;
	private String text;
	private String suffix;
	private String url;
	private int width = 200;
	private String help;

	private Class<?> fieldClass;
	private String databaseColumnName;
	private boolean visible = true;
	private boolean filterable = true;
	private boolean sortable = true;
	private String preTranslation;
	private String postTranslation;
	private OpPerms requiredPermission = OpPerms.None;
	private FieldImportance importance = FieldImportance.Low;

	public Field(ReportField annotation) {
		type = annotation.type();
		width = annotation.width();
		url = annotation.url();
		category = annotation.category();
		requiredPermission = annotation.requiredPermissions();
		visible = annotation.visible();
		filterable = annotation.filterable();
		sortable = annotation.sortable();
		importance = annotation.importance();

		preTranslation = annotation.i18nKeyPrefix();
		postTranslation = annotation.i18nKeySuffix();
		if (type.getFilterType() == FilterType.ShortList && Strings.isEmpty(preTranslation)) {
			preTranslation = type.toString();
		}
	}

	public Field(String name) {
		this.name = name;
		this.databaseColumnName = name;
	}

	public Field(String name, String databaseColumnName, FieldType type) {
		this.name = name;
		this.databaseColumnName = databaseColumnName;
		if (type == null) {
			throw new RuntimeException("type is required when creating Fields");
		}
		
		this.type = type;
	}

	public Field setTranslationPrefixAndSuffix(String prefix, String suffix) {
		this.preTranslation = prefix;
		this.postTranslation = suffix;
		return this;
	}

	public String getI18nKey(String value) {
		String key = value;

		if (!Strings.isEmpty(preTranslation))
			key = preTranslation + "." + key;

		if (!Strings.isEmpty(postTranslation))
			key = key + "." + postTranslation;

		return key;
	}

	public Set<String> getDependentFields() {
		Set<String> dependent = new HashSet<String>();
		if (!Strings.isEmpty(url)) {
			Matcher urlFieldMatcher = FIELD_VARIABLE_PATTERN.matcher(url);

			while (urlFieldMatcher.find()) {
				dependent.add(urlFieldMatcher.group(1));
			}
		}

		return dependent;
	}

	public boolean isTranslated() {
		if (Strings.isEmpty(preTranslation) && Strings.isEmpty(postTranslation))
			return false;

		return true;
	}

	public FieldType getType() {
		return type;
	}
	
	@Transient
	public String getColumnType() {
		// TODO // [boolean, flag, number, string] - SEEMS WAY TO LIMITED - IMPLICITLY CREATE MODEL TYPE - CREATE MODEL TYPE CONVERSION CLASS FE
        // dates, datetimes and functions on dates will all be transmitted back as translated and/or formated strings
        // floats, ints, and currencies will be passed back as translated formatted numbers including relevant decimal and thousand separators
		return type.toString();
	}
	
	@Transient
	public String getFilterType() {
		// TODO // filter type - these need to be fixed [AccountID, AccountName, Autocomplete, Boolean, Date, DaysAgo, Enum, Float, Integer, LowMedHigh, NUmber*, String, UserID]
		return type.toString();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getDatabaseColumnName() {
		return databaseColumnName;
	}

	public void setDatabaseColumnName(String databaseColumnName) {
		this.databaseColumnName = databaseColumnName;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public FieldCategory getCategory() {
		return category;
	}

	public Field setCategory(FieldCategory category) {
		this.category = category;
		return this;
	}

	@Transient
	public String getCategoryTranslation() {
		return categoryTranslation;
	}

	public void setCategoryTranslation(String categoryTranslation) {
		this.categoryTranslation = categoryTranslation;
	}

	public void setPreTranslation(String preTranslation) {
		this.preTranslation = preTranslation;
	}

	public void setPostTranslation(String postTranslation) {
		this.postTranslation = postTranslation;
	}

	public Field requirePermission(OpPerms opPerm) {
		requiredPermission = opPerm;
		return this;
	}

	public boolean canUserSeeQueryField(Permissions permissions) {
		if (requiredPermission == null)
			return true;

		if (requiredPermission.isNone())
			return true;

		return permissions.hasPermission(requiredPermission);
	}

	public void setFieldClass(Class<?> fieldClass) {
		this.fieldClass = fieldClass;
	}

	public Class<?> getFieldClass() {
		return fieldClass;
	}

	public String getPreTranslation() {
		return preTranslation;
	}

	public String getPostTranslation() {
		return postTranslation;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isFilterable() {
		return filterable;
	}

	public void setFilterable(boolean filterable) {
		this.filterable = filterable;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public FieldImportance getImportance() {
		return importance;
	}

	public void setImportance(FieldImportance importance) {
		this.importance = importance;
	}

	public Field clone() {
		Field copiedField = new Field(name, databaseColumnName, type);
		copiedField.category = category;
		copiedField.text = text;
		copiedField.suffix = suffix;
		copiedField.url = url;
		copiedField.width = width;
		copiedField.help = help;
		copiedField.fieldClass = fieldClass;
		copiedField.visible = visible;
		copiedField.filterable = filterable;
		copiedField.sortable = sortable;
		copiedField.preTranslation = preTranslation;
		copiedField.postTranslation = postTranslation;
		copiedField.requiredPermission = requiredPermission;
		copiedField.importance = importance;
		return copiedField;
	}

	@Override
	public String toString() {
		return category + ": " + name;
	}
}