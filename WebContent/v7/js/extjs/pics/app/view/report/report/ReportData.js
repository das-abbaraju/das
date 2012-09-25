Ext.define('PICS.view.report.report.ReportData', {
    extend: 'Ext.grid.Panel',
    alias: ['widget.reportdata'],

    requires: [
        'PICS.view.report.LinkColumn',
        'PICS.view.report.report.ReportPagingToolbar'
    ],

    store: 'report.ReportDatas',

    border: 0,
    // column configuration must be specified - will be overridden dynamically
    columns: [{
        xtype: 'rownumberer'
    }],
    dockedItems: [{
        xtype: 'reportpagingtoolbar',
        dock: 'top'
    }],
    id: 'report_data',
    listeners: {
        reconfigure: function (cmp) {
            cmp.columns[0].setHeight(23);
        }
    },
    margin: '0 30 0 0',
    rowLines: false,

    initComponent: function () {
        this.callParent(arguments);

        this.headerCt.on('headerclick', function (header, column, event, html) {
            if (column.xtype == 'rownumberer') {
                return false;
            };

            header.showMenuBy(column.el.dom, column);
        }, this);

        this.headerCt.on('menucreate', function (header, column, event, html) {
            var menu = header.getMenu();

            this.createHeaderMenu(menu);
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
            xtype: 'menuseparator'
        }, {
            name: 'function',
            text: 'Functions...'
        }, {
            xtype: 'menuseparator'
        }, {
            name: 'remove_column',
            text: 'Remove'
        });
    }
});