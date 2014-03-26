package com.picsauditing.report.fields;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Transient;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dr.domain.fields.DisplayType;
import com.picsauditing.dr.domain.fields.FilterType;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.util.Strings;

public class Field {

	private static final Pattern FIELD_VARIABLE_PATTERN = Pattern.compile("\\{(\\w+)\\}");

	private FieldType type = FieldType.String;
	private String name;
	private String categoryTranslation;
	private String text;
	private String url;
	private int width = Column.DEFAULT_WIDTH;
	private String help;

    private String requiredJoin;
	private Class<?> fieldClass;
	private String databaseColumnName;
	private boolean visible = true;
	private boolean filterable = true;
	private boolean sortable = true;
	private String preTranslation;
	private String postTranslation;
	private String separator;
	private String drillDownField;
	private OpPerms requiredPermission = OpPerms.None;
	private FieldImportance importance = FieldImportance.Low;

    private String prefixValue;
    private String suffixValue;

	public Field(ReportField annotation) {
		type = annotation.type();
		width = annotation.width();

		if (Strings.isEmpty(annotation.url())) {
			url = null;
		} else {
			url = annotation.url();
		}

		requiredPermission = annotation.requiredPermissions();
		visible = annotation.visible();
		filterable = annotation.filterable();
		sortable = annotation.sortable();
		importance = annotation.importance();

		preTranslation = annotation.i18nKeyPrefix();
		postTranslation = annotation.i18nKeySuffix();
		if (type.getFilterType() == FilterType.Multiselect && Strings.isEmpty(preTranslation)) {
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

		if (!Strings.isEmpty(preTranslation)) {
			key = preTranslation + "." + key;
		}

		if (!Strings.isEmpty(postTranslation)) {
			key = key + "." + postTranslation;
		}

		return key;
	}

	public Set<String> getDependentFields() {
		Set<String> dependent = new HashSet<String>();
		if (Strings.isNotEmpty(url)) {
			Matcher urlFieldMatcher = FIELD_VARIABLE_PATTERN.matcher(url);

			while (urlFieldMatcher.find()) {
				dependent.add(urlFieldMatcher.group(1));
			}
		}
        if (drillDownField != null)
            dependent.add(drillDownField);

		return dependent;
	}

	public boolean isTranslated() {
		if (Strings.isEmpty(preTranslation) && Strings.isEmpty(postTranslation)) {
			return false;
		}

		return true;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	@Transient
	public DisplayType getDisplayType() {
		return type.getDisplayType();
	}

	@Transient
	public FilterType getFilterType() {
		return type.getFilterType();
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

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

	public Field requirePermission(OpPerms opPerm) {
		requiredPermission = opPerm;
		return this;
	}

	public boolean canUserSeeQueryField(Permissions permissions) {
		if (requiredPermission == null) {
			return true;
		}

		if (requiredPermission.isNone()) {
			return true;
		}

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

	// TODO: This is very questionable
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

    public String getPrefixValue() {
        return prefixValue;
    }

    public void setPrefixValue(String prefixValue) {
        this.prefixValue = prefixValue;
    }

    public String getSuffixValue() {
        return suffixValue;
    }

    public void setSuffixValue(String suffixValue) {
        this.suffixValue = suffixValue;
    }

    public String getDrillDownField() {
        return drillDownField;
    }

    public void setDrillDownField(String drillDownField) {
        this.drillDownField = drillDownField;
    }

    public Field clone() {
		Field copiedField = new Field(name, databaseColumnName, type);
		copiedField.text = text;
		copiedField.url = url;
		copiedField.width = width;
		copiedField.help = help;
		copiedField.fieldClass = fieldClass;
		copiedField.visible = visible;
		copiedField.filterable = filterable;
		copiedField.sortable = sortable;
        copiedField.requiredJoin = requiredJoin;
		copiedField.preTranslation = preTranslation;
		copiedField.postTranslation = postTranslation;
        copiedField.separator = separator;
        copiedField.drillDownField = drillDownField;
		copiedField.requiredPermission = requiredPermission;
		copiedField.importance = importance;
        copiedField.prefixValue = prefixValue;
        copiedField.suffixValue = suffixValue;

		return copiedField;
	}

	@Override
	public String toString() {
		return categoryTranslation + ": " + name;
	}

    public String getRequiredJoin() {
        return requiredJoin;
    }

    public void setRequiredJoin(String requiredJoin) {
        this.requiredJoin = requiredJoin;
    }
}