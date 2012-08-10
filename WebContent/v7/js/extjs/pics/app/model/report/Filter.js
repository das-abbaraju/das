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
        // filter name
        name: 'name',
        type: 'string'
    }, {
        // filter operator aka contains, starts with, =, !=, etc.
        name: 'operator',
        type: 'string'
    }, {
        // filter value
        name: 'value',
        type: 'string'
    }]
});