Ext.define('PICS.view.report.settings.Export', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportsettingsexport'],

    border: 0,
    id: 'report_export',
    items: [{
        text: '<i class="icon-table icon-large"></i>Spread Sheet'
    }, {
        text: '<i class="icon-file icon-large"></i>PDF'
    }, {
        text: '<i class="icon-home icon-large"></i>To Dashboard'
    }],
    // custom config
    modal_title: 'Export Report',
    title: '<i class="icon-eject icon-large"></i>Export',
    vertical: true
});