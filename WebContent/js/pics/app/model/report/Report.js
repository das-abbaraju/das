Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',
    requires: [
        'PICS.model.report.SimpleField',
        'PICS.model.report.SimpleFilter',
        'PICS.model.report.SimpleSort'
    ],

    fields: [
        { name: 'id', type: 'int' },
        { name: 'modelType', type: 'string' },
        { name: 'summary', type: 'string' },
        { name: 'description', type: 'string' }
    ],
    
    hasMany: [{
        model: 'PICS.model.report.SimpleField', 
        name: 'columns'
    }, {
        model: 'PICS.model.report.SimpleFilter', 
        name: 'filters'
    }, {
        model: 'PICS.model.report.SimpleSort', 
        name: 'sorts'
    }],
    
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