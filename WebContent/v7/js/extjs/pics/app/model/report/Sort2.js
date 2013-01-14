Ext.define('PICS.model.report.Sort2', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'report_id',
        type: 'int'
    }, {
        name: 'id',
        type: 'string'
    }, {
        name: 'direction',
        type: 'string'
    }],

    associations: [{
        type: 'belongsTo',
        model: 'PICS.model.report.Report2',
        getterName: 'getReport',
        setterName: 'setReport'
    }]
});