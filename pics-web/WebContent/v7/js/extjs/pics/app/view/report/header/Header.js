Ext.define('PICS.view.report.header.Header', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportheader',

    requires: [
        'PICS.view.report.header.PageHeader',
        'PICS.view.report.header.Actions'
    ],

    border: 0,
    height: 90,
    id: 'report_header',
    items: [{
        xtype: 'reportpageheader',
        region: 'center'
    }, {
        xtype: 'reportactions',
        region: 'east'
    }],
    layout: 'border'
});