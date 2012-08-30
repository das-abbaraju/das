Ext.define('PICS.controller.report.ReportData', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportPagingToolbar',
        selector: 'reportpagingtoolbar'
    }],

    stores: [
        'report.AvailableFields',
        'report.Reports',
        'report.ReportDatas'
    ],

    init: function () {
        this.control({
            'reportdata': {
                reconfigure: this.onReportReconfigure
            },

            'reportdata headercontainer': {
            	columnmove: this.onColumnMove
            },

            'reportdata gridcolumn': {
                render: this.onColumnRender
            },

            'reportpagingtoolbar button[itemId=refresh]': {
                click: this.onReportRefreshClick
            },

            'reportpagingtoolbar combo[name=rows_per_page]': {
                select: this.onRowsPerPageSelect
            },

            'reportpagingtoolbar button[action=add-column]': {
                click: this.onAddColumn
            },

            'menu[name=report_data_header_menu] menuitem[name=function]': {
                click: this.onColumnFunction
            },

            'menu[name=report_data_header_menu] menuitem[name=remove_column]': {
                click: this.onColumnRemove
            },

            'menu[name=report_data_header_menu] menuitem[name=sort_asc]': {
                click: this.onColumnSortAsc
            },

            'menu[name=report_data_header_menu] menuitem[name=sort_desc]': {
                click: this.onColumnSortDesc
            }
        });

        this.application.on({
            refreshreportdisplayinfo: this.onRefreshReportDisplayInfo,
            scope: this
        });
    },

    // find index position of the grid column starting after the row numberer (row number)
    findGridColumnIndexPosition: function (column) {
        var grid_columns = Ext.ComponentQuery.query('reportdata gridcolumn'),
            num_grid_columns = grid_columns.length,
            index_position = -1;

        // remove first grid column - row numberer
        grid_columns = grid_columns.slice(1, num_grid_columns);

        Ext.each(grid_columns, function (grid_column, index) {
            if (column.id == grid_column.id) {
                index_position = index;

                return;
            }
        });

        return index_position;
    },

    onAddColumn: function (cmp, event, eOpts) {
        this.application.fireEvent('showavailablefieldmodal', 'column');
    },

    onColumnFunction: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column_store = report.columns(),
            selected_grid_column = cmp.up('menu').activeHeader,
            selected_grid_column_index,
            selected_column;

        selected_grid_column_index = this.findGridColumnIndexPosition(selected_grid_column);

        if (selected_grid_column_index == -1) {
            Ext.Error.raise('Invalid Grid column');
        }

        selected_column = column_store.getAt(selected_grid_column_index);

        this.application.fireEvent('showcolumnfunctionmodal', selected_column);
    },

    onColumnMove: function (cmp, column, fromIdx, toIdx, eOpts) {
		var report_store = this.getReportReportsStore(),
			report = report_store.first(),
			column_store = report.columns(),
			columns = [];

		// generate an array of columns from column store
		column_store.each(function (column, index) {
			columns[index] = column;
		});

		// splice out the column store - column your moving
		var spliced_column = columns.splice((fromIdx - 1), 1);

		// insert the column store - column to the position you moved it to
		columns.splice((toIdx - 1), 0, spliced_column);

		// remove all column store records
		column_store.removeAll();

		// re-insert column store records in the new position
		Ext.each(columns, function (column, index) {
			column_store.add(column);
		});
	},

    onColumnRemove: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column_store = report.columns(),
            selected_grid_column = cmp.up('menu').activeHeader,
            selected_grid_column_index,
            selected_column;

        selected_grid_column_index = this.findGridColumnIndexPosition(selected_grid_column);

        if (selected_grid_column_index == -1) {
            Ext.Error.raise('Invalid Grid column');
        }

        column_store.removeAt(selected_grid_column_index);

        this.application.fireEvent('refreshreport');
    },

    onColumnRender: function (cmp, eOpts) {
        var store = this.getReportAvailableFieldsStore();

        function createTooltip(store) {
            var field = store.findRecord('name', cmp.dataIndex);

            if (!field) {
                return false;
            }

            var help = field.get('help');

            Ext.create('Ext.tip.ToolTip', {
                anchor: 'bottom',
                showDelay: 0,
                target: cmp.el,
                html: [
                    '<div>',
                        '<h3>' + field.get('text') + '</h3>',
                        '<p>' + field.get('help') + '</p>',
                    '</div>'
                ].join('')
            });

            Ext.QuickTips.init();
        }

        if (!store.isLoaded()) {
            store.on('load', function (store, records, successful, eOpts) {
                createTooltip(store);
            });
        } else {
            createTooltip(store);
        }
    },

    onColumnSortAsc: function (cmp, event, eOpts) {
        var sort_store = this.getReportReportsStore().first().sorts(),
            name = cmp.up('menu').activeHeader.dataIndex;

        sort_store.removeAll();
        sort_store.add(Ext.create('PICS.model.report.Sort', {
            name: name,
            direction: 'SUPERMAN' // lawl could be anything (ASC)
        }));

        this.application.fireEvent('refreshreport');
    },

    onColumnSortDesc: function (cmp, event, eOpts) {
        var sort_store = this.getReportReportsStore().first().sorts(),
            name = cmp.up('menu').activeHeader.dataIndex;

        sort_store.removeAll();
        sort_store.add(Ext.create('PICS.model.report.Sort', {
            name: name,
            direction: 'DESC'
        }));

        this.application.fireEvent('refreshreport');
    },

    onRefreshReportDisplayInfo: function () {
        var store = this.getReportReportDatasStore(),
            report_paging_toolbar = this.getReportPagingToolbar(),
            count;

        if (!store.isLoaded()) {
            store.on('load', function (store, records, successful, eOpts) {
                count = store.getTotalCount();

                report_paging_toolbar.updateDisplayInfo(count);
            }, this, {
                single: true
            });
        } else {
            count = store.getTotalCount();

            report_paging_toolbar.updateDisplayInfo(count);
        }
    },

    onReportReconfigure: function (cmp, eOpts) {
        var report_paging_toolbar = this.getReportPagingToolbar();
            report_paging_toolbar.moveFirst();

        this.application.fireEvent('refreshreportdisplayinfo');
    },

    onReportRefreshClick: function (cmp, event, eOpts) {
        this.application.fireEvent('refreshreport');
    },

    onRowsPerPageSelect: function (cmp, records, options) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_data_store = this.getReportReportDatasStore(),
            rows_per_page = parseInt(cmp.getValue()),
            report_paging_toolbar = this.getReportPagingToolbar();

        report.set('rowsPerPage', rows_per_page);

        report_data_store.pageSize = rows_per_page;
        report_data_store.configureProxyUrl(report);

        report_paging_toolbar.moveFirst();
    }
});
