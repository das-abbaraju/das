Ext.define('PICS.controller.report.ColumnSelectorController', {
    extend: 'Ext.app.Controller',
    refs: [{
        ref: 'columnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'columnSelectorGrid',
        selector: 'reportcolumnselectorgrid'
    }, {
        ref: 'hideColumnCheckbox',
        selector: 'reportcolumnselector toolbar[dock=top] checkbox'
    }, {
        ref: 'searchField',
        selector: 'reportcolumnselector toolbar[dock=top] textfield'
    }],
    stores: [
        'report.AvailableFieldsByCategory',
        'report.Reports'
    ],

    init: function () {
        var me = this;

        this.control({
            'reportcolumnselector button[action=add]':  {
                click: this.addColumnToReportOptionsColumns
            },
            'reportcolumnselector button[action=close]':  {
                click: function () {
                    this.getColumnSelector().close();
                }
            },
            'reportcolumnselector toolbar[dock=top] checkbox': {
                change: function (component, newValue, oldValue, eOpts) {
                    var store = this.getReportAvailableFieldsByCategoryStore();

                    store.clearFilter();

                    me.toggleSelectedColumns();
                    me.search();
                }
            },
            'reportcolumnselector toolbar[dock=top] textfield': {
                keyup: function (component, e, eOpts) {
                    var store = this.getReportAvailableFieldsByCategoryStore();

                    store.clearFilter();

                    me.toggleSelectedColumns();
                    me.search();
                }
            },
            'reportcolumnselectorgrid': {
                beforerender: function (component, eOpts) {
                    me.toggleSelectedColumns();
                }
            }
        });
    },

    addColumnToReportOptionsColumns: function(component, e, eOpts) {
        var grid = this.getColumnSelectorGrid();
        var selected = grid.getSelectionModel().getSelection();
        var window = this.getColumnSelector();

        if (selected.length > 0) {
            var report = this.getReportReportsStore().first();
            
            if (window._column_type === "column") {
                var column_store = report.columns();
                
                Ext.Array.forEach(selected, function (field) {
                    column_store.add(field.createColumn());
                });
                
                this.application.fireEvent('refreshreport');
            } else if (window._column_type === "filter") {
                var filter_store = report.filters();
                
                Ext.Array.forEach(selected, function (field) {
                    filter_store.add(field.createFilter());
                });
                
                this.application.fireEvent('refreshfilters');
            } else {
                throw 'columnSelector.column_type is ' + window.column_type + ' - must be (filter|column)';
            }
        }
        
        window.destroy();
    },

    search: function () {
        var available_field_store = this.getReportAvailableFieldsByCategoryStore();
        var search_field = this.getSearchField();

        available_field_store.filter(Ext.create('Ext.ux.util.FilterMultipleColumn', {
            property: [
                'category',
                'text'
            ],
            value: search_field.getValue(),
            anyMatch: true,
            root: 'data'
        }));
    },

    toggleSelectedColumns: function () {
        var available_field_store = this.getReportAvailableFieldsByCategoryStore();
        var hide_column_checkbox = this.getHideColumnCheckbox();

        if (hide_column_checkbox.checked) {
            var report = this.getReportReportsStore().first();
            var selected_columns = [];
            var store;
            var window = this.getColumnSelector();

            if (window._column_type === 'column') {
                store = report.columns();
            } else if (window._column_type === 'filter') {
                store = report.filters();
            } else if (window._column_type === 'sort') {
                store = report.sorts();
            } else {
                throw 'columnSelector.column_type is ' + window.column_type + ' - must be (filter|column)';
            }

            // build a list of selected column, filter, or sorts
            store.each(function (record) {
                selected_columns.push(record.get('name'));
            });

            // filter the available field store by the built list
            available_field_store.filter(function (item) {
                return selected_columns.indexOf(item.get('name')) == -1;
            });
        }
    }
});