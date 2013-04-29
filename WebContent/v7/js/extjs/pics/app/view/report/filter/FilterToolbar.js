Ext.define('PICS.view.report.filter.FilterToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportfiltertoolbar'],

    border: 0,
    height: 25,
    id: 'report_filter_toolbar',
    items: [{
        action: 'show-filter-formula',
        border: 0,
        cls: 'show-filter-formula',
        height: 23,
        text: PICS.text('Report.execute.filterToolbar.filterFormulaTitle')
    }],
    layout: {
        type: 'vbox',
        align: 'center'
    }
});