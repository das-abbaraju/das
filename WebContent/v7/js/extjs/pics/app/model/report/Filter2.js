Ext.define('PICS.model.report.Filter2', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'type',
        type: 'string',
        persist: false
    }, {
        name: 'category',
        type: 'string',
        persist: false
    }, {
        name: 'name',
        type: 'string',
        persist: false
    }, {
        name: 'description',
        type: 'string',
        persist: false
    }, {
        name: 'operator',
        type: 'string'
    }, {
        name: 'value',
        type: 'string'
    }, {
        name: 'column_compare_id',
        type: 'string'
    }]    
});