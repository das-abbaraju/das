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
        ref: 'columnFilterList',
        selector: 'reportcolumnfilterlist'
    }, {
        ref: 'columnModalSearchBox',
        selector: 'reportcolumnmodal textfield[name=search_box]'
    }, {
        ref: 'filterModalSearchBox',
        selector: 'reportfiltermodal textfield[name=search_box]'
    }, {
        ref: 'columnModalAddButton',
        selector: 'reportcolumnmodal button[action=add]'
    }, {
        ref: 'filterModalAddButton',
        selector: 'reportfiltermodal button[action=add]'
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
                beforehide: this.beforeColumnModalHide
            },
            'reportfiltermodal': {
                beforehide: this.beforeFilterModalHide
            },
            'reportcolumnmodal textfield[name=search_box]': {
                keyup: this.onColumnModalTextfieldKeyup
            },
            'reportfiltermodal textfield[name=search_box]': {
                keyup: this.onFilterModalTextfieldKeyup
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
            },
            'reportcolumnlist': {
                selectionchange: this.onColumnListSelectionChange
            },
            'reportfilterlist': {
                selectionchange: this.onFilterListSelectionChange
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

    beforeColumnModalHide: function (cmp, eOpts) {
        this.getColumnList().reset();
    },

    beforeFilterModalHide: function (cmp, eOpts) {
        this.getFilterList().reset();
    },

    onColumnListSelectionChange: function (selection_model, selected, eOpts) {
        this.toggleAddButtonFromSelectionModelCount(selection_model, this.getColumnModalAddButton());
    },

    onFilterListSelectionChange: function (selection_model, selected, eOpts) {
        this.toggleAddButtonFromSelectionModelCount(selection_model, this.getFilterModalAddButton());
    },

    toggleAddButtonFromSelectionModelCount: function (selection_model, add_button) {
        if (selection_model.getCount()) {
            add_button.setDisabled(false);
        } else {
            add_button.setDisabled(true);
        }
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

        column_modal.hide();

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

        filter_modal.hide();
    },

    cancelColumnModal: function (cmp, event, eOpts) {
        var column_modal = this.getColumnModal();

        column_modal.hide();
    },

    cancelFilterModal: function (cmp, event, eOpts) {
        var filter_modal = this.getFilterModal();

        filter_modal.hide();
    },

    onColumnModalTextfieldKeyup: function (cmp, event, eOpts) {
        var column_list_view = this.getColumnList();

        this.searchColumnList();
        column_list_view.updateNoResultsMessage();
    },

    onFilterModalTextfieldKeyup: function (cmp, event, eOpts) {
        var filter_list_view = this.getFilterList();

        this.searchFilterList();
        filter_list_view.updateNoResultsMessage();
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
        var column_modal = this.getColumnModal();

        if (!column_modal) {
            column_modal = Ext.create('PICS.view.report.modal.column-filter.ColumnModal', {
                defaultFocus: 'textfield[name=search_box]'
            });
        }

        column_modal.show();
    },

    openFilterModal: function () {
        var filter_modal = this.getFilterModal();

        if (!filter_modal) {
            filter_modal = Ext.create('PICS.view.report.modal.column-filter.FilterModal', {
                defaultFocus: 'textfield[name=search_box]'
            });
        }

        filter_modal.show();
    }
});