Ext.define('PICS.view.report.settings.PrintSetting', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportprintsetting',

    border: 0,
    id: 'report_print',
    items: [{
        xtype: 'button',
        action: 'print-preview',
        text : '<i class="icon-picture icon-large"></i><span>' + PICS.text('Report.execute.printSetting.buttonPreview') + '</span>',
        cls: 'default print',
        id: 'print-button',
        tooltip: PICS.text('Report.execute.printSetting.tooltipPreview'),
        height: 28,
        margin: '100 0 0 0',
        width: 200
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    },
    // custom config
    modal_title: PICS.text('Report.execute.printSetting.title'),
    title: '<i class="icon-print icon-large"></i>' + PICS.text('Report.execute.printSetting.tabName')
});