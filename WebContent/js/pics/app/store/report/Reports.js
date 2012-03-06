Ext.define('PICS.store.report.Reports', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.Report',
	
	autoLoad: true,
	listeners: {
        load: {
            fn: function(store, records, successful, operation, options) {
            	var colStore = Ext.StoreManager.get("report.ReportsColumn"),
                data = null,
            	fieldsStore = Ext.StoreManager.get("report.AvailableFields"),
            	filterStore = Ext.StoreManager.get("report.ReportsFilter"),            	
            	report = store.first(),
            	records = [];

            	data = report.columns().data;
            	for(i = 0; i < data.length; i++) {
            		var item = data.items[i],
            		field = fieldsStore.getField(item.get("name"));
            		
            		if (field == null) {
            			item.set('text', item.get("name"));
            		} else {
            			item.set('text', field.get("text"));
            		}
            		records.push(item);
            	}
            	colStore.loadRecords(records);
            	filterStore.loadRecords(records);
            }
        }
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
        		
        		// See http://docs.sencha.com/ext-js/4-0/source/Json3.html#Ext-data-writer-Json
        		// writeRecords
        		request.url = 'ReportDynamic!save.action?report=' + report.getId();
                request.jsonData = request.jsonData || {};
                request.jsonData["report"] = data;
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