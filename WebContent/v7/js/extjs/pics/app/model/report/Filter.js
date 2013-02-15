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
            var type = record.get('type');
            
            if (value == null) {
                return '';
            }
            
            switch (type) {
                case PICS.data.FilterType.Date:
                    value = Ext.Date.format(value, 'Y-m-d') || value;
                    
                    break;
                case PICS.data.FilterType.Multiselect:
                    if (value instanceof Array) {
                        value = value.join(', ');
                    }
                    
                    break;
                case PICS.data.FilterType.AccountID:
                case PICS.data.FilterType.Boolean:
                case PICS.data.FilterType.Number:
                case PICS.data.FilterType.UserID:
                    // flatten all values return into strings instead of overriding Ext.form.Basic.getFieldValues
                    value = value.toString();
                    
                    break;
                case PICS.data.FilterType.Autocomplete:
                case PICS.data.FilterType.String:
                default:
                    // no conversion necessary
                    break;
            }
            
            return value;
        },
        type: 'string',
        useNull: true
    }, {
        name: 'column_compare_id',
        type: 'string',
        useNull: true
    }]
});