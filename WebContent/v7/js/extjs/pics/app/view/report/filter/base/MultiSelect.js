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
            queryMode: 'local', // Prevents reloading of the store, which would wipe out pre-selections.
            valueField: 'key',
            width: 258
        };
    },
    
    updateValueFieldStore: function (filter) {
        var field_id = filter.get('field_id'),
            filter_value = filter.get('value'),
            value_field = this.down('combobox'),
            url = PICS.data.ServerCommunicationUrl.getMultiSelectUrl(field_id);
        
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
                url: url,
                reader: {
                    root: 'result',
                    type: 'json'
                }
            },
            listeners: {
                // Pre-select saved selections, i.e., display them in the input field and highlight them in the down-down.
                load: function (store, records, successful, eOpts) {
                    if (typeof filter_value == 'string') {
                        var keys = filter_value.split(', ');
                        
                        value_field.select(keys);
                    }
                }
            }
        });
    }
});