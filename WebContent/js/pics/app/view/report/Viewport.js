Ext.define('PICS.view.report.Viewport', {
    extend : 'Ext.container.Viewport',
    requires: [
       'PICS.view.layout.Menu',
       'PICS.view.report.ReportOptions',
       'PICS.view.report.ReportOptionsColumns',
       'PICS.view.report.ColumnSelector',
       'PICS.view.report.ColumnSelectorGrid',
       'PICS.view.form.SearchFilter',
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
        id: 'main',
        items: [{
            xtype: 'reportoptions',
            region: 'west',
            
            id: 'aside',
            width: 300
        }, {
            xtype: 'tabpanel',
            region: 'center',
            
            items: [{
                xtype: 'reportdatagrid'
            }, {
                title: 'Chart'
            }],
            title: 'Recently Added Contractors'
        }],
        layout: 'border'
    }, {
        xtype: 'layoutfooter',
        
        region: 'south'
    }],
    layout : 'border',
    title: 'Main'
});