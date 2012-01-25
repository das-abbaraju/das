Ext.define('PICS.model.report.SimpleFilter', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'field',
        type: 'string'
    }, {
        name: 'not',
        type: 'boolean'
    }, {
        name: 'operator',
        type: 'string'
    }, {
        name: 'field2',
        type: 'string'
    }, {
        name: 'value',
        type: 'string'
    }],
    
    belongsTo: 'Parameter'
});