Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id'
    }, {
        name: 'modelType'
    }, {
        name: 'summary' // Custom title for report
    }, {
        name: 'description'
    }],
    
    belongsTo: {
        modal: 'Parameter',
        associationKey: 'parameters'
    },
    
    proxy: {
        type: 'ajax',
        url: 'ReportDynamic.action?report=' + reportID,
        reader: {
            type: 'json',
            root: 'report'
        }
    }
});