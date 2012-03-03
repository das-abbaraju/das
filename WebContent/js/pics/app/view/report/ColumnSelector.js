Ext.define('PICS.view.report.ColumnSelector', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportcolumnselector'],
    
    layout: 'fit',
    
    title: 'Select Report Columns',
    
    height: 500,
    width: 600,

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        items: [{
            xtype: 'tbfill'
        }, {
            xtype: 'button',
            text: 'Save'
        }, {
            text: 'Cancel'
        }]
    }, {
        xtype: 'searchFilter',
        dock: 'top',
        fieldLabel: 'Search',
        labelWidth: 40,
        id: 'filterfield',
        store: 'report.AvailableFieldsByCategory',
        fields: [
            'category', 
            'name' 
        ]
    }],
    
    items: [{
        xtype: 'reportcolumnselectorgrid'
    }]
});