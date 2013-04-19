Ext.define('PICS.view.report.settings.ExportSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportexportsetting',

    border: 0,
    id: 'report_export',
    items: [{
        xtype: 'button',
        action: 'export',
        text : '<i class="icon-table icon-large"></i><span>Spreadsheet</span>',
        cls: 'default export',
        id: 'export-button',
        tooltip: 'Export this report to Excel',
        height: 28,
        margin: '100 0 0 0',
        width: 200
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    },
    // custom config
    modal_title: 'Export Report',
    title: '<i class="icon-eject icon-large"></i>Export'
});