Ext.define('PICS.controller.report.ColumnFilterModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'columnModal',
        selector: 'reportcolumnmodal'
    }, {
        ref: 'filterModal',
        selector: 'reportfiltermodal'
    }, {
        ref: 'columnList',
        selector: 'reportcolumnlist'
    }, {
        ref: 'filterList',
        selector: 'reportfilterlist'
    }, {
        ref: 'columnModalSearchBox',
        selector: 'reportcolumnmodal textfield[name=search_box]'
    }, {
        ref: 'filterModalSearchBox',
        selector: 'reportfiltermodal textfield[name=search_box]'
    }],

    stores: [
        'report.Reports',
        'report.Columns',
        'report.Filters'
    ],

    views: [
        'PICS.view.report.modal.column-filter.ColumnModal',
        'PICS.view.report.modal.column-filter.FilterModal'
    ],

    init: function () {
        this.control({
            'reportcolumnmodal': {
                beforeclose: this.beforeColumnModalClose
            },
            'reportfiltermodal': {
                beforeclose: this.beforeFilterModalClose
            },
            'reportcolumnmodal textfield[name=search_box]': {
                keyup: this.searchColumnList
            },
            'reportfiltermodal textfield[name=search_box]': {
                keyup: this.searchFilterList
            },
            'reportcolumnmodal button[action=add]':  {
                click: this.addColumn
            },
            'reportfiltermodal button[action=add]':  {
                click: this.addFilter
            },
            'reportcolumnmodal button[action=cancel]':  {
                click: this.cancelColumnModal
            },
            'reportfiltermodal button[action=cancel]':  {
                click: this.cancelFilterModal
            }
        });

        this.application.on({
            opencolumnmodal: this.openColumnModal,
            scope: this
        });
        
        this.application.on({
            openfiltermodal: this.openFilterModal,
            scope: this
        });
    },

    addColumn: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column_list = this.getColumnList(),
            column_modal_checkbox_model = column_list.getSelectionModel(),
            selected_columns = column_modal_checkbox_model.getSelection(),
            column_modal = this.getColumnModal();
        
        // Add the selected columns to the report model.
        report.addColumns(selected_columns);
        
        column_modal.close();
        
        // Get new data for the modified report model (which will update the view, as well).
        PICS.data.ServerCommunication.loadData();
    },

    addFilter: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_list = this.getFilterList(),
            filter_modal_checkbox_model = filter_list.getSelectionModel(),
            selected_filters = filter_modal_checkbox_model.getSelection(),
            filter_modal = this.getFilterModal();
        
        // Add the selected filters to the report model.
        report.addFilters(selected_filters);
        
        // Add the selected filters to the FilterOptions view.
        this.application.fireEvent('refreshfilters');
        
        filter_modal.close();        
    },
    
    beforeColumnModalClose: function (cmp, eOpts) {
        var column_list = this.getColumnList();

        column_list.reset();
    },

    beforeFilterModalClose: function (cmp, eOpts) {
        var filter_list = this.getFilterList();

        filter_list.reset();
    },

    cancelColumnModal: function (cmp, event, eOpts) {
        var column_list = this.getColumnList(),
            column_modal = this.getColumnModal();

        column_modal.close();
    },

    cancelFilterModal: function (cmp, event, eOpts) {
        var filter_list = this.getFilterList(),
            filter_modal = this.getFilterModal();
        
        filter_modal.close();
    },
    
    searchColumnList: function (cmp, event, eOpts) {
        var columns_store = this.getReportColumnsStore(),
            columns_search_box = this.getColumnModalSearchBox(),
            search_query = columns_search_box.getValue();

        columns_store.clearFilter();

        columns_store.filter(Ext.create('PICS.ux.util.filter.ColumnFilterStoreFilter', {
            value: search_query
        }));
    },

    searchFilterList: function (cmp, event, eOpts) {
        var filters_store = this.getReportFiltersStore(),
            filters_search_box = this.getFilterModalSearchBox(),
            search_query = filters_search_box.getValue();

        filters_store.clearFilter();

        filters_store.filter(Ext.create('PICS.ux.util.filter.ColumnFilterStoreFilter', {
            value: search_query
        }));
    },
    
    openColumnModal: function () {
        // Create the modal.
        var column_modal = Ext.create('PICS.view.report.modal.column-filter.ColumnModal', {
            defaultFocus: 'textfield[name=search_box]'
        });

        // Display the modal.
        column_modal.show();
    },

    openFilterModal: function () {
        // Create the modal.
        var filter_modal = Ext.create('PICS.view.report.modal.column-filter.FilterModal', {
            defaultFocus: 'textfield[name=search_box]'
        });

        // Display the modal.
        filter_modal.show();
    }
});