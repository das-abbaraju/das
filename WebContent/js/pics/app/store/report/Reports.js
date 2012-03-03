Ext.define('PICS.store.report.Reports', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.Report',
	
	autoLoad: true,
    proxy: {
        api: {
            read: 'js/pics/data/report.json',
            update: 'ReportDynamic.action?report=' + reportID
        },
        reader: {
            root: 'report',
            type: 'json'
        },
        type: 'ajax'
    }
});