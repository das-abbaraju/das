Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',

 	stores: [
        'report.Reports', 'report.ReportsColumn'
    ],
	
    init: function() {
        this.control({
            "reportoptions button[action=save]": {
                click: this.saveReport
            },
            "reportoptions button[action=add]": {
                click: this.showColumnSelector
            },
            "reportcolumnselector button[action=add]":  {
            	click: this.addColumn
            }
        });
    },
    addColumn: function(button, e, options) {
        var grid = Ext.ComponentQuery.query('reportcolumnselectorgrid');
        if (grid.length > 0) {
        	// Not sure why we might have two
        	grid = grid[0];
        }
        var selected = grid.getSelectionModel().getSelection();
        if (selected.length > 0) {
            var store = this.getReportReportsColumnStore();
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
    saveReport: function(button, e, options) {
        var reportStore = this.getReportReportsStore();
        reportStore.first().setDirty();
        reportStore.sync();
    },
    columnSelector: null,
    showColumnSelector: function(button, e, options) {
    	console.log(button);
        var window = Ext.ComponentQuery.query('reportcolumnselector');
        
        if (!window.length) {
            var window = Ext.create('PICS.view.report.ColumnSelector');
        } else {
            window = window[0];
        }
        
        window.show();
        
        this.columnSelector = window;
    }
});
