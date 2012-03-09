Ext.define('PICS.view.report.Viewport', {
    extend : 'Ext.container.Viewport',
    requires: [
       'PICS.view.layout.Menu',
       'PICS.view.report.ReportOptions',
       'PICS.view.report.ReportOptionsColumns',
       'PICS.view.report.ColumnSelector',
       'PICS.view.report.ColumnSelectorGrid',
       'PICS.view.report.ReportOptionsFilters',
       'PICS.view.report.DataGrid',
       'PICS.view.layout.Footer'
    ],
    
    items: [{
        region: 'center',
        
        dockedItems: [{
            xtype: 'layoutmenu',
            dock: 'top',
            height: 30
        }],
        items: [{
            xtype: 'reportoptions',
            region: 'west',
            width: 300
        }, {
            xtype: 'tabpanel',
            region: 'center',
            id: 'main',
            items: [{
                xtype: 'reportdatagrid'
            }, {
                title: 'Chart'
            }],
            title: 'Report'
        }],
        layout: 'border'
    }, {
        xtype: 'layoutfooter',
        
        region: 'south'
    }],
    layout : 'border',
    title: 'Main'
});