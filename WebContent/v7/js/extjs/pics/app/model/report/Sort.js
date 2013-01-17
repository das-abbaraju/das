Ext.define('PICS.model.report.Sort', {
    extend: 'Ext.data.Model',

    fields: [/*{
        name: 'report_id',
        type: 'int'
    }, */{
        name: 'id',
        type: 'string'
    }, {
        name: 'direction',
        type: 'string',
        defaultValue: 'ASC'
    }],

    associations: [{
        type: 'belongsTo',
        model: 'PICS.model.report.Report',
        getterName: 'getReport',
        setterName: 'setReport'
    }]
});