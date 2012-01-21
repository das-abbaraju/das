package com.picsauditing.report;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.picsauditing.util.Strings;

/**
 * This is based largely on the Grid Column API from Sencha
 * http://docs.sencha.com/ext-js/4-0/#!/api/Ext.grid.column.Column
 */
public class QueryField implements JSONAware {
	/**
	 * aka field or alias
	 */
	private String dataIndex;
	private String sql;
	private String requireJoin;
	private FilterType filterType;
	private int width = 0;
	private boolean sortable = true;
	private boolean hideable = true;
	private boolean suggested = false;
	private boolean hidden = false;
	private int flex = 0;
	private FieldType type = FieldType.Auto;
	private JavaScript renderer;
	private JavaScript editor;
	private String label;
	private String preTranslation;
	private String postTranslation;

	// xtype : 'actioncolumn',
	/*
	 * items : [ { icon : 'images/edit_pencil.png', tooltip : 'Edit', handler : function(grid, rowIndex, colIndex) { var
	 * record = grid.getStore().getAt(rowIndex); alert("Edit " + record.data.accountID); } } ] OR renderer :
	 * function(value, metaData, record) { return Ext.String .format( '<a href="ContractorEdit.action?id={0}">Edit</a>',
	 * record.data.accountID);
	 */

	public QueryField(String dataIndex, String sql, FilterType filterType) {
		this(dataIndex, sql, filterType, null, false);
	}

	public QueryField(String dataIndex, String sql, FilterType filterType, String requireJoin, boolean isDefault) {
		this.dataIndex = dataIndex;
		this.sql = sql;
		this.filterType = filterType;
		this.requireJoin = requireJoin;
		this.suggested = isDefault;

		if (filterType == FilterType.Date) {
			type = FieldType.Date;
		}

		if (StringUtils.endsWithIgnoreCase(dataIndex, "id"))
			hide();
	}

	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject json = new JSONObject();
		json.put("dataIndex", dataIndex);
		if (!Strings.isEmpty(label))
			json.put("text", label);
		else
			json.put("text", dataIndex);

		if (width > 0)
			json.put("width", width);
		if (!hideable)
			json.put("hideable", hideable);
		if (hidden)
			json.put("hidden", hidden);
		if (!sortable)
			json.put("sortable", sortable);

		if (flex > 0)
			json.put("flex", flex);
		if (renderer != null && renderer.toJSONString().length() > 0)
			json.put("renderer", renderer);
		if (renderer != null)
			json.put("editor", editor);

		if (type == FieldType.Date) {
			json.put("xtype", "datecolumn");
		}
		return json.toJSONString();
	}

	public QueryField hide() {
		this.hidden = true;
		return this;
	}

	public QueryField type(FieldType type) {
		this.type = type;
		return this;
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

	public QueryField requireJoin(String joinAlias) {
		this.requireJoin = joinAlias;
		return this;
	}

	public QueryField makeDefault() {
		this.suggested = true;
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

	public boolean requiresJoin() {
		return !Strings.isEmpty(requireJoin);
	}

	public String getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getRequireJoin() {
		return requireJoin;
	}

	public void setRequireJoin(String requireJoin) {
		this.requireJoin = requireJoin;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isHideable() {
		return hideable;
	}

	public void setHideable(boolean hideable) {
		this.hideable = hideable;
	}

	public boolean isSuggested() {
		return suggested;
	}

	public void setSuggested(boolean suggested) {
		this.suggested = suggested;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public int getFlex() {
		return flex;
	}

	public void setFlex(int flex) {
		this.flex = flex;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public JavaScript getRenderer() {
		return renderer;
	}

	public void setRenderer(JavaScript renderer) {
		this.renderer = renderer;
	}

	public JavaScript getEditor() {
		return editor;
	}

	public void setEditor(JavaScript editor) {
		this.editor = editor;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPreTranslation() {
		return preTranslation;
	}

	public void setPreTranslation(String preTranslation) {
		this.preTranslation = preTranslation;
	}

	public String getPostTranslation() {
		return postTranslation;
	}

	public void setPostTranslation(String postTranslation) {
		this.postTranslation = postTranslation;
	}

}
