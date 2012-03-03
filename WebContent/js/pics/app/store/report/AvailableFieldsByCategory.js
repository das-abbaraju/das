Ext.define('PICS.store.report.AvailableFieldsByCategory', {
    extend : 'Ext.data.Store',
    model : 'PICS.model.report.AvailableField',
    autoLoad: true,
    
    groupField: 'category',
    
    proxy: {
        type: 'ajax',
        url: 'ReportDynamic!availableFields.action?report=7',
        reader: {
            type: 'json',
            root: 'fields'
        }
    }
});