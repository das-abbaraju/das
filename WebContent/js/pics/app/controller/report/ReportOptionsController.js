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
        'report.ReportsFilter'
    ],
    
    init: function () {
        this.control({
            'reportoptions button[action=add]': {
                click: this.showColumnSelector
            },
            'reportoptions button[action=remove]': {
                click: this.removeColumn
            }
        });
    },
    
    removeColumn: function(component, e, options) {
        var store;
        var grid;
        
        if (component.column_type === 'filter') {
            store = this.getReportReportsFilterStore();
            grid = this.getReportOptionsFiltersGrid();
        } else if (component.column_type === 'column') {
            store = this.getReportReportsColumnStore();
            grid = this.getReportOptionsColumnsGrid();
        } else {
            throw 'columnSelector.column_type is ' + columnSelector.column_type + ' - must be (filter|column)';
        }
        
        var selected = grid.getSelectionModel().getSelection(); 
        
        store.remove(selected);
    },
    
    showColumnSelector: function(component, e, options) {
        var window = this.getReportColumnSelector();
        
        if (!window) {
            var store = this.getReportAvailableFieldsByCategoryStore();
            store.clearFilter();
            
            window = Ext.create('PICS.view.report.ColumnSelector');
            
            window.column_type = component.column_type;
            window.show();
        }
    }
});