Ext.define('PICS.model.report.SimpleFilter', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'column',
        type: 'string'
    }, {
        name: 'not',
        type: 'boolean'
    }, {
        name: 'operator',
        type: 'string'
    }, {
        name: 'column2',
        type: 'string'
    }, {
        name: 'value',
        type: 'string'
    }],
    
    belongsTo: 'Report'
});