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
            xtype: 'boxselect',
            displayField: 'value',
            editable: true,
            emptyText: PICS.text('Report.execute.filter.autocomplete.emptyText') + '\u2026',
            filterPickList: true,
            flex: 1,
            height: 60,
            hideTrigger: true,
            minChars: 2,
            name: 'value',
            queryParam: 'searchQuery',
            triggerOnClick: false,
            valueField: 'key'
        };
    },

    updateValueFieldStore: function (filter) {
        var field_id = filter.get('field_id'),
            filter_value = filter.get('value'),
            value_field = this.down('combobox'),
            url = PICS.data.ServerCommunicationUrl.getAutocompleteUrl(field_id, filter_value);

        value_field.store = Ext.create('Ext.data.Store', {
            autoLoad: true,
            fields: [{
                name: 'key',
                type: 'string'
            }, {
                name: 'value',
                type: 'string'
            }],
            initialSelectionMade: false,
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
                    if (!this.initialSelectionMade) {
                        this.initialSelectionMade = true;
                        this.proxy.url = PICS.data.ServerCommunicationUrl.getAutocompleteUrl(field_id);
                        
                        value_field.select(filter.get('value').split(', '));
                    }
                }
            }
        });
    }
});