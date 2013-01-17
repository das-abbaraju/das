Ext.define('PICS.model.report.Filter', {
    extend: 'Ext.data.Model',

    fields: [/*{
        name: 'report_id',
        type: 'int'
    }, */{
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
        type: 'string',
        useNull: true
    }, {
        name: 'value',
        type: 'string',
        useNull: true
    }, {
        name: 'column_compare_id',
        type: 'string',
        useNull: true
    }],

    associations: [{
        type: 'belongsTo',
        model: 'PICS.model.report.Report',
        getterName: 'getReport',
        setterName: 'setReport'
    }]
});