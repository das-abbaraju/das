Ext.define('PICS.store.report.Reports', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.Report',
	
	listeners: {
        load: {
            fn: function(store, records, successful, operation, options) {
            	var report = store.first();
            	
            	if (report) {
            		this.loadStoreColumn('report.ReportsColumn', report.columns());
            		this.loadStoreFilter('report.ReportsFilter', report.filters());
            		this.loadStoreSort('report.ReportsSort', report.sorts());            		
            	}
            }
        }
    },
    
    loadStoreColumn: function(store_name, child) {
    	var available_fields_store = Ext.StoreManager.get('report.AvailableFields');
    	
    	var records = [];
    	for(i = 0; i < child.data.length; i++) {
    		var item = child.data.items[i],
    		field = available_fields_store.findField(item.get('name'));
    		
    		item.setAvailableField(field);
    		records.push(item);
    	}
    	
    	var store = Ext.StoreManager.get(store_name);
    	store.loadRecords(records);
    },
    
    loadStoreFilter: function(store_name, child) {
    	// TODO refactor these two methods
    	var available_fields_store = Ext.StoreManager.get('report.AvailableFields');
    	var records = [];
    	
    	for(i = 0; i < child.data.length; i++) {
    		var item = child.data.items[i],
    		field = available_fields_store.findField(item.get('column'));
    		
    		item.setAvailableField(field);
    		records.push(item);
    	}
    	
    	var store = Ext.StoreManager.get(store_name);
    	store.loadRecords(records);
    },
    loadStoreSort: function(store_name, child) {
        // TODO refactor these two methods
        var available_fields_store = Ext.StoreManager.get('report.AvailableFields');
        var records = [];
        
        for(i = 0; i < child.data.length; i++) {
            var item = child.data.items[i],
            field = available_fields_store.findField(item.get('column'));
            
            item.setAvailableField(field);
            records.push(item);
        }
        
        var store = Ext.StoreManager.get(store_name);
        store.loadRecords(records);
    },    
    proxy: {
        // TODO: refactor proxy + figure out better writer
        // url parameter is important and must be null????
        // create proxy reader and writer on the fly???
        // writer.base????
        // better way of overriding request.params + url object? blind dependency
    	url: '',
        reader: {
            root: 'report',
            type: 'json'
        },
        writer: {
        	xtype: 'writer.base',
        	write: function(request) {
        		// See http://docs.sencha.com/ext-js/4-0/source/Json3.html#Ext-data-writer-Json
        		// writeRecords
        		var report = request.records[0];
                request.params['report.parameters'] = report.parameters;
                request.url = 'ReportDynamic!save.action?report=' + report.getId();
                return request;
        	}
        },
        type: 'ajax'
    }
});