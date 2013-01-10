Ext.define('PICS.ux.grid.column.Boolean', {
	extend: 'PICS.ux.grid.column.Column',
	
	align: 'center',
	width: 50,
	
	constructor: function () {
		this.callParent(arguments);
	},
	
	renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
	    value = value ? '<i class="icon-ok"></i>' : '';
        
        return this.callParent(arguments);
    }
});