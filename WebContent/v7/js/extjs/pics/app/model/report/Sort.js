Ext.define('PICS.model.report.Sort', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'field_id',
        type: 'string'
    }, {
        name: 'direction',
        type: 'string',
        defaultValue: 'ASC'
    }]
});