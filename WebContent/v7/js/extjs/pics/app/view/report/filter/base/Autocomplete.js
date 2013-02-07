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

    updateValueFieldStore: function (report, filter) {
        var report_id = report.get('id'),
            field_id = filter.get('field_id'),
            filter_value = filter.get('value'),
            value_field = this.down('combobox'),
            url = PICS.data.ServerCommunicationUrl.getAutocompleteUrl(report_id, field_id, filter_value);
        
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
                    value_field.select(filter_value);
                }
            }
        });
    }
});