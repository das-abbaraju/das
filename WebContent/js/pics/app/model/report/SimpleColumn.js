Ext.define('PICS.model.report.SimpleColumn', {
	extend: 'Ext.data.Model',

	belongsTo: {
	    model: 'PICS.model.report.AvailableField',
	    foreignKey: 'field',
	    getterName: 'getField',
	    setterName: 'setField'
	},
	fields: [
        { name: 'name', type: 'string' },
        { name: 'method', type: 'string' },
        { name: 'option', type: 'string' },
        { name: 'renderer' }
    ],
    toStoreField: function () {
		var field = this.data.field.toStoreField();
		field.name = this.get("name");
		return field;
    },
    toGridColumn: function () {
    	var gridColumn = this.data.field.toGridColumn();
    	gridColumn.dataIndex = this.get("name");
    	// TODO add in method, option and renderer
    	return gridColumn;
    }
});