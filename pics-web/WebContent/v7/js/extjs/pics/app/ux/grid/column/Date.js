Ext.define('PICS.ux.grid.column.Date', {
    extend: 'PICS.ux.grid.column.Column',
    
    format: 'Y-m-d',
    
    constructor: function () {
        this.callParent(arguments);
    },
    
    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        var grid = view.ownerCt,
            grid_column = grid.columns[colIndex];
        
        value = Ext.Date.format(value, grid_column.format);
        
        return this.callParent(arguments);
    }
});