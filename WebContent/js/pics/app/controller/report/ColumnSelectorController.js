Ext.define('PICS.controller.report.ColumnSelectorController', {
    extend: 'Ext.app.Controller',
    
    refs: [{
        ref: 'columnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'columnSelectorGrid',
        selector: 'reportcolumnselectorgrid'
    }, {
        ref: 'dataSetGrid',
        selector: 'reportdatasetgrid'
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
            'reportcolumnselector button[action=close]':  {
                click: function () {
                    this.getColumnSelector().close();
                }
            },
            'reportcolumnselector button[action=add]':  {
                click: function () {
                    var window = this.getColumnSelector();
                    
                    if (window.columnSelectorType === 'column') {
                        me.addColumnToReport();
                    } else if (window.columnSelectorType === 'filter') {
                        me.addFilterToReport();
                    } else {
                        throw 'columnSelector.type is ' + window.columnSelectorType + ' - must be (filter|column)';
                    }

                    window.destroy();
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

        this.application.on({
            showcolumnselector: this.showColumnSelector,
            scope: this
        });
    },

    addColumnToReport: function(component, e, eOpts) {
        var grid = this.getColumnSelectorGrid(),
            selected = grid.getSelectionModel().getSelection();

        if (selected.length > 0) {
            var column_store = this.getReportReportsStore().first().columns();

            Ext.Array.forEach(selected, function (field) {
                column_store.add(field.createColumn());
            });

            this.application.fireEvent('refreshreport');
        }
    },

    addFilterToReport: function(component, e, eOpts) {
        var grid = this.getColumnSelectorGrid(),
            selected = grid.getSelectionModel().getSelection();

        if (selected.length > 0) {
            var filter_store = this.getReportReportsStore().first().filters();

            Ext.Array.forEach(selected, function (field) {
                filter_store.add(field.createFilter());
            });

            this.application.fireEvent('refreshfilters');
        }
    },
   
    search: function () {
        var available_field_store = this.getReportAvailableFieldsByCategoryStore(),
            search_field = this.getSearchField();

        available_field_store.filter(Ext.create('Ext.ux.util.FilterMultipleColumn', {
            anyMatch: true,
            property: [
                'category',
                'text'
            ],
            root: 'data',
            value: search_field.getValue()
        }));
    },
    
    showColumnSelector: function(component, e, options) {
        var me = this,
            window = this.getColumnSelector();

        if (!window) {
            var store = this.getReportAvailableFieldsByCategoryStore();
            
            store.clearFilter();

            window = Ext.create('PICS.view.report.ColumnSelector', {
                columnSelectorType: component.columnSelectorType
            });
            
            window.show();
        }
    },

    toggleSelectedColumns: function () {
        var available_field_store = this.getReportAvailableFieldsByCategoryStore(),
            hide_column_checkbox = this.getHideColumnCheckbox();

        if (hide_column_checkbox.checked) {
            var report = this.getReportReportsStore().first(),
                selected_columns = [],
                store = '',
                window = this.getColumnSelector();
                
            if (window.columnSelectorType === 'column') {
                store = report.columns();
            } else if (window.columnSelectorType === 'filter') {
                store = report.filters();
            } else {
                throw 'columnSelector.type is ' + window.columnSelectorType + ' - must be (filter|column)';
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