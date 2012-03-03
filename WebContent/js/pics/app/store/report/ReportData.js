Ext.define('PICS.store.report.ReportData', {
	extend: 'Ext.data.Store',
	model: 'PICS.model.report.ReportRow',
	
	autoLoad: true,
	proxy: {
	    reader: {
            root: 'data',
            type: 'json'
        },
        type: 'ajax',
        url: reportURL
    }
});