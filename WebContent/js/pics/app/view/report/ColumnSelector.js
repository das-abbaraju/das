Ext.define('PICS.view.report.ColumnSelector', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportcolumnselector'],
    
    layout: 'fit',
    
    title: 'Select Report Columns',
    
    height: 500,
    width: 600,

    fbar: [{
        xtype: 'tbfill'
    }, {
        text: 'Save'
    }, {
        text: 'Cancel'
    }],
    
    items: [{
        xtype: 'reportcolumnselectorgrid'
    }],
    tbar: [{
            xtype: 'searchFilter',
            fieldLabel: 'Search',
            labelWidth: 40,
            id: 'filterfield',
            store: 'report.AvailableFields',
            fields: ['category','name','text','help','filterType']        
    }]
});