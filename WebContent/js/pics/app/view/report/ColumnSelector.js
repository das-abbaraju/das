Ext.define('PICS.view.report.ColumnSelector', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportcolumnselector'],
    
    initComponent: function() {
        this.buttons = [{
            action: 'add',
            scope: this,
            text: 'Add'
        },{
            handler: this.close,
            scope: this,
            text: 'Cancel'
        }];

        this.callParent(arguments);
    },
    height: 500,
    items: [{
        xtype: 'reportcolumnselectorgrid'
    }],
    layout: 'fit',
    resizable: false,
    tbar: [{
        xtype: 'searchFilter',
        store: 'report.AvailableFieldsByCategory',
        
        fieldLabel: 'Search',
        fields: [
            'category',
            'text'
        ],
        id: 'filterfield',
        labelWidth: 40
    }],
    title: 'Select Report Columns',
    width: 600
});