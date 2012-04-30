Ext.define('PICS.model.report.Sort', {
    extend: 'Ext.data.Model',

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
        name: 'direction',
        type: 'string',
        defaultValue: 'ASC'
    }]
});