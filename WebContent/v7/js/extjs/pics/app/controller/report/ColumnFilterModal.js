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

    onColumnModalAddClick: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            column_modal = this.getColumnModal(),
            column_list = this.getColumnList(),
            column_modal_checkbox_model = column_list.getSelectionModel(),
            selected_columns = column_modal_checkbox_model.getSelection();

        // Add the selected column to the report model.
        report.addColumns(selected_columns);
        
        // Close the column modal.
        column_modal.close();

        // Get new data for the modified report model.
        PICS.data.ServerCommunication.loadData();
    },

    onColumnModalCancelClick: function (cmp, event, eOpts) {
        var column_modal = this.getColumnModal();
        
        column_modal.close();
    },

    onColumnModalSearch: function (cmp, event, eOpts) {
        var columns_store = this.getReportColumnsStore(),
            columns_search_box = this.getColumnModalSearchBox(),
            search_query = columns_search_box.getValue();

        columns_store.clearFilter();

        columns_store.filter(Ext.create('PICS.ux.util.filter.ColumnFilterStoreFilter', {
            value: search_query
        }));
    },

    onFilterModalAddClick: function (cmp, event, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            filter_modal = this.getFilterModal(),
            filter_list = this.getFilterList(),
            filter_modal_checkbox_model = filter_list.getSelectionModel(),
            selected_filters = filter_modal_checkbox_model.getSelection();
    
        // Add the selected filter to the report model.
        report.addFilters(selected_filters);

        // Close the filter modal.
        filter_modal.close();
        
        this.application.fireEvent('refreshfilters');
    },

    onFilterModalCancelClick: function (cmp, event, eOpts) {
        var filter_modal = this.getFilterModal();
        
        filter_modal.close();
    },

    onFilterModalSearch: function (cmp, event, eOpts) {
        var filters_store = this.getReportFiltersStore(),
            filters_search_box = this.getFilterModalSearchBox(),
            search_query = filters_search_box.getValue();

        filters_store.clearFilter();

        filters_store.filter(Ext.create('PICS.ux.util.filter.ColumnFilterStoreFilter', {
            value: search_query
        }));
    },

    showColumnModal: function () {
        var column_modal = Ext.create('PICS.view.report.modal.column-filter.ColumnModal', {
            defaultFocus: 'textfield[name=search_box]'
        });
        
        // TODO: possibly link in the deselection on cancel, close, ReportModal.close
        var column_list = this.getColumnList();

        column_modal.show();
        column_list.getSelectionModel().deselectAll();
    },

    showFilterModal: function () {
        var filter_modal = Ext.create('PICS.view.report.modal.column-filter.FilterModal', {
            defaultFocus: 'textfield[name=search_box]'
        });
        
        // TODO: possibly link in the deselection on cancel, close, ReportModal.close
        var filter_list = this.getFilterList();
        
        filter_modal.show();
        filter_list.getSelectionModel().deselectAll();
    }
});