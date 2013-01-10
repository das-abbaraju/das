Ext.define('PICS.ux.grid.column.Int', {
    extend: 'PICS.ux.grid.column.Column',

    align: 'right',
    format: '0',
    
    constructor: function () {
        this.callParent(arguments);
    },
    
    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        return this.callParent(arguments);
    }
});