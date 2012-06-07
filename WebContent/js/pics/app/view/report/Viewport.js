Ext.define('PICS.view.report.Viewport', {
    extend: 'Ext.container.Viewport',

    requires: [
        'PICS.view.layout.Footer',
        'PICS.view.layout.Header',
        'PICS.view.report.DataSetGrid',
        'PICS.view.report.FilterOptions',
        'PICS.view.report.Header'
    ],

    layout: 'border',
    listeners: {
        render: function () {
             Ext.get('loadingPage').dom.hidden = true;
        }
    },
    items: [{
        region: 'north',
        xtype: 'layoutheader'
    }, {
        region: 'center',

        border: 0,
        id: 'content',
        items: [{
            region: 'north',
            xtype: 'reportheader'
        }, {
            region: 'west',
            xtype: 'filteroptions'
        }, {
            region: 'center',
            xtype: 'reportdatasetgrid'
        }],
        layout: 'border'
    }, {
        region: 'south',
        xtype: 'layoutfooter',

        id: 'footer'
    }]
});