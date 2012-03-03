Ext.define('PICS.store.report.AvailableFieldsByCategory', {
    extend : 'Ext.data.Store',
    model : 'PICS.model.report.AvailableField',
    
    autoLoad: true,
    groupField: 'category',
    proxy: {
        reader: {
            root: 'fields',
            type: 'json'
        },
        type: 'ajax',
        url: 'ReportDynamic!availableFields.action?report=7'
    }
});