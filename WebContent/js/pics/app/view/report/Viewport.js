Ext.define('PICS.view.report.Viewport', {
    extend: 'Ext.container.Viewport',

    requires: [
        'PICS.view.layout.Footer',
        'PICS.view.layout.Header',
        'PICS.view.layout.Menu',
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
        id: 'layoutNorth',
        items: [{
            xtype: 'layoutheader'
        }, {
            xtype: 'layoutmenu'
        }]
    }, {
        region: 'center',
        id: 'layoutCenter',
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
        xtype: 'layoutfooter'
    }]
});