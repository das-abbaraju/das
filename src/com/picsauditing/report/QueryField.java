package com.picsauditing.report;

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
	public int width = 0;
	public boolean sortable = true;
	public boolean hideable = true;
	public boolean hidden = false;
	public int flex = 0;
	public JavaScript renderer;
	public JavaScript editor;
	public String label;

	// xtype : 'actioncolumn',
	/*
	 * items : [ { icon : 'images/edit_pencil.png', tooltip : 'Edit', handler : function(grid, rowIndex, colIndex) { var
	 * record = grid.getStore().getAt(rowIndex); alert("Edit " + record.data.accountID); } } ]
	 * OR
					renderer : function(value, metaData, record) {
						return Ext.String
								.format(
										'<a href="ContractorEdit.action?id={0}">Edit</a>',
										record.data.accountID);
	 */

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
		if (renderer != null)
			json.put("renderer", renderer);
		if (renderer != null)
			json.put("editor", editor);
		return json.toJSONString();
	}

	public QueryField hide() {
		this.hidden = true;
		return this;
	}
}
