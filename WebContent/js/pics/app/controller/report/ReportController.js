Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',
 	stores: [
        'report.ReportData',
        'report.Reports',
        'report.ReportsColumn',
        'report.ReportsFilter'
    ],
	
    init: function() {
        this.control({
            'reportoptions button[action=refresh]': {
                click: this.refreshData
            },
            'reportoptions button[action=save]': {
                click: this.save
            }
        });
        
        var reportStore = this.getReportReportsStore();
        
        reportStore.loadRawData({
            report: reportParameters
        });
        
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
		data.filters = this.addChildren(this.filterStore);
		// data.sorts = this.addChildren(this.sortsStore);
		
		delete data.id;
		delete data.modelType;
		delete data.summary;
		delete data.description;
		//request.params['report.name'] = data.name;
		//request.params['report.description'] = data.description;
		
		this.report.parameters = Ext.encode(data);
		return this.report.parameters;
    },
	addChildren: function(child) {
		var records = child.data.items;
		var data = [];
		for (var i = 0; i < records.length; i++) {
			data.push(this.getRecordData(records[i]));
        }
		return data;
	},
    getRecordData: function(record) {
    	var data = {};
    	record.fields.each(function(field){
            if (field.persist) {
                name = field['name'] || field.name;
                data[name] = record.get(field.name);
            }
        });
    	return data;
    },
    refreshData: function() {
        if (this.report == undefined) {
        	Ext.MessageBox.alert('Error', 'Missing report definition');
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
		var url = 'ReportDynamic!data.action?';
		
		if (this.report && this.report.getId() > 0) {
			url += 'report=' + this.report.getId();
		} else {
			url += 'report.base=' + this.report.get('base');
		}
		
		url += '&report.parameters=' + this.buildParameters();
        dataStore.proxy.url = url;

        // Run the report
        dataStore.load({
        	callback: function(records, operation, success) {
        		if (success) {
        			var dataGrid = Ext.getCmp('dataGrid');
        			var newColumns = [{
        			    xtype: 'rownumberer',
        			    width: 27
			        }];
        			
                    for(var i = 0; i < columns.length; i++) {
                    	newColumns.push(columns[i].toGridColumn());
                    }
                    
        			dataGrid.reconfigure(null, newColumns);
        		} else {
        			Ext.MessageBox.alert('Failed to read data from Server', 'Reason: ' + operation.error);
        		}
            }, scope: this
        });
    },
    save: function(button, e, options) {
        var reportStore = this.getReportReportsStore();
        this.buildParameters();
        this.report.setDirty();
        reportStore.sync();
        this.refreshData();
    }
});