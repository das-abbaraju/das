Ext.define('PICS.view.report.SettingsPrint', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportsettingsprint'],

    border: 0,
    id: 'report_print',
    items: [{
        text: '<i class="icon-print icon-large"></i>Print'
    }],
    title: '<i class="icon-print icon-large"></i>Print',
    vertical: true
});