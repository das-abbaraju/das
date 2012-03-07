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
        this.report = reportStore.first();
        this.columnStore = this.getReportReportsColumnStore();
        this.filterStore = this.getReportReportsFilterStore();
        
        this.refreshData();
    },
    
    report: null,
    columnStore: null,
    filterStore: null,
    sortsStore: null,
    
    buildParameters: function () {
		// See http://docs.sencha.com/ext-js/4-0/source/Writer.html#Ext-data-writer-Writer-method-write
		var data = this.getRecordData(this.report);
		
		data.columns = this.addChildren(this.columnStore);
		// data.sorts = this.addChildren(this.sortsStore);
		// data.filter = this.addChildren(this.filtersStore);
		
		delete data.id;
		delete data.modelType;
		delete data.summary;
		delete data.description;
		//request.params["report.name"] = data.name;
		//request.params["report.description"] = data.description;
		
		this.report.parameters = Ext.encode(data);
		return this.report.parameters;
    },
    getRecordData: function(record) {
    	var data = {};
    	record.fields.each(function(field){
            if (field.persist) {
                name = field["name"] || field.name;
                data[name] = record.get(field.name);
            }
        });
    	return data;
    },
	addChildren: function(child) {
		var records = child.data.items;
		var data = [];
		for (var i = 0; i < records.length; i++) {
			data.push(this.getRecordData(records[i]));
        }
		return data;
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
                store = this.report.filtersStore();
            } else {
                store = this.columnStore;
            }
            
            Ext.Array.forEach(selected, function(field) {
                store.add(field.createSimpleColumn());
            });
        }
    },
    refreshData: function() {
        if (this.report == undefined) {
        	Ext.MessageBox.alert("Error", "Missing report definition");
        	return;
        }
        
        var columns = this.columnStore.data.items;
        
        // Setup store fields for the report data
        var fields = [];
        Ext.Array.forEach(columns, function(col) {
        	fields.push(col.toStoreField());
        });
        
        var model = Ext.define('PICS.model.report.ReportRow', {
            extend: 'Ext.data.Model',
            fields: fields
        });
        var dataStore = this.getReportReportDataStore();
        dataStore.removeAll(true);
        dataStore.proxy.reader.setModel(model);
        
        // Setup the URL
        this.buildParameters();
		var url = "ReportDynamic!data.action?";
		if (this.report && this.report.getId() > 0) {
			url += "report=" + this.report.getId();
		} else {
			url += "report.base=" + this.report.get("base");
		}
		url += "&report.parameters=" + this.buildParameters();
        dataStore.proxy.url = url;

        // Run the report
        dataStore.load({
        	callback: function(records, operation, success) {
        		if (success) {
        			var dataGrid = Ext.getCmp("dataGrid");
        			var newColumns = [{"width":27,"xtype":"rownumberer"}];
                    for(var i = 0; i < columns.length; i++) {
                    	newColumns.push(columns[i].toGridColumn());
                    }
        			dataGrid.reconfigure(null, newColumns);
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
            store = this.columnStore;
            type = 'reportoptionscolumns';
        }
        grid = Ext.ComponentQuery.query(type +' gridpanel');
        selected = grid[0].getSelectionModel().getSelection();
        store.remove(selected);
    },
    saveReport: function(button, e, options) {
        var reportStore = this.getReportReportsStore();
        this.buildParameters();
        this.report.setDirty();
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
