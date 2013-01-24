Ext.define('PICS.view.report.settings.PrintSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportprintsetting',

    border: 0,
    id: 'report_print',
    items: [{
        xtype: 'button',
        action: 'print-preview',
        text : 'Print Preview',
        cls: 'primary print',
        id: 'print-button',
        tooltip: 'Preview a printable version of this report',
        margin: '100 0 0 0'
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    },
    title: '<i class="icon-print icon-large"></i>Print',
    // custom config
    modal_title: 'Print Report'
});