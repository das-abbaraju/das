Ext.define('PICS.view.report.header.ReportHeader', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheader'],

    requires: [
        'PICS.view.report.header.ReportSummary',
        'PICS.view.report.header.ReportActions'
    ],

    border: 0,
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