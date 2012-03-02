Ext.define('PICS.store.report.AvailableFields', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.AvailableField',
	autoLoad: true,
	
	proxy: {
        type: 'ajax',
        url: 'ReportDynamic!availableFields.action?report=7',
        reader: {
            type: 'json',
            root: 'fields'
        }
    }
});