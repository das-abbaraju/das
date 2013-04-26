Ext.define('PICS.view.report.settings.ExportSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportexportsetting',

    border: 0,
    id: 'report_export',
    items: [{
        xtype: 'button',
        action: 'export',
        text : '<i class="icon-table icon-large"></i><span>' + PICS.text('Report.execute.exportSetting.buttonExport') + '</span>',
        cls: 'default export',
        id: 'export-button',
        tooltip: PICS.text('Report.execute.exportSetting.tooltipExport'),
        height: 28,
        margin: '100 0 0 0',
        width: 200
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    },
    // custom config
    modal_title: PICS.text('Report.execute.exportSetting.title'),
    title: '<i class="icon-eject icon-large"></i>' + PICS.text('Report.execute.exportSetting.tabName')
});