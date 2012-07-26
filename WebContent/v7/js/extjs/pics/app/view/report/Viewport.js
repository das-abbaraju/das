Ext.define('PICS.view.report.Viewport', {
    extend: 'Ext.container.Viewport',

    requires: [
        'PICS.view.layout.Header',
        //'PICS.view.report.DataSetGrid',
        'PICS.view.report.report.ReportData',
        'PICS.view.report.filter.FilterOptions',
        'PICS.view.report.Header'
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
        	xtype: 'reportdata',
            region: 'center'
        }],
        layout: 'border'
    }],
    layout: 'border'
});