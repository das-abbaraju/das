Ext.define('PICS.store.report.Reports', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.Report',
	autoLoad: true,
	
    proxy: {
        type: 'ajax',
        api: {
            read: 'js/pics/data/report.json',
            update: 'ReportDynamic.action?report=' + reportID
        },
        reader: {
            type: 'json',
            root: 'report'
        }
    }
});