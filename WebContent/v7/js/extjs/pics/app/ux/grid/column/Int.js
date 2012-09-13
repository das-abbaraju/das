Ext.define('PICS.ux.grid.column.Int', {
	extend: 'Ext.grid.column.Number',
	
	align: 'right',
	format: '0',
	
	constructor: function () {
		this.callParent(arguments);

        if (!this.record) {
            Ext.Error.raise('Invalid column record');
        }
        
        var field = this.record.getAvailableField();

        if (!field) {
            Ext.Error.raise('Invalid available field');
        }
        
        var name = field.get('name'),
	    	text = field.get('text'),
	    	width = field.get('width');
        
        this.dataIndex = name;
        this.text = text;
        this.width = width;
	}
});