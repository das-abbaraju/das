Ext.define('PICS.view.report.Viewport', {
    extend : 'Ext.container.Viewport',
    
    layout : 'border',
    
    requires: [
       'PICS.view.layout.Menu',
       'PICS.view.report.ReportOptions',
       'PICS.view.report.ReportOptionsColumns',
       'PICS.view.report.ColumnSelector',
       'PICS.view.report.ColumnSelectorGrid',
       'PICS.view.report.DataGrid',
       'PICS.view.layout.Footer'
    ],
    
    title: 'Main',
    
    items: [{
        region: 'center',
        layout: 'border',
        
        id: 'main',
        
        dockedItems: [{
            xtype: 'layoutmenu',
            
            dock: 'top',
            height: 30
        }],
        
        items: [{
            xtype: 'reportoptions',
            region: 'west',
            id: 'aside',
            width: 300
        }, {
            xtype: 'tabpanel',
            
            region: 'center',
            
            title: 'Recently Added Contractors',
            
            items: [{
                xtype: 'reportdatagrid'
            }, {
                title: 'Chart'
            }]
        }]
    }, {
        xtype: 'layoutfooter',
        region: 'south'
    }]
});