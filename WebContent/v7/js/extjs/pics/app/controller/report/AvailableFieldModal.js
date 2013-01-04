Ext.define('PICS.controller.report.AvailableFieldModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'availableFieldModal',
        selector: 'reportavailablefieldmodal'
    }, {
        ref: 'availableFieldList',
        selector: 'reportavailablefieldlist'
    }, {
        ref: 'availableFieldSearchBox',
        selector: 'reportavailablefieldmodal textfield[name=search_box]'
    }],

    stores: [
        'report.AvailableFieldsByCategory',
        'report.Reports'
    ],

    views: [
        'PICS.view.report.available-field.AvailableFieldModal',
        'PICS.ux.util.FilterMultipleColumn'
    ],

    init: function () {
        this.control({
            'reportavailablefieldmodal textfield[name=search_box]': {
                keyup: this.onAvailableFieldSearch
            },
            'reportavailablefieldmodal button[action=add]':  {
                click: this.onAvailableFieldAdd
            },
            'reportavailablefieldmodal button[action=cancel]':  {
                click: this.onAvailableFieldCancel
            }
        });

        this.application.on({
            showavailablefieldmodal: this.showAvailableFieldModal,
            scope: this
        });
    },

    addColumnToReport: function(cmp, event, eOpts) {
        var available_field_list = this.getAvailableFieldList(),
            available_field_checkbox_model = available_field_list.getSelectionModel();

        if (available_field_checkbox_model.getCount() > 0) {
            var report_store = this.getReportReportsStore(),
                report = report_store.first(),
                available_fields = available_field_checkbox_model.getSelection(),
                columns = [];
            
            Ext.Array.forEach(available_fields, function (available_field) {
                var column = available_field.toColumn();
                
                columns.push(column);
            });
            
            report.addColumns(columns);

            this.application.fireEvent('refreshreport');
        }
    },

    addFilterToReport: function(cmp, event, eOpts) {
        var available_field_list = this.getAvailableFieldList(),
            available_field_checkbox_model = available_field_list.getSelectionModel();
    
        if (available_field_checkbox_model.getCount() > 0) {
            var report_store = this.getReportReportsStore(),
                report = report_store.first(),
                available_fields = available_field_checkbox_model.getSelection(),
                filters = [];

            Ext.Array.forEach(available_fields, function (available_field) {
                var filter = available_field.toFilter();
                
                filters.push(filter);
            });
            
            report.addFilters(filters);

            this.application.fireEvent('refreshfilters');
        }
    },

    onAvailableFieldAdd: function (cmp, event, eOpts) {
        var available_field_modal = this.getAvailableFieldModal(),
            available_field_list = this.getAvailableFieldList(),
            available_field_search_box = this.getAvailableFieldSearchBox(),
            available_field_checkbox_model = available_field_list.getSelectionModel(),
            type = available_field_modal.type;

        if (type === 'column') {
            this.addColumnToReport();
        } else if (type === 'filter') {
            this.addFilterToReport();
        } else {
            Ext.Error.raise('Invalid type:' + available_field_modal.type + ' - must be (filter|column)');
        }

        // clear selected available fields
        available_field_checkbox_model.clearSelections();

        // remove modal
        available_field_modal.destroy();
    },

    onAvailableFieldCancel: function (cmp, event, eOpts) {
        var available_field_modal = this.getAvailableFieldModal();
        
        available_field_modal.close();
    },

    onAvailableFieldSearch: function (cmp, event, eOpts) {
        var available_field_by_category_store = this.getReportAvailableFieldsByCategoryStore(),
            available_field_search_box = this.getAvailableFieldSearchBox(),
            value = available_field_search_box.getValue();

        // clear store filters
        available_field_by_category_store.clearFilter();
        
        // filter store on value
        available_field_by_category_store.filter(Ext.create('PICS.ux.util.FilterMultipleColumn', {
            value: value
        }));
    },

    showAvailableFieldModal: function(type) {
        var available_field_by_category_store = this.getReportAvailableFieldsByCategoryStore();

        // clear store filters
        available_field_by_category_store.clearFilter();

        var available_field_modal = Ext.create('PICS.view.report.available-field.AvailableFieldModal', {
            defaultFocus: 'textfield[name=search_box]',
            type: type
        });

        available_field_modal.show();
    }
});