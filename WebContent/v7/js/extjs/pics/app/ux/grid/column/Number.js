Ext.define('PICS.ux.grid.column.Number', {
    extend: 'PICS.ux.grid.column.Column',
	
	format: '0,000',
	
    constructor: function () {
        this.callParent(arguments);
    },
    
    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        return this.callParent(arguments);
    }
});