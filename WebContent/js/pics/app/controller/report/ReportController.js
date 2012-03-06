Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',

 	stores: [
        'report.ReportData', 'report.Reports', 'report.ReportsColumn', 'report.ReportsFilter'
    ],
	
    init: function() {
        this.control({
            "reportoptions button[action=refresh]": {
                click: this.refreshData
            },
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
        
        var reportStore = this.getReportReportsStore();
        reportStore.loadRawData({"report": reportParameters});
        
        this.refreshData();
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
            if (this.columnSelector.columntype === "filter") {
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
    refreshData: function() {
        var reportStore = this.getReportReportsStore();
        var report = reportStore.first();
        
        if (report == undefined) {
        	Ext.MessageBox.alert("Error", "Missing report definition");
        	return;
        }
        
        var columnStore = this.getReportReportsColumnStore();
        var columns = columnStore.data.items;
        console.log(columns);

        // Setup store fields for the report data
        var dataStore = this.getReportReportDataStore();
        
        var fields = [];
        for(var i = 0; i < columns.length; i++) {
        	var field = {};
        	var column = columns[i];
        	field.name = column.get("name");
        	// field.type = column.get("extType");
        	fields.push(field);
        }
        
        var model = Ext.define('PICS.model.report.ReportRow', {
            extend: 'Ext.data.Model',
            fields: fields
        });
        dataStore.proxy.reader.setModel(model);
        
        // console.log(dataStore);
//        console.log(this.getReportReportDataStore().model);
//        console.log(Ext.ModelManager.getModel("PICS.model.report.ReportRow"));
        
        // Setup the URL
		var url = "ReportDynamic!data.action?";
		if (report && report.getId() > 0) {
			url += "report=" + report.getId();
		} else {
			url += "report.base=" + report.get("base");
		}
        dataStore.proxy.url = url;

        // Run the report
        dataStore.load({
        	callback: function(records, operation, success) {
        		if (success) {
        			var dataGrid = Ext.getCmp("dataGrid");
        			dataGrid.headerCt.removeAll();
                    dataGrid.headerCt.add({"width":27,"xtype":"rownumberer"});
                    for(var i = 0; i < columns.length; i++) {
                    	var gridColumn = {};
                    	var column = columns[i];
                    	gridColumn.dataIndex = column.get("name");
                    	gridColumn.text = column.get("text");
                    	if (column.get("width") > 0)
                    		gridColumn.width = column.get("width");
                    	dataGrid.headerCt.add(gridColumn);
                    }
                    console.log(columns.length);
        		} else {
        			Ext.MessageBox.alert("Failed to read data from Server", "Reason: " + operation.error);
        		}
            }, scope: this
        });
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
        selected = grid[0].getSelectionModel().getSelection();
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
