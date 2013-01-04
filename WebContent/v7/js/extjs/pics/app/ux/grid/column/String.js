Ext.define('PICS.ux.grid.column.String', {
    extend: 'PICS.ux.grid.column.Column',
    
    constructor: function () {
        this.callParent(arguments);
    },
    
    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        return this.callParent(arguments);
    }
});