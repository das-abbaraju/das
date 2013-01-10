Ext.define('PICS.model.report.Sort2', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'id',
        type: 'string'
    }, {
        name: 'direction',
        type: 'string'
    }],

    mutableFields: ['id','direction']
});