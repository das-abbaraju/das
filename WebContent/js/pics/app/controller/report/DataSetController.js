Ext.define('PICS.controller.report.DataSetController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'paging',
        selector: 'reportdatasetgrid pagingtoolbar'
    }, {
        ref: 'rowsPerPage',
        selector: 'pagingtoolbar combo[name=rows_per_page]'
    }],

    stores: [
        'report.Reports',
        'report.DataSets'
    ],

    init: function () {
        var me = this;
        this.control({
            'reportdatasetgrid': {
                beforerender: this.configureColumnMenu
            },
            'menuitem[name=removeColumn]': {
                click: function (menuItem, event, options) {
                    var columnName = menuItem.up('menu').ownerCt.activeHeader.dataIndex;

                    me.removeColumn(columnName);
                }
            },
            'menuitem[name=sortASC]': {
                click: function (menuItem, event, options) {
                    var columnName = menuItem.up('menu').ownerCt.activeHeader.dataIndex;

                    this.getController('report.SortController').addSortItem(columnName, 'ASC');
                }
            },
            'menuitem[name=sortDESC]': {
                click: function (menuItem, event, options) {
                    var columnName = menuItem.up('menu').ownerCt.activeHeader.dataIndex;

                    this.getController('report.SortController').addSortItem(columnName, 'DESC');
                }
            },
            'reporttoolbar button[action=add-column]': {
                click: function () {
                    PICS.app.fireEvent('showcolumnselector', {columnSelectorType: 'column'});
                }
            },
            'reportdatasetgrid pagingtoolbar button[itemId=refresh]': {
                click: function () {
                    PICS.app.fireEvent('refreshreport');
                }
            },
            'reportdatasetgrid pagingtoolbar combo[name=rows_per_page]': {
                select: function (combo, records, options) {
                    this.getReportDataSetsStore().updateReportPaging(records[0].get('field1'));
                }
            }
        });
    },

    // TODO: SHOULD NOT BE HERE
    configureColumnMenu: function (grid) {
        var me = this;

        grid.columns[0].ownerCt.on('menucreate', function (container, menu, opts) {

            //delete existing menu items
            menu.removeAll();

            //add new menu items
            var columnMenu = {
                xtype: 'menu',
                border: false,
                enableScrolling: false,
                floating: false,
                items: [{
                    cls: 'x-hmenu-sort-asc',
                    name: 'sortASC',
                    text: 'Sort Ascending',
                }, {
                    cls: 'x-hmenu-sort-desc',
                    name: 'sortDESC',
                    text: 'Sort Descending',
                }, {
                    name: 'removeColumn',
                    text: 'Remove'
                }]
            };

            menu.add(columnMenu);
        });
    },

    removeColumn: function (activeMenuItem) {
        var column_store = this.getReportReportsStore().first().columns(),
            colIndex = column_store.find('name', activeMenuItem);

        column_store.removeAt(colIndex);
        this.application.fireEvent('refreshreport');
    }
});
