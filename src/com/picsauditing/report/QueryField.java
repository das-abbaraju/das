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
		if (StringUtils.endsWithIgnoreCase(dataIndex, "name") || StringUtils.endsWithIgnoreCase(dataIndex, "website"))
			applyRendering();
	}

	private void applyRendering() {
		width = 200;
		StringBuffer js = new StringBuffer();
		String action = "";
		String parameters = "";
		if (StringUtils.endsWithIgnoreCase(dataIndex, "AccountName")) {
			action = "ContractorView.action?id={0}\">{1}";

			String prefix = StringUtils.removeEndIgnoreCase(dataIndex, "Name");
			parameters = "record.data." + prefix + "ID,record.data." + dataIndex;
		} else if (StringUtils.endsWithIgnoreCase(dataIndex, "OperatorName")) {
			action = "FacilitiesEdit.action?operator={0}\">{1}";

			String prefix = StringUtils.removeEndIgnoreCase(dataIndex, "Name");
			parameters = "record.data." + prefix + "ID,record.data." + dataIndex;
		} else if (StringUtils.endsWithIgnoreCase(dataIndex, "AuditTypeName")) {
			width = 180;
			action = "Audit.action?auditID={0}\">{1} {2}";
			parameters = "record.data.auditID,record.data.auditTypeName,record.data.auditFor";
		} else if (StringUtils.endsWithIgnoreCase(dataIndex, "RequestedName")) {
			action = "RequestNewContractor.action?newContractor={0}\">{1}";
			parameters = "record.data.requestID,record.data.requestedName";
		} else if (StringUtils.endsWithIgnoreCase(dataIndex, "UserName")) {
			action = "UsersManage.action?account={0}&user={1}\">{2}";

			String prefix = StringUtils.removeEndIgnoreCase(dataIndex, "name");
			parameters = "record.data." + prefix + "AccountID," + "record.data." + prefix + "ID," + "record.data."
					+ dataIndex;
		} else if (StringUtils.endsWithIgnoreCase(dataIndex, "Website")) {
			action = "http://{0}\">{0}";
			parameters = "record.data.accountWebsite";
		}
		else
			return;
		js.append("function(value, metaData, record) {return Ext.String.format('<a href=\"");
		js.append(action);
		js.append("</a>',");
		js.append(parameters);
		js.append(");}");
		renderer = new JavaScript(js.toString());

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
		if (renderer != null)
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
}
