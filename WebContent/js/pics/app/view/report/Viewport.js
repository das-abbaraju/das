Ext.define('PICS.view.report.Viewport', {
    extend: 'Ext.container.Viewport',


    requires: [
        'PICS.view.layout.Footer',               
        'PICS.view.layout.Header',
        'PICS.view.layout.Menu',
        'PICS.view.report.DataGrid',
        'PICS.view.report.FilterOptions',
        'PICS.view.report.Header',
        'PICS.view.report.SortToolbar'
    ],
    defaults: {
        border: false
    },  
    layout: 'border',
    items: [{
            region: 'north',
            items: [{
                xtype: 'layoutheader'
            },{
                xtype: 'layoutmenu'
            }]
        },{
            region: 'center',
            bodyStyle: 'background: #FFF',
            layout: 'border',
            items: [{
                region: 'north',
                xtype: 'reportheader'
            },{
                region: 'west',
                xtype: 'filteroptions'
            },{
                region: 'center',
                xtype: 'reportdatagrid'
            }]
        },{
            region: 'south',
            xtype: 'layoutfooter'
        }]
});