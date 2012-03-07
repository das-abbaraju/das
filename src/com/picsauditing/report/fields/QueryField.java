package com.picsauditing.report.fields;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.util.Strings;

/**
 * This is based largely on the Grid Column API from Sencha
 * http://docs.sencha.com/ext-js/4-0/#!/api/Ext.grid.column.Column
 */
public class QueryField implements JSONAware {
	/**
	 * aka field or alias
	 */
	private String name;
	private String sql;
	private FilterType filterType = FilterType.String;
	private int width = 0;
	private boolean visible = true;
	private boolean filterable = true;
	private boolean sortable = true;
	private boolean suggested = false;
	private boolean hidden = false;
	private int flex = 0;
	private ExtFieldType type = ExtFieldType.Auto;
	private JavaScript renderer;
	// private JavaScript editor;
	private String preTranslation;
	private String postTranslation;
	private FieldCategory category = FieldCategory.General;
	private Set<OpPerms> requiredPermissions = new HashSet<OpPerms>();

	// xtype : 'actioncolumn',
	/*
	 * items : [ { icon : 'images/edit_pencil.png', tooltip : 'Edit', handler : function(grid, rowIndex, colIndex) { var
	 * record = grid.getStore().getAt(rowIndex); alert("Edit " + record.data.accountID); } } ] OR renderer :
	 * function(value, metaData, record) { return Ext.String .format( '<a href="ContractorEdit.action?id={0}">Edit</a>',
	 * record.data.accountID);
	 */

	public QueryField(String name, String sql, FilterType filterType) {
		this(name, sql, filterType, false);
	}

	public QueryField(String name, String sql, FilterType filterType, boolean isDefault) {
		this.name = name;
		this.sql = sql;
		this.filterType = filterType;
		this.suggested = isDefault;
		
		if (filterType != null) {
			this.type = this.filterType.getFieldType();
		}
		
		if (StringUtils.endsWithIgnoreCase(name, "id"))
			hidden = true;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		// TODO Move this to SimpleColumn.js toGridColumn
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("text", name);
		
		if (width > 0)
			json.put("width", width);
		if (!visible)
			json.put("visible", visible);
		if (!filterable)
			json.put("filterable", filterable);
		if (!sortable)
			json.put("sortable", sortable);
		if (suggested)
			json.put("suggested", suggested);
		if (hidden)
			json.put("hidden", hidden);

		if (flex > 0)
			json.put("flex", flex);
		if (renderer != null && renderer.toJSONString().length() > 0)
			json.put("renderer", renderer);

		json.put("filterType", filterType.toString());

		if (!filterType.equals(ExtFieldType.Auto)) {
			json.put("type", type.toString().toLowerCase());
			if (type == ExtFieldType.Date) {
				json.put("xtype", "datecolumn");
				json.put("dateFormat", "time");
			}
		}
		return json;
	}

	public String toJSONString() {
		return toJSONObject().toJSONString();
	}

	public QueryField translate(String prefix, String suffix) {
		this.preTranslation = prefix;
		this.postTranslation = suffix;
		return this;
	}

	public QueryField addRenderer(Renderer renderer) {
		this.renderer = renderer;
		return this;
	}

	public QueryField addRenderer(String action, String[] parameters) {
		this.width = 200;
		this.renderer = new Renderer(action, parameters);
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

	public boolean isTranslated() {
		if (preTranslation == null && postTranslation == null)
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isSuggested() {
		return suggested;
	}

	public void setSuggested(boolean suggested) {
		this.suggested = suggested;
	}

	public QueryField setSuggested() {
		setSuggested(true);
		return this;
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

	public void setType(ExtFieldType type) {
		this.type = type;
	}

	public void setRenderer(JavaScript renderer) {
		this.renderer = renderer;
	}

	public FieldCategory getCategory() {
		return category;
	}

	public QueryField setCategory(FieldCategory category) {
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
	
	public QueryField requirePermission(OpPerms opPerm) {
		this.requiredPermissions.add(opPerm);
		return this;
	}

	public QueryField setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public QueryField setFilterable(boolean filterable) {
		this.filterable = filterable;
		return this;
	}
	
	
}
