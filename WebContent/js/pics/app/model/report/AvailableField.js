Ext.define('PICS.model.report.AvailableField', {
	extend: 'Ext.data.Model',

	fields: [
	    { name: 'name', type: 'string' },
        { name: 'category', type: 'string' },
        { name: 'text', type: 'string' },
        { name: 'help', type: 'string' },
        { name: 'width', type: 'int', defaultValue: 0 },
        { name: 'type', type: 'string' },
        { name: 'dateFormat', type: 'string' },
        { name: 'filterType', type: 'string' }
    ],
    createSimpleColumn: function () {
        var column = Ext.create('PICS.model.report.SimpleColumn', {
        	'name': this.get("name"),
        	'type': this.get("type")
        });
        column.setField(this);
        return column;
    },
	// See http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Field
    toStoreField: function () {
    	// TODO I've only tested a couple of the config options here
    	var field = {};
    	field.name = this.get("name");
    	if (this.get("type")) {
    		field.type = this.get("type");
    		if (field.type == "date")
    			field.dateFormat = this.get("dateFormat");
    	}
    	return field;
    },
	// See http://docs.sencha.com/ext-js/4-0/#!/api/Ext.grid.column.Column
    toGridColumn: function () {
    	// TODO I've only tested a couple of the config options here
    	var gridColumn = {};
    	gridColumn.dataIndex = this.get("name");
    	gridColumn.text = this.get("text");
    	if (this.get("width") > 0)
    		gridColumn.width = this.get("width");

    	if (this.get("type")) {
    		gridColumn.type = this.get("type");
    		if (gridColumn.type == "date")
    			gridColumn.dateFormat = this.get("dateFormat");
    	}
    	return gridColumn;
    }
});