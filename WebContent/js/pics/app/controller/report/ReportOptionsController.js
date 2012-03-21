Ext.define('PICS.controller.report.ReportOptionsController', {
    extend: 'Ext.app.Controller',
    refs: [{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'reportOptionsColumnsGrid',
        selector: 'reportoptionscolumns grid'
    }, {
        ref: 'reportOptionsFiltersGrid',
        selector: 'reportoptionsfilters grid'
    }],
    stores: [
        'report.AvailableFieldsByCategory',
        'report.ReportsColumn',
        'report.ReportsFilter',
        'report.ReportsSort'
    ],
    
    init: function () {
        this.control({
            'reportoptions grid actioncolumn': {
                click: this.removeColumn
            },
            'reportoptionscolumns grid toolbar[dock=top] button[action=add-column]': {
                click: this.showColumnSelector
            },
            'reportoptionsfilters grid toolbar[dock=top] button[action=add-filter]': {
                click: this.showColumnSelector
            },
            'reportoptionssorts grid toolbar[dock=top] button[action=add-sort]': {
                click: this.showColumnSelector
            }
        });
    },
    
    removeColumn: function(view, cell, row, col, e) {
        var target = e.getTarget();
        
        if (target && typeof target.className == 'string') {
            if (target.className.search('remove-column|remove-filter|remove-sort') !== -1) {
                var grid = view.up('grid');
                var store;
                
                if (grid._column_type === 'filter') {
                    store = this.getReportReportsFilterStore();
                } else if (grid._column_type === 'column') {
                    store = this.getReportReportsColumnStore();
                } else if (grid._column_type === 'sort') {
                    store = this.getReportReportsSortStore();                
                } else {
                    throw 'grid._column_type is ' + grid._column_type + ' - must be (filter|column)';
                }
                
                grid.getSelectionModel().select(row, false);
                
                var selected = grid.getSelectionModel().getSelection(); 
                
                store.remove(selected);
                this.application.fireEvent('refreshreport');                
            }
        }
    },
    
    showColumnSelector: function(component, e, options) {
        var window = this.getReportColumnSelector();
        
        if (!window) {
            var grid = component.up('gridpanel');
            
            var store = this.getReportAvailableFieldsByCategoryStore();
            store.clearFilter();
            
            window = Ext.create('PICS.view.report.ColumnSelector');
            
            window._column_type = grid._column_type;
            window.show();
        }
    }
});