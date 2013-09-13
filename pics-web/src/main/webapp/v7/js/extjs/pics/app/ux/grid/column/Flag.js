Ext.define('PICS.ux.grid.column.Flag', {
    extend: 'PICS.ux.grid.column.Column',
    
    align: 'center',
    
    constructor: function () {
        this.callParent(arguments);
    },
    
    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
        switch (value) {
            case 'Green':
                value = '<i class="icon-flag green"></i>';
                
                break;
            case 'Red':
                value = '<i class="icon-flag red"></i>';
                
                break;
            case 'Yellow':
            case 'Amber':
                value = '<i class="icon-flag amber"></i>';
                
                break;
            default:
                value = '<i class="icon-flag clear"></i>';
                break;
        }
        
        return this.callParent(arguments);
    }
});