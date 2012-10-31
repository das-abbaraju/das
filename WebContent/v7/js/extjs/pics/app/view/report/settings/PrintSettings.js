Ext.define('PICS.view.report.settings.PrintSettings', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportsettingsprint'],

    border: 0,
    id: 'report_print',
    /*items: [{
        text: '<i class="icon-print icon-large"></i>Print'
    }],*/
    items: [{
        xtype: 'button',
        action: 'print',
        text : 'Print',
        cls: 'primary print',
        id: 'print-button',
        tooltip: 'Print this report',
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