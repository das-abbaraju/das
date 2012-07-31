Ext.define('PICS.view.report.filter.FilterOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilteroptions'],

    requires: [
        'PICS.view.report.filter.FilterHeader',
        'PICS.view.report.filter.FilterPlaceholder',
        'PICS.view.report.filter.FilterToolbar'
    ],

    autoScroll: true,
    bodyBorder: false,
    border: 0,
    collapsed: false,
    collapsible: true,
    dockedItems: [{
        xtype: 'reportfiltertoolbar',
        dock: 'top'
    }, {
        bodyBorder: false,
        border: 0,
        dock: 'bottom',
        height: 10,
        id: 'report_filter_options_footer'
    }],
    floatable: false,
    header: {
        xtype: 'reportfilterheader'
    },
    id: 'report_filter_options',
    margin: '0 20 0 0',
    placeholder: {
        xtype: 'reportfilterplaceholder'
    },
    title: 'Filter',
    width: 320
});