Ext.define('PICS.view.report.ReportOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptions'],
    
    layout: 'accordion',
    
    collapsed: false,
    collapsible: true,
    resizable: {
        handles: 'e'
    },
    
    title: 'Report Options',
    
    items: [{
        title: 'Filters'
    }, {
        title: 'Columns',
        xtype: 'reportoptionscolumns'
    }, {
        title: 'Sort'
    }, {
        title: 'Share'
    }, {
        title: 'Save'
    }],
    
    initComponent: function () {
        
        this.callParent();
    }
});