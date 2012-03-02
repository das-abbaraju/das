Ext.define('PICS.view.report.ColumnSelectorGrid', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportcolumnselectorgrid'],
    
    store: 'report.AvailableFields',
    
    enableColumnHide: false,
    
    columns: [{
        text: 'Category',
        dataIndex: 'category',
        width: 160
    }, {
        text: 'Column Name',
        dataIndex: 'text',
        flex: 1
    }],
    
    initComponent: function () {
        this.selModel = Ext.create('Ext.selection.CheckboxModel');
        
        this.features = Ext.create('Ext.grid.feature.Grouping',{
            groupHeaderTpl: 'Cuisine: {name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'
        });
        
        this.callParent();
    }
});