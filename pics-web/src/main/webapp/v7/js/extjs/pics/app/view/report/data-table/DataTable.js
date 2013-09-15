Ext.define('PICS.view.report.data-table.DataTable', {
    extend: 'PICS.ux.grid.Panel',
    alias: 'widget.reportdatatable',

    requires: [
        'Ext.grid.RowNumberer',
        'PICS.view.report.data-table.PagingToolbar'
    ],

    store: 'report.DataTables',

    border: 0,
    // column configuration must be specified - will be overridden dynamically
    columns: [{
        xtype: 'rownumberer'
    }],
    dockedItems: [{
        xtype: 'reportpagingtoolbar',
        dock: 'top'
    }],
    id: 'data_table',
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
        menu.name = 'data_table_header_menu';

        // Hack solution to apparent ExtJS bug causing mis-alignment of the menu
        // with columns whose right edge is cut off by the right edge of the grid panel.
        menu.on('afterrender', function (menu) {
            menu.hide();
            menu.show();
        });

        menu.add({
            name: 'sort_asc',
            text: PICS.text('Report.execute.columnDropDown.sortAsc')
        }, {
            name: 'sort_desc',
            text: PICS.text('Report.execute.columnDropDown.sortDesc')
        }, {
            xtype: 'menuseparator'
        }, {
            name: 'function',
            text: PICS.text('Report.execute.columnDropDown.function') + '&hellip;'
        }, {
            xtype: 'menuseparator'
        }, {
            name: 'remove_column',
            text: PICS.text('Report.execute.columnDropDown.removeColumn')
        });
    },

    getFunctionMenuItem: function (menu_items) {
        var function_item;

        Ext.each(menu_items, function(item, index) {
            if (item.name == 'function' || item.name == 'remove_function') {
                function_item = item;

                return false;
            }
        });

        return function_item;
    },

    // column header height is dictated by the height of the rownumberer column
    // more information on how to override header height:
    // http://stackoverflow.com/questions/11676084/extjs-4-1-how-to-change-grid-panel-header-height/11695543#11695543
    updateGridColumns: function (new_grid_columns) {
        var grid_columns = [{
            xtype: 'rownumberer',
            height: 23,
            resizable: true,
            width: 50
        }];

        grid_columns = grid_columns.concat(new_grid_columns);

        this.reconfigure(null, grid_columns);
    },

    updateFunctionMenuItem: function(grid_column) {
        var menu = this.headerCt.getMenu(),
            menu_items = menu.items.items,
            function_item = this.getFunctionMenuItem(menu_items),
            column = grid_column.column,
            sql_function = column.get('sql_function');

        if (!function_item) {
            return false;
        }

        if (!sql_function) {
            function_item.setText(PICS.text('Report.execute.columnDropDown.function') + '&hellip;');
            function_item.name = "function";
        } else {
            function_item.setText(PICS.text('Report.execute.columnDropDown.removeFunction'));
            function_item.name = "remove_function";
        }
    }
});