package com.picsauditing.report.fields;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.annotations.ReportField;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.util.Strings;

/**
 * This is based largely on the Grid Column API from Sencha
 * http://docs.sencha.com/ext-js/4-0/#!/api/Ext.grid.column.Column
 */
public class Field implements JSONAware {

	private Class<?> fieldClass;
	private String name;
	private String text;
	private String databaseColumnName;
	private FilterType filterType = FilterType.String;
	private AutocompleteType autocompleteType = AutocompleteType.None;
	private int width = 200;
	private boolean visible = true;
	private boolean filterable = true;
	private boolean sortable = true;
	private boolean hidden = false;
	private int flex = 0;
	private ExtFieldType type = ExtFieldType.Auto;
	private String url;
	private JavaScript renderer;
	private String preTranslation;
	private String postTranslation;
	private FieldCategory category = FieldCategory.General;
	private Set<OpPerms> requiredPermissions = new HashSet<OpPerms>();

	/**
	 * Currently autocomplete is only supported via entity annotations
	 */
	public Field(ReportField annotation) {
		filterType = annotation.filterType();
		autocompleteType = annotation.autocomplete();
		width = annotation.width();
		url = annotation.url();
		preTranslation = annotation.i18nKeyPrefix();
		postTranslation = annotation.i18nKeySuffix();
		visible = annotation.visible();
		filterable = annotation.filterable();
		sortable = annotation.sortable();
		category = annotation.category();
		requiredPermissions = new HashSet<OpPerms>(Arrays.asList(annotation.requiredPermissions()));
	}

	public Field(String name, String databaseColumnName, FilterType filterType) {
		this(name, databaseColumnName, filterType, false);
	}

	public Field(String name, String databaseColumnName, FilterType filterType, boolean isDefault) {
		this.name = name;
		this.databaseColumnName = databaseColumnName;
		this.filterType = filterType;

		if (filterType != null) {
			this.type = this.filterType.getFieldType();
		}

		if (StringUtils.endsWithIgnoreCase(name, "id")) {
			hidden = true;
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		// TODO Move this to SimpleColumn.js toGridColumn
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("text", text);

		if (width > 0)
			json.put("width", width);
		if (!visible)
			json.put("visible", visible);
		if (!filterable)
			json.put("filterable", filterable);
		if (!sortable)
			json.put("sortable", sortable);
		if (hidden)
			json.put("hidden", hidden);

		if (flex > 0)
			json.put("flex", flex);

		if (!Strings.isEmpty(url))
			json.put("url", url);

		if (renderer != null && renderer.toJSONString().length() > 0)
			json.put("renderer", renderer);

		if (filterType != null) {
			json.put("filterType", filterType.toString());

			if (!filterType.equals(ExtFieldType.Auto)) {
				json.put("type", type.toString().toLowerCase());
			}
		}

		return json;
	}

	public String toJSONString() {
		return toJSONObject().toJSONString();
	}

	@SuppressWarnings("unchecked")
	public JSONObject renderEnumFieldAsJson(Locale locale) {
		JSONArray jsonArray = new JSONArray();
		JSONObject json = new JSONObject();

		for (Object enumValue : fieldClass.getEnumConstants()) {
			JSONObject enumAsJson = new JSONObject();
			enumAsJson.put("id", enumValue.toString());

			String translationKey = fieldClass.getSimpleName().toString() + "." + enumValue.toString();
			String translatedString = ReportUtil.getText(translationKey, locale);

			if (translatedString == null) {
				translatedString = enumValue.toString();
			}

			enumAsJson.put("name", translatedString);
			jsonArray.add(enumAsJson);
		}

		json.put("result", jsonArray);

		return json;
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
			Pattern fieldVariablePattern = Pattern.compile("\\{(\\w+)\\}");
			Matcher urlFieldMatcher = fieldVariablePattern.matcher(url);

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

	public boolean canUserSeeQueryField(Permissions permissions) {
		if (CollectionUtils.isEmpty(requiredPermissions))
			return true;

		// TODO shouldn't be the opposite: if the user doesn't have a single permssion, it should return false?
		for (OpPerms requiredPermission : requiredPermissions) {
			if (permissions.hasPermission(requiredPermission))
				return true;
		}

		return false;
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

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	public void setAutocompleteType(AutocompleteType autocompleteType) {
		this.autocompleteType = autocompleteType;
	}

	public AutocompleteType getAutocompleteType() {
		return autocompleteType;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setFlex(int flex) {
		this.flex = flex;
	}

	public ExtFieldType getType() {
		return type;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setType(ExtFieldType type) {
		this.type = type;
	}

	public void setRenderer(JavaScript renderer) {
		this.renderer = renderer;
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

	public void setPreTranslation(String preTranslation) {
		this.preTranslation = preTranslation;
	}

	public void setPostTranslation(String postTranslation) {
		this.postTranslation = postTranslation;
	}

	public Set<OpPerms> getRequiredPermissions() {
		return requiredPermissions;
	}

	public Field requirePermission(OpPerms opPerm) {
		requiredPermissions.add(opPerm);
		return this;
	}

	public Field setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public Field setFilterable(boolean filterable) {
		this.filterable = filterable;
		return this;
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
}