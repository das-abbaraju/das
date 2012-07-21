Ext.define('PICS.view.report.filter.Options', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilteroptions'],

    requires: [
        'PICS.view.report.filter.Header',
        'PICS.view.report.filter.Placeholder',
        'PICS.view.report.filter.Toolbar'
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