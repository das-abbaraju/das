Ext.define('PICS.view.report.ReportHeader', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheader'],

    requires: [
        'PICS.view.report.header.ReportSummary',
        'PICS.view.report.header.ReportActions'
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