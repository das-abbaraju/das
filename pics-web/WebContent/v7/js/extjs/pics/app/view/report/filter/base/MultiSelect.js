Ext.define('PICS.view.report.filter.base.MultiSelect', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbasemultiselect',

    cls: 'multiselect-shortlist',

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
            editable: false,
            filterPickList: true,
            flex: 1,
            height: 61,
            name: 'value',
            queryMode: 'local', // Prevents reloading of the store, which would wipe out pre-selections.
            valueField: 'key',
            selectOnFocus: false
        };
    },

    updateValueFieldStore: function (filter_record) {
        var field_id = filter_record.get('field_id'),
            value_field = this.down('boxselect'),
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
                // Pre-select saved selections, i.e., display them in the input field and highlight them in the drop-down.
                load: function (store, records, successful, eOpts) {
                    value_field.select(filter_record.get('value').split(', '));
                }
            }
        });
    }
});