Ext.define('PICS.view.report.data-table.DataTable', {
    extend: 'Ext.grid.Panel',
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
            text: 'Sort Ascending'
        }, {
            name: 'sort_desc',
            text: 'Sort Descending'
        }, {
            xtype: 'menuseparator'
        }, {
            name: 'function',
            text: 'Functions' + '&hellip;'
        }, {
            xtype: 'menuseparator'
        }, {
            name: 'remove_column',
            text: 'Remove'
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
            width: 50
        }];
        
        grid_columns = grid_columns.concat(new_grid_columns);
        
        this.reconfigure(null, grid_columns);
    },
    
    // update or reset no results message
    updateNoResultsMessage: function () {
        var store = this.getStore(),
            view = this.getView();
        
        if (store.getCount() == 0) {
            view.emptyText = '<div class="x-grid-empty">no results</div>';
        } else {
            view.emptyText = '';
        }

        view.refresh();
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
            function_item.setText('Functions' + '&hellip;');
            function_item.name = "function";
        } else {
            function_item.setText('Remove Function');
            function_item.name = "remove_function";
        }
    }
});