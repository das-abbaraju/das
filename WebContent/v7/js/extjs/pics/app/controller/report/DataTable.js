Ext.define('PICS.controller.report.DataTable', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'pagingToolbar',
        selector: 'reportpagingtoolbar'
    }, {
        ref: 'dataTable',
        selector: 'reportdatatable'
    }],

    stores: [
        'report.Reports',
        'report.DataTables'
    ],
    
    init: function () {
        this.control({
            'reportdatatable': {
                beforerender: this.beforeDataTableRender,
                reconfigure: this.reconfigureDataTable
            },

            'reportdatatable headercontainer': {
            	columnmove: this.moveColumn,
            	columnresize: this.resizeColumn
            },

            'reportdatatable gridcolumn': {
                render: this.renderGridColumn
            },
            
            'reportpagingtoolbar': {
                changepage: this.moveToPage
            },

            'reportpagingtoolbar button[itemId=refresh]': {
                click: this.refreshReport
            },
            
            'reportpagingtoolbar button[itemId=first]': {
                click: this.moveToFirstPage
            },
            
            'reportpagingtoolbar button[itemId=prev]': {
                click: this.moveToPreviousPage
            },
            
            'reportpagingtoolbar button[itemId=next]': {
                click: this.moveToNextPage
            },
            
            'reportpagingtoolbar button[itemId=last]': {
                click: this.moveToLastPage
            },

            'reportpagingtoolbar combo[name=limit]': {
                select: this.changeLimit
            },

            'reportpagingtoolbar button[action=add-column]': {
                click: this.openColumnModal
            },

            'menu[name=data_table_header_menu] menuitem[name=function]': {
                click: this.openColumnFunctionModal
            },

            'menu[name=data_table_header_menu] menuitem[name=remove_column]': {
                click: this.removeColumn
            },

            'menu[name=data_table_header_menu] menuitem[name=sort_asc]': {
                click: this.sortColumnAsc
            },

            'menu[name=data_table_header_menu] menuitem[name=sort_desc]': {
                click: this.sortColumnDesc
            }
        });
    },
    
    beforeDataTableRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            grid_columns = report.convertColumnsToGridColumns(),
            data_table_view = this.getDataTable();

        data_table_view.updateGridColumns(grid_columns);
    },
    
    changeLimit: function (cmp, records, options) {
        var limit = cmp.getValue();

        PICS.data.ServerCommunication.loadData(1, limit);
    },
    
    moveColumn: function (cmp, column, fromIdx, toIdx, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            // off by one due to rownumberer
            from_index = fromIdx - 1,
            // off by one due to rownumberer
            to_index = toIdx - 1;
        
        report.moveColumnByIndex(from_index, to_index);
    },
    
    moveToPage: function (cmp, page, eOpts) {
        PICS.data.ServerCommunication.loadData(page);
    },
    
    moveToFirstPage: function (cmp, event, eOpts) {
        PICS.data.ServerCommunication.loadData(1);
    },
    
    moveToPreviousPage: function (cmp, event, eOpts) {
        var data_table_store = this.getReportDataTablesStore(),
            current_page = data_table_store.currentPage,
            previous_page = current_page - 1;
        
        PICS.data.ServerCommunication.loadData(previous_page);
    },
    
    moveToNextPage: function (cmp, event, eOpts) {
        var data_table_store = this.getReportDataTablesStore(),
            current_page = data_table_store.currentPage,
            next_page = current_page + 1;
        
        PICS.data.ServerCommunication.loadData(next_page);
    },
    
    moveToLastPage: function (cmp, event, eOpts) {
        var data_table_store = this.getReportDataTablesStore(),
            report_paging_toolbar_view = this.getPagingToolbar();
            last_page = report_paging_toolbar_view.getPageData().pageCount;
        
        PICS.data.ServerCommunication.loadData(last_page);
    },

    openColumnFunctionModal: function (cmp, event, eOpts) {
        var column = cmp.up('menu').activeHeader.column;

        this.application.fireEvent('opencolumnfunctionmodal', column);
    },

    openColumnModal: function (cmp, event, eOpts) {
        this.application.fireEvent('opencolumnmodal');
    },
    
    reconfigureDataTable: function (cmp, eOpts) {
        var data_table_view = cmp,
            data_table_store = data_table_view.getStore(),
            results_total = data_table_store.getTotalCount(),
            report_paging_toolbar = this.getPagingToolbar();
        
        // remove no results message if one exists
        data_table_view.updateNoResultsMessage();
        
        // update display count
        report_paging_toolbar.updateDisplayInfo(results_total);
    },
    
    removeColumn: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column_store = report.columns(),
            column = cmp.up('menu').activeHeader.column;

        column_store.remove(column);

        PICS.data.ServerCommunication.loadData();
    },
    
    renderGridColumn: function (cmp, eOpts) {
        // only create tooltips for PICS.ux.grid.column.Column(s)
        if (typeof cmp.createTooltip == 'function') {
            cmp.createTooltip();
        }
    },
    
    resizeColumn: function (ct, column, width, eOpts) {
        column.column.set('width', width);
    },
    
    sortColumnAsc: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column = cmp.up('menu').activeHeader.column;

        // clear sorts
        report.removeSorts();
        
        // add sort
        report.addSort(column, 'ASC');

        PICS.data.ServerCommunication.loadData();
    },

    sortColumnDesc: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column = cmp.up('menu').activeHeader.column;
        
        // clear sorts
        report.removeSorts();
        
        // add sort
        report.addSort(column, 'DESC');

        PICS.data.ServerCommunication.loadData();
    },
    
    refreshReport: function (cmp, event, eOpts) {
        PICS.data.ServerCommunication.loadData(1);
    }
});
