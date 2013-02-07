Ext.define('PICS.model.report.Filter', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'field_id',
        type: 'string'
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
        type: 'string',
        useNull: true
    }, {
        name: 'value',
        convert: function (value, record) {
            if (value instanceof Array) {
                value = value.join(', ');
            }
            
            if (value == null) {
                value = '';
            }
            
            // flatten all values return into strings instead of overriding Ext.form.Basic.getFieldValues
            return value.toString();
        },
        type: 'string',
        useNull: true
    }, {
        name: 'column_compare_id',
        type: 'string',
        useNull: true
    }]
});