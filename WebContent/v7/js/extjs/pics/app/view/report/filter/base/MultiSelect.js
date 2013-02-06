Ext.define('PICS.view.report.filter.base.MultiSelect', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbasemultiselect',
    
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
            editable: false,
            multiSelect: true,
            name: 'value',
            valueField: 'key',
            width: 258
        };
    },
    
    updateValueFieldStore: function (field_id) {
        var value_field = this.down('combobox');
        
        value_field.store = Ext.create('Ext.data.Store', {
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