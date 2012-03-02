Ext.define('PICS.store.report.ReportData', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.ReportRow',
	autoLoad : true,
	
	proxy : {
        type : 'ajax',
        url : reportURL,
        reader : {
            type : 'json',
            root : 'data'
        }
    }
});