Ext.define('PICS.controller.report.ReportData', {
    extend: 'Ext.app.Controller',

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
            	columnmove: this.moveColumn
            },

            'reportdata gridcolumn': {
                render: this.onGridColumnRender
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

            'menu[name=report_data_header_menu] menuitem[name=function]': {
                click: this.openColumnFunctionModal
            },

            'menu[name=report_data_header_menu] menuitem[name=remove_column]': {
                click: this.removeColumn
            },

            'menu[name=report_data_header_menu] menuitem[name=sort_asc]': {
                click: this.sortColumnAsc
            },

            'menu[name=report_data_header_menu] menuitem[name=sort_desc]': {
                click: this.sortColumnDesc
            }
        });
    },
    
    onGridColumnRender: function (cmp, eOpts) {
        // only create tooltips for PICS.ux.grid.column.Column(s)
        if (typeof cmp.createTooltip == 'function') {
            cmp.createTooltip();
        }
    },
    
    onReportDataBeforeRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            grid_columns = report.convertColumnsToGridColumns(),
            report_data_view = this.getReportData();

        report_data_view.updateGridColumns(grid_columns);
    },
    
    onReportDataReconfigure: function (cmp, eOpts) {
        var report_data_view = cmp,
            report_data_store = report_data_view.getStore(),
            results_total = report_data_store.getTotalCount(),
            report_paging_toolbar = this.getReportPagingToolbar();
        
        // remove no results message if one exists
        report_data_view.updateNoResultsMessage();
        
        // update display count
        report_paging_toolbar.updateDisplayInfo(results_total);
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
        var report_data_store = this.getReportReportDatasStore(),
            current_page = report_data_store.currentPage,
            previous_page = current_page - 1;
        
        PICS.data.ServerCommunication.loadData(previous_page);
    },
    
    moveToNextPage: function (cmp, event, eOpts) {
        var report_data_store = this.getReportReportDatasStore(),
            current_page = report_data_store.currentPage,
            next_page = current_page + 1;
        
        PICS.data.ServerCommunication.loadData(next_page);
    },
    
    moveToLastPage: function (cmp, event, eOpts) {
        var report_data_store = this.getReportReportDatasStore(),
            report_paging_toolbar_view = this.getReportPagingToolbar();
            last_page = report_paging_toolbar_view.getPageData().pageCount;
        
        PICS.data.ServerCommunication.loadData(last_page);
    },
    
    removeColumn: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column_store = report.columns(),
            column = cmp.up('menu').activeHeader.column;

        column_store.remove(column);

        PICS.data.ServerCommunication.loadData();
    },
    
    openColumnFunctionModal: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column = cmp.up('menu').activeHeader.column;

        this.application.fireEvent('opencolumnfunctionmodal', column);
    },
    
    openColumnModal: function (cmp, event, eOpts) {
        this.application.fireEvent('opencolumnmodal');
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
