Ext.define('PICS.controller.report.ReportData', {
    extend: 'Ext.app.Controller',

    requires: [
       'PICS.data.ServerCommunication'               
    ],

    refs: [{
        ref: 'reportPagingToolbar',
        selector: 'reportpagingtoolbar'
    }, {
        ref: 'reportData',
        selector: 'reportdata'
    }],

    stores: [
        'report.Reports',
        'report.ReportDatas'
    ],
    
    init: function () {
        this.control({
            'reportdata': {
                beforerender: this.onReportDataBeforeRender,
                reconfigure: this.onReportDataReconfigure
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
    },

    onAddColumn: function (cmp, event, eOpts) {
        this.application.fireEvent('showcolumnmodal');
    },

    onColumnFunction: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column_store = report.columns(),
            column = cmp.up('menu').activeHeader,
            // off by one due to rownumberer
            column_index = column.getIndex() - 1,
            selected_column;
        
        selected_column = column_store.getAt(column_index);

        this.application.fireEvent('showcolumnfunctionmodal', selected_column);
    },

    onColumnMove: function (cmp, column, fromIdx, toIdx, eOpts) {
		var report_store = this.getReportReportsStore(),
			report = report_store.first(),
			// off by one due to rownumberer
			from_index = fromIdx - 1,
			// off by one due to rownumberer
			to_index = toIdx - 1;
		
		report.moveColumnByIndex(from_index, to_index);
	},

    onColumnRemove: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column_store = report.columns(),
            column = cmp.up('menu').activeHeader,
            // off by one due to rownumberer
            column_index = column.getIndex() - 1;

        column_store.removeAt(column_index);

        PICS.data.ServerCommunication.loadData();
    },

    onColumnRender: function (cmp, eOpts) {
        // only create tooltips for PICS.ux.grid.column.Column(s)
        if (typeof cmp.createTooltip == 'function') {
            cmp.createTooltip();
        }
    },

    onColumnSortAsc: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column = cmp.up('menu').activeHeader.column;

        // clear sorts
        report.removeSorts();
        
        // add sort
        report.addSort(column, 'ASC');

        PICS.data.ServerCommunication.loadData();
    },

    onColumnSortDesc: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column = cmp.up('menu').activeHeader.column;
        
        // clear sorts
        report.removeSorts();
        
        // add sort
        report.addSort(column, 'DESC');

        PICS.data.ServerCommunication.loadData();
    },

    onReportDataBeforeRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            grid_columns = report.convertColumnsToGridColumns(),
            report_data_view = this.getReportData();

        report_data_view.updateGridColumns(grid_columns);
    },
    
    onReportDataReconfigure: function (cmp, eOpts) {
        var report_data = cmp,
            report_data_store = cmp.getStore(),
            total = report_data_store.getTotalCount(),
            report_paging_toolbar = this.getReportPagingToolbar();
        
        // remove no results message if one exists
        report_data.updateNoResultsMessage();
        
        // update display count
        report_paging_toolbar.updateDisplayInfo(total);
    },

    onReportRefreshClick: function (cmp, event, eOpts) {
        var report_data = this.getReportData(),
            report_data_store = report_data.store;
        
        report_data_store.loadPage(1);
    },

    onRowsPerPageSelect: function (cmp, records, options) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_data = this.getReportData(),
            report_data_store = report_data.store,
            value = cmp.getValue();

        // TODO: THIS IS TOTALLY NOT NEEDED, EVERYTHING SHOULD BE BASED ON LIMIT NOT ROWS PER PAGE, DELETE THAT SHIT
        // TODO: THIS IS TOTALLY NOT NEEDED, EVERYTHING SHOULD BE BASED ON LIMIT NOT ROWS PER PAGE, DELETE THAT SHIT
        report.set('rowsPerPage', value);
        report_data_store.updateProxyParameters(report.toRequestParams());
        
        report_data_store.setLimit(value);
        
        // load new data
        report_data_store.loadPage(1);
    }
});
