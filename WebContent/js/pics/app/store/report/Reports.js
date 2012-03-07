Ext.define('PICS.store.report.Reports', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.Report',
	
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
    		field = fieldsStore.findField(item.get("name"));
    		
    		item.setField(field);
//    		if (field == null) {
//    			item.set('text', item.get("name"));
//    		} else {
//    			item.set('text', field.get("text"));
//    		}
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
        		// See http://docs.sencha.com/ext-js/4-0/source/Json3.html#Ext-data-writer-Json
        		// writeRecords
                request.params["report.parameters"] = 1;
                request.url = 'ReportDynamic!save.action?report=' + report.getId();
                return request;
        	}
        },
        type: 'ajax'
    }
});