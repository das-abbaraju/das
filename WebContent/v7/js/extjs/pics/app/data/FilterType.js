Ext.define('PICS.data.FilterType', {
    statics: (function () {
        var types = {
            AccountID: 'AccountID',
            Autocomplete: 'Autocomplete',
            Boolean: 'Boolean',
            Date: 'Date',
            Multiselect: 'Multiselect',
            Number: 'Number',
            String: 'String',
            UserID: 'UserID'
        };
        
        return {
            getTypes: function () {
                return types;
            },
            
            AccountID: types.AccountID,
            Autocomplete: types.Autocomplete,
            Boolean: types.Boolean,
            Date: types.Date,
            Multiselect: types.Multiselect,
            Number: types.Number,
            String: types.String,
            UserID: types.UserID
        };
    }())
});