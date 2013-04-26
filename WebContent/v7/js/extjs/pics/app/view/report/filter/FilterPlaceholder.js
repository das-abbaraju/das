Ext.define('PICS.view.report.filter.FilterPlaceholder', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterplaceholder'],

    border: 0,
    id: 'report_filter_options-placeholder',
    items: [{
        xtype: 'button',
        height: 25,
        id: 'report_filter_options_expand',
        text: '<i class="icon-filter"></i>',
        tooltip: PICS.text('Report.execute.filterPlaceholder.tooltipFilter'),
        width: 20
    }],
    margin: '0 10 0 0'
});