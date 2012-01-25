Ext.define('PICS.view.report.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterpanel'],
    
    layout: 'accordion',
    
    collapsed: true,
    collapsible: true,
    resizable: {
        handles: 'e'
    },
    
    title: 'Report Options',
    
    items: [{
        xtype: 'reportfiltercolumngrid',
        
        title: 'Columns'
    }, {
        title: 'Filters'
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