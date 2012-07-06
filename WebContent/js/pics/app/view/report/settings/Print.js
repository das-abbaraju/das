Ext.define('PICS.view.report.settings.Print', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportsettingsprint'],

    border: 0,
    id: 'report_print',
    items: [{
        text: '<i class="icon-print icon-large"></i>Print'
    }],
    title: '<i class="icon-print icon-large"></i>Print',
    modal_title: 'Print Report',
    vertical: true
});