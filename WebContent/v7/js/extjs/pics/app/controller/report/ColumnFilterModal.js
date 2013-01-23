Ext.define('PICS.controller.report.ColumnFilterModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'columnModal',
        selector: 'columnmodal'
    }, {
        ref: 'filterModal',
        selector: 'filtermodal'
    }, {
        ref: 'columnList',
        selector: 'columnlist'
    }, {
        ref: 'filterList',
        selector: 'filterlist'
    }, {
        ref: 'columnModalSearchBox',
        selector: 'columnmodal textfield[name=search_box]'
    }, {
        ref: 'filterModalSearchBox',
        selector: 'filtermodal textfield[name=search_box]'
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
            'columnmodal': {
                beforeclose: this.beforeColumnModalClose
            },
            'filtermodal': {
                beforeclose: this.beforeFilterModalClose
            },
            'columnmodal textfield[name=search_box]': {
                keyup: this.onColumnModalSearch
            },
            'filtermodal textfield[name=search_box]': {
                keyup: this.onFilterModalSearch
            },
            'columnmodal button[action=add]':  {
                click: this.onColumnModalAddClick
            },
            'filtermodal button[action=add]':  {
                click: this.onFilterModalAddClick
            },
            'columnmodal button[action=cancel]':  {
                click: this.onColumnModalCancelClick
            },
            'filtermodal button[action=cancel]':  {
                click: this.onFilterModalCancelClick
            }
        });

        this.application.on({
            showcolumnmodal: this.showColumnModal,
            scope: this
        });
        
        this.application.on({
            showfiltermodal: this.showFilterModal,
            scope: this
        });
    },

    beforeColumnModalClose: function (cmp, event, eOpts) {
        var column_list = this.getColumnList();

        column_list.reset();
    },

    beforeFilterModalClose: function (cmp, event, eOpts) {
        var filter_list = this.getFilterList();

        filter_list.reset();
    },

    onColumnModalAddClick: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column_list = this.getColumnList(),
            column_modal_checkbox_model = column_list.getSelectionModel(),
            selected_columns = column_modal_checkbox_model.getSelection(),
            column_modal = this.getColumnModal();

        // Add the selected columns to the report model.
        report.addColumns(selected_columns);
      
        column_list.reset();

        column_modal.close();

        // Get new data for the modified report model (which will update the view, as well).
        PICS.data.ServerCommunication.loadData();
    },

    onColumnModalCancelClick: function (cmp, event, eOpts) {
        var column_list = this.getColumnList(),
            column_modal = this.getColumnModal();

        column_list.reset();
        
        column_modal.destroy();
    },

    onColumnModalSearch: function (cmp, event, eOpts) {
        var columns_store = this.getReportColumnsStore(),
            columns_search_box = this.getColumnModalSearchBox(),
            search_query = columns_search_box.getValue();

        columns_store.filter(Ext.create('PICS.ux.util.filter.ColumnFilterStoreFilter', {
            value: search_query
        }));
    },

    onFilterModalAddClick: function (cmp, event, eOpts) {
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

        filter_list.reset();

        filter_modal.destroy();

        // Get new data for the modified report.
        PICS.data.ServerCommunication.loadData();
    },

    onFilterModalCancelClick: function (cmp, event, eOpts) {
        var filter_list = this.getFilterList(),
            filter_modal = this.getFilterModal();

        filter_list.reset();

        filter_modal.destroy();
    },

    onFilterModalSearch: function (cmp, event, eOpts) {
        var filters_store = this.getReportFiltersStore(),
            filters_search_box = this.getFilterModalSearchBox(),
            search_query = filters_search_box.getValue();

        filters_store.filter(Ext.create('PICS.ux.util.filter.ColumnFilterStoreFilter', {
            value: search_query
        }));
    },
    
    showColumnModal: function () {
        // Create the modal.
        var column_modal = Ext.create('PICS.view.report.modal.column-filter.ColumnModal', {
            defaultFocus: 'textfield[name=search_box]'
        });

        // Display the modal.
        column_modal.show();
    },

    showFilterModal: function () {
        // Create the modal.
        var filter_modal = Ext.create('PICS.view.report.modal.column-filter.FilterModal', {
            defaultFocus: 'textfield[name=search_box]'
        });

        // Display the modal.
        filter_modal.show();
    }
});