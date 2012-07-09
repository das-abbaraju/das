Ext.define('PICS.view.report.Viewport', {
    extend: 'Ext.container.Viewport',

    requires: [
        'PICS.view.layout.Header',
        'PICS.view.report.DataSetGrid',
        'PICS.view.report.FilterOptions',
        'PICS.view.report.Header'
    ],

    items: [{
    	xtype: 'layoutheader',
        region: 'north'
    }, {
        region: 'center',

        border: false,
        id: 'content',
        items: [{
        	xtype: 'reportheader',
            region: 'north'
        }, {
        	xtype: 'filteroptions',
            region: 'west'
        }, {
        	xtype: 'reportdatasetgrid',
            region: 'center'
        }],
        layout: 'border'
    }],
    layout: 'border'
});