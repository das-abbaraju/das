Ext.define('PICS.model.report.Filter', {
    extend: 'Ext.data.Model',

    // TODO: change to 'Field'
    associations: [{
        type: 'hasOne',
        model: 'PICS.model.report.AvailableField',
        associationKey: 'field',
        getterName: 'getAvailableField',
        setterName: 'setAvailableField'
    }],
    fields: [{
        name: 'name',
        type: 'string'
    }, {
        name: 'operator',
        type: 'string'
    }, {
        name: 'column2',
        type: 'string'
    }, {
        name: 'value',
        type: 'string'
    }]
});