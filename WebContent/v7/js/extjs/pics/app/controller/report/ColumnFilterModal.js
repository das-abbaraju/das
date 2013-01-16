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
        selection: 'filtermodal textfield[name=search_box]'
    }],

    stores: [
        'report.Reports2'
    ],

    views: [
        'PICS.view.report.modal.column-filter.ColumnModal',
        'PICS.view.report.modal.column-filter.FilterModal',
        // TODO: Move this out of views (e.g., to requires?)
        'PICS.ux.util.ColumnFilterStoreFilter'
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
            showavailablefieldmodal: this.showAvailableFieldModal,
            scope: this
        });
    },

    onColumnModalAddClick: function (cmp, event, eOpts) {
        var report_store = this.getReports2Store(),
            report = report_store.first(),
            column_list = this.getColumnList(),
            column_modal_checkbox_model = column_list.getSelectionModel(),
            selected_columns = column_modal_checkbox_model.getSelection();

        report.addColumns(selected_columns);
    },

    onColumnModalCancelClick: function (cmp, event, eOpts) {
        var column_modal = this.getColumnModal();
        
        column_modal.close();
    },

    onColumnModalSearch: function (cmp, event, eOpts) {
        var columns_store = this.getColumnsStore(),
            columns_search_box = this.getColumnsSearchBox(),
            search_query = columns_search_box.getValue();

        columns_store.clearFilter();

        columns_store.filter(Ext.create('PICS.ux.util.ColumnFilterStoreFilter'), {
            value: search_query
        })
    },

    onFilterModalAddClick: function (cmp, event, eOpts) {
        var report_store = this.getReports2Store(),
            report = report_store.first(),
            filter_list = this.getFilterList(),
            filter_modal_checkbox_model = filter_list.getSelectionModel(),
            selected_filters = filter_modal_checkbox_model.getSelection();
    
        report.addColumns(selected_filters);
    },

    onFilterModalCancelClick: function (cmp, event, eOpts) {
        var filter_modal = this.getFilterModal();
        
        filter_modal.close();
    },

    onFilterModalSearch: function (cmp, event, eOpts) {
        var filters_store = this.getFiltersStore(),
            filters_search_box = this.getFiltersSearchBox(),
            search_query = columns_search_box.getValue();

        filters_store.clearFilter();

        filters_store.filter(Ext.create('PICS.ux.util.ColumnFilterStoreFilter'), {
            value: search_query
        })
    },

    showColumnModal: function () {
        var column_modal = Ext.create('PICS.view.report.modal.column-filter.ColumnModal', {
            defaultFocus: 'textfield[name=search_box]'
        });

        column_modal.show();
    },

    showFilterModal: function () {
        var filter_modal = Ext.create('PICS.view.report.modal.column-filter.FilterModal', {
            defaultFocus: 'textfield[name=search_box]'
        });

        filter_modal.show();
    }
});