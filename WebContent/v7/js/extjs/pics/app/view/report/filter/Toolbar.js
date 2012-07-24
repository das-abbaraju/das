Ext.define('PICS.view.report.filter.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportfiltertoolbar'],

    dock: 'top',
    height: 25,
    id: 'report_filter_toolbar',
    items: [{
        action: 'show-filter-formula',
        border: 0,
        cls: 'show-filter-formula',
        height: 23,
        text: 'Filter Formula'
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    }
});