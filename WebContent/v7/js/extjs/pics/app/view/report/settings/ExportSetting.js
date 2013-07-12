Ext.define('PICS.view.report.settings.ExportSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportexportsetting',

    border: 0,
    id: 'report_export',
    items: [{
        xtype: 'button',
        action: 'print-preview',
        text : '<i class="icon-picture icon-large"></i><span>' + PICS.text('Report.execute.exportSetting.buttonPrint') + '</span>',
        cls: 'default print',
        id: 'print-button',
        tooltip: PICS.text('Report.execute.exportSetting.tooltipPreview'),
        height: 28,
        margin: '47 0 0 0',
        width: 200
    }, {
        xtype: 'button',
        action: 'export',
        text : '<i class="icon-table icon-large"></i><span>' + PICS.text('Report.execute.exportSetting.buttonSpreadsheet') + '</span>',
        cls: 'default export',
        id: 'export-button',
        tooltip: PICS.text('Report.execute.exportSetting.tooltipExport'),
        height: 28,
        margin: '10 0 0 0',
        width: 200
    }, {
        xtype: 'component',
        id: 'message',
        tpl: new Ext.XTemplate([
            '<p class="export-message">',
                'All {record_count} rows will be exported.',
            '</p>'
        ]),
        margin: '20 0 0 0'
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    },
    // custom config
    modal_title: PICS.text('Report.execute.exportSetting.title'),
    title: '<i class="icon-download icon-large"></i>' + PICS.text('Report.execute.exportSetting.tabName'),

    update: function (values) {
        this.items.get('message').update(values);
    }
});