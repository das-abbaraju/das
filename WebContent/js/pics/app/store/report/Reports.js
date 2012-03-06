Ext.define('PICS.store.report.Reports', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.Report',
	
	// autoLoad: true,
	listeners: {
        load: {
            fn: function(store, records, successful, operation, options) {
            	var report = store.first();
            	
            	if (report) {
            		this.loadStore("report.ReportsColumn", report.columns());
            		this.loadStore("report.ReportsFilter", report.filters());
            	}
            }
        }
    },
    loadStore: function(storeName, child) {
    	var fieldsStore = Ext.StoreManager.get("report.AvailableFields");
    	
    	var records = [];
    	for(i = 0; i < child.data.length; i++) {
    		var item = child.data.items[i],
    		field = fieldsStore.getField(item.get("name"));
    		
    		if (field == null) {
    			item.set('text', item.get("name"));
    		} else {
    			item.set('text', field.get("text"));
    		}
    		records.push(item);
    	}
    	
    	var store = Ext.StoreManager.get(storeName);
    	store.loadRecords(records);
    },
    proxy: {
        url: 'js/pics/data/report.json',
        reader: {
            root: 'report',
            type: 'json'
        },
        writer: {
        	xtype: 'writer.base',
        	write: function(request) {
        		// See http://docs.sencha.com/ext-js/4-0/source/Writer.html#Ext-data-writer-Writer-method-write
        		var report = request.operation.records[0];
        		var data = this.getRecordData(report);
        		
        		data.columns = this.addChildren(report.columnsStore);
        		data.sorts = this.addChildren(report.sortsStore);
        		data.filter = this.addChildren(report.filtersStore);
        		
        		delete data.id;
        		delete data.modelType;
        		request.params["report.name"] = data.name;
        		delete data.summary;
        		request.params["report.description"] = data.description;
        		delete data.description;
        		
        		// See http://docs.sencha.com/ext-js/4-0/source/Json3.html#Ext-data-writer-Json
        		// writeRecords
                request.params["report.parameters"] = Ext.encode(data);
                request.url = 'ReportDynamic!save.action?report=' + report.getId();
                return request;
        	},
        	addChildren: function(child) {
        		var records = child.data.items,
        		len = records.length,
        		i = 0;

        		var data = [];
        		for (; i < len; i++) {
        			data.push(this.getRecordData(records[i]));
                }
        		return data;
        	}
        },
        type: 'ajax'
    }
});