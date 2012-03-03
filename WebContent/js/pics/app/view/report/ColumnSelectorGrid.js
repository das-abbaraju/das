Ext.define('PICS.view.report.ColumnSelectorGrid', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportcolumnselectorgrid'],
    store: 'report.AvailableFieldsByCategory',
    
    columns: [{
        dataIndex: 'category',
        text: 'Category',
        width: 160
    }, {
        dataIndex: 'text',
        text: 'Column Name',
        flex: 1
    }],
    enableColumnHide: false,
    
    initComponent: function () {
        this.selModel = Ext.create('Ext.selection.CheckboxModel');
        
        this.features = Ext.create('Ext.grid.feature.Grouping', {
            groupHeaderTpl: 'Category: {name} ({rows.length} Item{[values.rows.length != 1 ? "s" : ""]})'
        });
        
        this.callParent();
    }
});