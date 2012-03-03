Ext.define('PICS.store.report.AvailableFields', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.AvailableField',
	
	autoLoad: true,
	proxy: {
	    reader: {
            root: 'fields',
            type: 'json'
        },
        type: 'ajax',
        url: 'ReportDynamic!availableFields.action?report=7'
    }
});