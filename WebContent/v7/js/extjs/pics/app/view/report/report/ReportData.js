Ext.define('PICS.view.report.ReportData', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportdata'],

    requires: [
        'PICS.view.report.LinkColumn',
        'PICS.view.report.report.ReportPagingToolbar'
    ],

    store: 'report.DataSets',

    border: false,
    // column configuration must be specified - will be overridden dynamically
    columns: [{
        xtype: 'rownumberer'
    }],
    dockedItems: [{
        xtype: 'reportpagingtoolbar',
        dock: 'top'
    }, {
        xtype: 'panel',
        dock: 'bottom',
        height: 10,
        id: 'report_data_footer'
    }],
    id: 'report_data',
    margin: '0 30 20 0',
    rowLines: false,

    initComponent: function () {
        this.callParent(arguments);

        this.headerCt.on('headerclick', function (header, column, event, html) {
            var menu = header.getMenu();
            this.createHeaderMenu(menu);

            header.showMenuBy(column.el.dom, column);
        }, this);
    },

    createHeaderMenu: function (menu) {
        menu.removeAll();

        // simulate header menu to be plain (menu is already created at this point)
        menu.addCls(Ext.baseCSSPrefix + 'menu-plain');
        menu.name = 'report_data_header_menu';

        menu.add({
            name: 'sort_asc',
            text: 'Sort Ascending'
        }, {
            name: 'sort_desc',
            text: 'Sort Descending'
        }, {
            name: 'remove_column',
            text: 'Remove'
        });
    }
});