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
        'report.ReportsColumn',
        'report.ReportsFilter',
        'report.ReportsSort'        
    ],
    
    init: function () {
        var me = this;
        
        this.control({
            'reportcolumnselector button[action=add]':  {
                click: this.addColumnToReportOptionsColumns
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
        var window = this.getColumnSelector();
        var grid = this.getColumnSelectorGrid();
        
        var selected = grid.getSelectionModel().getSelection();
        
        if (selected.length > 0) {
            var store;
            if (window._column_type === "filter") {
                store = this.getReportReportsFilterStore();
                colStore = this.getReportReportsColumnStore();
                Ext.Array.forEach(selected, function (field) {
                    store.add(field.createSimpleFilter());
                    if (colStore.findRecord("name", field.get('name')) === null) {
                        colStore.add(field.createSimpleColumn());
                    }
                });
            } else if (window._column_type === "column") {
                store = this.getReportReportsColumnStore();
                Ext.Array.forEach(selected, function (field) {
                    store.add(field.createSimpleColumn());
                });
            } else if (window._column_type === "sort") {
                store = this.getReportReportsSortStore();
                colStore = this.getReportReportsColumnStore();
                Ext.Array.forEach(selected, function (field) {
                    store.add(field.createSimpleSort());
                    if (colStore.findRecord("name", field.get('name')) === null) {
                        colStore.add(field.createSimpleColumn());
                    }
                });
            } else {
                throw 'columnSelector.column_type is ' + window.column_type + ' - must be (filter|column)';
            }
        }
        
        this.application.fireEvent('refreshreport');
        
        window.destroy();
    },
    
    search: function () {
        var store = this.getReportAvailableFieldsByCategoryStore();
        var search = this.getSearchField();
        
        store.filter(Ext.create('Ext.ux.util.FilterMultipleColumn', {
            property: [
                'category',
                'text'
            ],
            value: search.getValue(),
            anyMatch: true,
            root: 'data'
        }));
    },
    
    toggleSelectedColumns: function () {
        var field_store = this.getReportAvailableFieldsByCategoryStore();
        var hide_column_checkbox = this.getHideColumnCheckbox();
        
        if (hide_column_checkbox.checked) {
            var window = this.getColumnSelector();
            
            if (window._column_type === 'filter') {
                store = this.getReportReportsFilterStore();
            } else if (window._column_type === 'column') {
                store = this.getReportReportsColumnStore();
            } else if (window._column_type === 'sort') {
                store = this.getReportReportsSortStore();                
            } else {
                throw 'columnSelector.column_type is ' + window.column_type + ' - must be (filter|column)';
            }
            
            var columns = [];
            
            store.each(function (record) {
                columns.push(record.get('name'));
            });
            
            field_store.filter(function (item) {
                return columns.indexOf(item.get('name')) == -1;
            });
        }
    }
});