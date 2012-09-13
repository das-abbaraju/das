Ext.define('PICS.ux.grid.column.Boolean', {
	extend: 'Ext.grid.column.Action',
	
	align: 'center',
	menuDisabled: true,
    sortable: false,
	
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
	    	width = field.get('width') ? field.get('width') : 50;
        
        this.dataIndex = name;
        this.text = text;
        this.width = width;
	},
	
	renderer: function (value) {
		return value ? '<i class="icon-ok"></i>' : '';
	}
});