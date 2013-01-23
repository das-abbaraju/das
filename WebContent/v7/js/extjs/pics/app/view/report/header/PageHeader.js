Ext.define('PICS.view.report.header.PageHeader', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportpageheader',

    border: false,
    height: 90,
    id: 'report_page_header',
    tpl: '<h1 class="name">{name}</h1><h2 class="description">{description}</h2>',

    update: function (report) {
        if (Ext.getClassName(report) != 'PICS.model.report.Report') {
            Ext.Error.raise('Invalid report record');
        }

        var data = report ? report.data : {};

        this.callParent([data]);
    }
});