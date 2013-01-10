Ext.define('PICS.ux.grid.column.Float', {
    extend: 'PICS.ux.grid.column.Column',
    
    align: 'right',
    format: '0,000.00',
    
    constructor: function () {
        this.callParent(arguments);
    },
    
    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        return this.callParent(arguments);
    }
});