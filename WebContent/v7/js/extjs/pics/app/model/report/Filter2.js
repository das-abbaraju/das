Ext.define('PICS.model.report.Filter2', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'string'
    }, {
        name: 'type',
        type: 'string'
    }, {
        name: 'category',
        type: 'string'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'description',
        type: 'string'
    }, {
        name: 'operator',
        type: 'string'
    }, {
        name: 'value',
        type: 'string'
    }, {
        name: 'column_compare_id',
        type: 'string'
    }
    ]
});