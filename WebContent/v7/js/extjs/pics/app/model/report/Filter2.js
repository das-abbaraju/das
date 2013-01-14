Ext.define('PICS.model.report.Filter2', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'report_id',
        type: 'int'
    }, {
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
    }],

    associations: [{
        type: 'belongsTo',
        model: 'PICS.model.report.Report2',
        getterName: 'getReport',
        setterName: 'setReport'
    }]
});