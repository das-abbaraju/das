Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',

 	stores: [
        'report.Reports', 'report.ReportsColumn', 'report.ReportsFilter'
    ],
	
    init: function() {
        this.control({
            "reportoptions button[action=save]": {
                click: this.saveReport
            },
            "reportoptions button[action=add]": {
                click: this.showColumnSelector
            },
            "reportoptions button[action=remove]": {
                click: this.removeColumn
            },
            "reportcolumnselector button[action=add]":  {
            	click: this.addColumn
            }
        });
    },
    addColumn: function(button, e, options) {
        var grid = Ext.ComponentQuery.query('reportcolumnselectorgrid'),
        store = null;
        
        if (grid.length > 0) {
        	// Not sure why we might have two
        	grid = grid[0];
        }
        var selected = grid.getSelectionModel().getSelection();
        if (selected.length > 0) {
            if (this.columnSelector.columntype === "filter"){
                store = this.getReportReportsFilterStore();
            } else {
                store = this.getReportReportsColumnStore();    
            }
            
            for(var i=0; i < selected.length; i++) {
            	var field = selected[i];
                var column = Ext.create('PICS.model.report.SimpleColumn', {
                	'name': field.get("name"),
                	'text': field.get("text")
                });
                store.add(column);
            }
        }
    },
    removeColumn: function(button, e, options) {
        var grid = null,
        store = null,
        type = null;
        
        if (button.columntype === 'filter') {
            store = this.getReportReportsFilterStore();
            type = 'reportoptionsfilters';
        } else {
            store = this.getReportReportsColumnStore();            
            type = 'reportoptionscolumns';
        }
        grid = Ext.ComponentQuery.query(type +' gridpanel');
        selected = grid[0].getSelectionModel().getSelection()
        store.remove(selected);
    },
    saveReport: function(button, e, options) {
        var reportStore = this.getReportReportsStore();
        reportStore.first().setDirty();
        reportStore.sync();
    },
    columnSelector: null,
    showColumnSelector: function(button, e, options) {
        var window = Ext.ComponentQuery.query('reportcolumnselector');
        
        if (!window.length) {
            var window = Ext.create('PICS.view.report.ColumnSelector');
        } else {
            window = window[0];
        }
        
        window.show();
        
        this.columnSelector = window;
        this.columnSelector.columntype = button.columntype;
    }
});
