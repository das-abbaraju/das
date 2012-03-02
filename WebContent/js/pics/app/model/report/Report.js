Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',
    requires: [
        'PICS.model.report.SimpleColumn',
        'PICS.model.report.SimpleFilter',
        'PICS.model.report.SimpleSort'
    ],

    fields: [
        { name: 'id', type: 'int' },
        { name: 'modelType', type: 'string' },
        { name: 'summary', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'filterExpression', type: 'string' },
        { name: 'rowsPerPage', type: 'int', defaultValue: 100 }
    ],
    
    hasMany: [{
        model: 'PICS.model.report.SimpleColumn', 
        name: 'columns'
    }, {
        model: 'PICS.model.report.SimpleFilter', 
        name: 'filters'
    }, {
        model: 'PICS.model.report.SimpleSort', 
        name: 'sorts'
    }]
});