Ext.define('PICS.view.report.Header', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheader'],

    requires: [
        'PICS.view.report.header.Summary',
        'PICS.view.report.header.Actions'
    ],

    border: false,
    height: 90,
    id: 'report_header',
    items: [{
        xtype: 'reportheadersummary',
        region: 'center'
    }, {
        xtype: 'reportheaderactions',
        region: 'east'
    }],
    layout: 'border'
});