Ext.define('PICS.view.report.header.ReportSummary', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheadersummary'],

    border: false,
    height: 90,
    id: 'report_summary',
    tpl: '<h1 class="name">{name}</h1><h2 class="description">{description}</h2>',

    update: function (report) {
        if (!report || report.modelName != 'PICS.model.report.Report') {
            throw 'Invalid report';
        }

        var data = report ? report.data : {};

        this.callParent([data]);
    }
});