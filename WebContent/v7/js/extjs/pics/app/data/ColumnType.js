Ext.define('PICS.data.ColumnType', {
    statics: (function () {
        var types = {
            Boolean: 'Boolean',
            Flag: 'Flag',
            Number: 'Number',
            String: 'String'
        };
        
        return {
            getTypes: function () {
                return types;
            },
            
            Boolean: types.Boolean,
            Flag: types.Flag,
            Number: types.Number,
            String: types.String
        };
    }())
});