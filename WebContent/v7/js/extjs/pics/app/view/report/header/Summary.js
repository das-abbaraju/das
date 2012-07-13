Ext.define('PICS.view.report.header.Summary', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheadersummary'],

    border: false,
    height: 90,
    html: new Ext.Template([
        '<h1 class="name"></h1>',
        '<h2 class="description"></h2>'
    ]),
    id: 'report_summary'
});