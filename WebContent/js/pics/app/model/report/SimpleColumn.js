Ext.define('PICS.model.report.SimpleColumn', {
	extend: 'Ext.data.Model',

	belongsTo: {
	    model: 'PICS.model.report.AvailableField',
	    foreignKey: 'field',
	    getterName: 'getAvailableField',
	    setterName: 'setAvailableField'
	},
	fields: [
        { name: 'name', type: 'string' },
        { name: 'method', type: 'string' },
        { name: 'option', type: 'string' },
        { name: 'renderer' }
    ],
    
    // TODO: refactor these relationships
    // so confusing - this is used to create a report row model on the fly in the report controller
    convertRecordToReportRowField: function () {
        var available_field = this.data.field;
        var field = {};
        
        field.name = available_field.get('name');
        
        if (available_field.get('type')) {
            field.type = available_field.get('type');
            
            if (field.type == 'date') {
                field.dateFormat = 'time';
            }
        }
        
        return field;
    },
    
    // TODO: not sure if this is used anymore
    toStoreField: function () {
		var field = this.data.field.toStoreField();
		
		field.name = this.get('name');
		
		return field;
    },
    
    toGridColumn: function () {
    	var gridColumn = this.data.field.toGridColumn();
    	
    	gridColumn.dataIndex = this.get('name');
    	// TODO add in method, option
    	
    	return gridColumn;
    }
});