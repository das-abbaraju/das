Ext.define('PICS.store.report.ReportData', {
	extend: 'Ext.data.Store',
	
	fields: [],
	proxy: {
	    reader: {
	    	messageProperty: 'message',
            root: 'data',
            type: 'json'
        },
        type: 'ajax'
    }
});