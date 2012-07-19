Ext.define('PICS.view.report.filter.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportfiltertoolbar'],

    dock: 'top',
    height: 25,
    id: 'report_filter_actions',
    items: [{
        action: 'toggle-filter-formula',
        border: false,
        cls: 'filter-formula',
        height: 23,
        text: 'Filter Formula'
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    }
});