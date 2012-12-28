Ext.define('PICS.controller.report.ReportData', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportPagingToolbar',
        selector: 'reportpagingtoolbar'
    },{
        ref: 'reportData',
        selector: 'reportdata'
    }],

    stores: [
        'report.AvailableFields',
        'report.Reports',
        'report.ReportDatas'
    ],
    
    views: [
        'PICS.view.report.report.ReportColumnTooltip'
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
        this.application.fireEvent('showavailablefieldmodal', 'column');
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

        this.application.fireEvent('refreshreport');
    },

    onColumnRender: function (cmp, eOpts) {
        var column = cmp.record;
        
        // do not apply any tooltips on rownumberers, etc
        if (Ext.getClassName(column) != 'PICS.model.report.Column') {
            return;
        }
        
        var target = cmp.el,
            field = column.getAvailableField(),
            text = field.get('text'),
            help = field.get('help');
        
        var tooltip = Ext.create('PICS.view.report.report.ReportColumnTooltip', {
            target: target
        });
        
        tooltip.update({
            text: text,
            help: help
        });
    },

    onColumnSortAsc: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column = cmp.up('menu').activeHeader.record;
        
        // clear sorts
        report.removeSorts();
        
        // add sort
        report.addSort(column, 'ASC');

        this.application.fireEvent('refreshreport');
    },

    onColumnSortDesc: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column = cmp.up('menu').activeHeader.record;
        
        // clear sorts
        report.removeSorts();
        
        // add sort
        report.addSort(column, 'DESC');

        this.application.fireEvent('refreshreport');
    },

    onReportDataBeforeRender: function (cmp, eOpts) {
        var store = this.getReportReportsStore();

        if (!store.isLoaded()) {
            store.on('load', function (store, records, successful, eOpts) {
                this.application.fireEvent('refreshreport');
            }, this);
        } else {
            this.application.fireEvent('refreshreport');
        }
    },
    
    onReportDataReconfigure: function (cmp, eOpts) {
        var report_data_store = cmp.getStore(), 
            report_paging_toolbar = this.getReportPagingToolbar();
        
        // load store - first page
        report_paging_toolbar.moveFirst();

        // update grid column header size
        cmp.updateColumnHeaderHeight(23);

        // remove no results message if one exists
        if (!report_data_store.isLoaded()) {
            report_data_store.on('load', function (store, records, successful, eOpts) {
                cmp.updateNoResultsMessage();
                
                report_paging_toolbar.updateDisplayInfo(store.getTotalCount());
            });
        } else {
            cmp.updateNoResultsMessage();
            
            report_paging_toolbar.updateDisplayInfo(report_data_store.getTotalCount());
        }
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

        // update rows per page in the report
        report.set('rowsPerPage', rows_per_page);
        
        this.application.fireEvent('refreshreport');
    }
});
