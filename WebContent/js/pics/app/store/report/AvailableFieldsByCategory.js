Ext.define('PICS.store.report.AvailableFieldsByCategory', {
    extend : 'Ext.data.Store',
    model : 'PICS.model.report.AvailableField',
    
	autoLoad: false,
	data: availableFields,
    groupField: 'category',
    proxy: {
        reader: {
            root: 'fields',
            type: 'json'
        },
        type: 'ajax'
    }
});