Ext.define('PICS.view.report.settings.PrintSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportprintsetting',

    border: 0,
    id: 'report_print',
    items: [{
        xtype: 'button',
        action: 'print-preview',
        text : '<i class="icon-picture icon-large"></i><span>Preview</span>',
        cls: 'default print',
        id: 'print-button',
        tooltip: 'Preview a printable version of this report',
        height: 28,
        margin: '100 0 0 0',
        width: 200
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    },
    title: '<i class="icon-print icon-large"></i>Print',
    // custom config
    modal_title: 'Print Report'
});