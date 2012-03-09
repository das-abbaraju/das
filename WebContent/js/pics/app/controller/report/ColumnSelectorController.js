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
        'report.ReportsFilter'
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
        var columnSelector = this.getColumnSelector();
        var columnSelectorGrid = this.getColumnSelectorGrid();
        
        var selected = columnSelectorGrid.getSelectionModel().getSelection();
        
        if (selected.length > 0) {
            var store;
            
            if (columnSelector.column_type === "filter") {
                store = this.getReportReportsFilterStore();
                Ext.Array.forEach(selected, function (field) {
                    store.add(field.createSimpleFilter());
                });
            } else if (columnSelector.column_type === "column") {
                store = this.getReportReportsColumnStore()
                Ext.Array.forEach(selected, function (field) {
                    store.add(field.createSimpleColumn());
                });
            } else {
                throw 'columnSelector.column_type is ' + columnSelector.column_type + ' - must be (filter|column)';
            }
        }
        
        columnSelector.destroy();
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
            
            if (window.column_type === 'filter') {
                store = this.getReportReportsFilterStore();
            } else if (window.column_type === 'column') {
                store = this.getReportReportsColumnStore();
            } else {
                throw 'columnSelector.column_type is ' + columnSelector.column_type + ' - must be (filter|column)';
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