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
            flex: 1,
            hideTrigger: true,
            minChars: 2,
            multiSelect: false,
            name: 'value',
            queryParam: 'searchQuery',
            valueField: 'key'
        };
    },

    updateValueFieldStore: function (filter) {
        var value = filter.get('value'),
            field_id = filter.get('field_id'),
            value_field = this.down('combobox');
        
        value_field.store = Ext.create('Ext.data.Store', {
            autoLoad: true,
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
        });
    }
});