Ext.define('PICS.controller.report.ReportData', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportPagingToolbar',
        selector: 'reportpagingtoolbar'
    }],

    stores: [
        'report.Reports',
        'report.ReportDatas'
    ],

    init: function () {
        this.control({
            'reportdata': {
                reconfigure: this.onReportReconfigure
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

    onAddColumn: function (cmp, event, eOpts) {
        this.application.fireEvent('showavailablefieldmodal', 'column');
    },

    onColumnFunction: function (cmp, event, eOpts) {
        var column_store = this.getReportReportsStore().first().columns(),
            column_name = cmp.up('menu').activeHeader.dataIndex,
            column = column_store.findRecord('name', column_name);

        var modal = Ext.create('PICS.view.report.function.FunctionModal', {
            column: column
        });

        modal.show();
    },

    onColumnRemove: function (cmp, event, eOpts) {
        var column_store = this.getReportReportsStore().first().columns(),
            column_name = cmp.up('menu').activeHeader.dataIndex,
            column = column_store.findRecord('name', column_name);

        column_store.remove(column);

        this.application.fireEvent('refreshreport');
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
