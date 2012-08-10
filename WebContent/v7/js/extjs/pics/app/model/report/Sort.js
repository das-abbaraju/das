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
        // sort name
        name: 'name',
        type: 'string'
    }, {
        // sort direction aka ASC, DESC
        name: 'direction',
        type: 'string',
        defaultValue: 'ASC'
    }]
});