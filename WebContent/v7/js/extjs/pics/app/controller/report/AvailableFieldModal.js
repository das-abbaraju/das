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
        var list = this.getAvailableFieldList(),
            selected = list.getSelectionModel().getSelection();

        if (selected.length > 0) {
            var column_store = this.getReportReportsStore().first().columns();

            Ext.Array.forEach(selected, function (field) {
                column_store.add(field.toColumn());
            });

            this.application.fireEvent('refreshreport');
        }
    },

    addFilterToReport: function(cmp, event, eOpts) {
        var list = this.getAvailableFieldList(),
            selected = list.getSelectionModel().getSelection();

        if (selected.length > 0) {
            var filter_store = this.getReportReportsStore().first().filters();

            Ext.Array.forEach(selected, function (field) {
                filter_store.add(field.toFilter());
            });

            this.application.fireEvent('refreshfilters');
        }
    },

    onAvailableFieldAdd: function (cmp, event, eOpts) {
        var modal = this.getAvailableFieldModal(),
            list = this.getAvailableFieldList(),
            search_box = this.getAvailableFieldSearchBox(),
            type = modal.type;

        if (type === 'column') {
            this.addColumnToReport();
        } else if (type === 'filter') {
            this.addFilterToReport();
        } else {
            Ext.Error.raise('Invalid type:' + modal.type + ' - must be (filter|column)');
        }

        list.getSelectionModel().clearSelections();

        modal.destroy();
    },

    onAvailableFieldCancel: function (cmp, event, eOpts) {
        this.getAvailableFieldModal().close();
    },

    onAvailableFieldSearch: function (cmp, event, eOpts) {
        var store = this.getReportAvailableFieldsByCategoryStore(),
            search_box = this.getAvailableFieldSearchBox();

        store.clearFilter();
        store.filter(Ext.create('PICS.ux.util.FilterMultipleColumn', {
            anyMatch: true,
            property: [
                'category',
                'text'
            ],
            root: 'data',
            value: search_box.getValue()
        }));
    },

    showAvailableFieldModal: function(type) {
        var store = this.getReportAvailableFieldsByCategoryStore();

        store.clearFilter();
        store.sort();

        var modal = Ext.create('PICS.view.report.available-field.AvailableFieldModal', {
            defaultFocus: 'textfield[name=search_box]',
            type: type
        });

        modal.show();
    }
});