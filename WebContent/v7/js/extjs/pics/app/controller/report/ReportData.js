Ext.define('PICS.controller.report.ReportData', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportPagingToolbar',
        selector: 'reportpagingtoolbar'
    }],

    stores: [
        'report.Reports',
        'report.DataSets'
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

            'menu[name=report_data_header_menu] menuitem[name=sort_asc]': {
                click: this.onColumnSortAsc
            },

            'menu[name=report_data_header_menu] menuitem[name=sort_desc]': {
                click: this.onColumnSortDesc
            },

            'menu[name=report_data_header_menu] menuitem[name=remove_column]': {
                click: this.onColumnRemove
            }
        });

        this.application.on({
            refreshreportdisplayinfo: this.refreshReportDisplayInfo,
            scope: this
        });
    },

    onAddColumn: function (cmp, event, eOpts) {
        this.application.fireEvent('showavailablefieldmodal', 'column');
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

    onReportReconfigure: function (cmp, eOpts) {
        this.application.fireEvent('refreshreportdisplayinfo');
    },

    onReportRefreshClick: function (cmp, event, eOpts) {
        this.application.fireEvent('refreshreport');
    },

    onRowsPerPageSelect: function (cmp, records, options) {
        this.getReportDataSetsStore().updateReportPaging(parseInt(cmp.getValue()));
    },

    refreshReportDisplayInfo: function () {
        var report_paging_toolbar = this.getReportPagingToolbar();

        report_paging_toolbar.updateDisplayInfo();
    }
});
