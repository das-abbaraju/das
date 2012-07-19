Ext.define('PICS.view.report.filter.Placeholder', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterplaceholder'],

    border: 0,
    id: 'report_filter_options-placeholder',
    items: [{
        xtype: 'button',
        height: 25,
        id: 'report_filter_options_expand',
        text: '<i class="icon-filter"></i>',
        width: 20
    }],
    margin: '0 10 0 0'
});