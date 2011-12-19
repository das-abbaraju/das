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
	public String dataIndex;
	public String sql;
	public String requireJoin;
	public int width = 0;
	public boolean sortable = true;
	public boolean hideable = true;
	public boolean hidden = false;
	public int flex = 0;
	public FieldType type = FieldType.Auto;
	public JavaScript renderer;
	public JavaScript editor;
	public String label;
	private String preTranslation;
	private String postTranslation;

	// xtype : 'actioncolumn',
	/*
	 * items : [ { icon : 'images/edit_pencil.png', tooltip : 'Edit', handler : function(grid, rowIndex, colIndex) { var
	 * record = grid.getStore().getAt(rowIndex); alert("Edit " + record.data.accountID); } } ] OR renderer :
	 * function(value, metaData, record) { return Ext.String .format( '<a href="ContractorEdit.action?id={0}">Edit</a>',
	 * record.data.accountID);
	 */

	public QueryField(String dataIndex, String sql) {
		this.dataIndex = dataIndex;
		this.sql = sql;

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

	public QueryField addRenderer(String action, String[] parameters) {
		this.width = 200;
		this.renderer = new Renderer(action, parameters);
		return this;
	}

	public QueryField requireJoin(String joinAlias) {
		this.requireJoin = joinAlias;
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

}
