Ext.define('PICS.view.report.Viewport', {
    extend: 'Ext.container.Viewport',

    requires: [
        'Ext.layout.container.Border',
        'Ext.resizer.Splitter',
        'PICS.view.layout.Header',
        'PICS.view.report.data-table.DataTable',
        'PICS.view.report.filter.FilterOptions',
        'PICS.view.report.header.Header',
        'PICS.view.report.alert.Error'
    ],

    items: [{
    	xtype: 'layoutheader',
        region: 'north'
    }, {
        region: 'center',

        border: 0,
        id: 'content',
        items: [{
        	xtype: 'reportheader',
            region: 'north'
        }, {
            xtype: 'reportfilteroptions',
            region: 'west'
        }, {
        	xtype: 'reportdatatable',
            region: 'center'
        }],
        layout: 'border'
    }],
    layout: 'border'
});