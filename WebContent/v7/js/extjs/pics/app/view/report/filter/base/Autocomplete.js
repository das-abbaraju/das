Ext.define('PICS.view.report.filter.base.Autocomplete', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbaseautocomplete',
    
    cls: 'autocomplete',
    
    createOperatorField: function () {
        return {
            xtype: 'hiddenfield',
            name: 'operator'
        };
    },

    createValueField: function () {
        return {
            xtype: 'combobox',
            displayField: 'value',
            editable: true,
            hideTrigger: true,
            minChars: 2,
            multiSelect: false,
            name: 'value',
            queryParam: 'searchQuery',
            valueField: 'key',
            width: 258
        };
    },
    
    updateValueFieldStore: function (field_id) {
        var value_field = this.down('combobox');
        
        value_field.store = {
            fields: [{
                name: 'key',
                type: 'string'
            }, {
                name: 'value',
                type: 'string'
            }],
            proxy: {
                type: 'ajax',
                url: 'Autocompleter.action?fieldType=' + field_id,
                reader: {
                    root: 'result',
                    type: 'json'
                }
            }
        };
    }
});