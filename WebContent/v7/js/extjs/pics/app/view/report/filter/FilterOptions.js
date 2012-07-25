Ext.define('PICS.view.report.filter.FilterOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilteroptions'],

    requires: [
        'PICS.view.report.filter.FilterHeader',
        'PICS.view.report.filter.FilterPlaceholder',
        'PICS.view.report.filter.FilterToolbar'
    ],

    autoScroll: true,
    collapsed: false,
    collapsible: true,
    dockedItems: [{
        xtype: 'reportfiltertoolbar'
    }],
    floatable: false,
    header: {
        xtype: 'reportfilterheader'
    },
    id: 'report_filter_options',
    margin: '0 20 20 0',
    placeholder: {
        xtype: 'reportfilterplaceholder'
    },
    title: 'Filter',
    width: 320
});