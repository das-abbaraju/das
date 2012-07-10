Ext.define('PICS.model.report.Report', {
    extend: 'Ext.data.Model',
    requires: [
        'PICS.model.report.Column',
        'PICS.model.report.Filter',
        'PICS.model.report.Sort'
    ],
    
    fields: [{
        name: 'id',
        type: 'int'
    }, {
        name: 'modelType',
        type: 'string'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'filterExpression',
        type: 'string'
    }, {
        name: 'rowsPerPage',
        type: 'int'
    }],
    hasMany: [{
        model: 'PICS.model.report.Column', 
        name: 'columns'
    }, {
        model: 'PICS.model.report.Filter', 
        name: 'filters'
    }, {
        model: 'PICS.model.report.Sort', 
        name: 'sorts'
    }]
});