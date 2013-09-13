Ext.define('PICS.view.report.filter.base.Autocomplete', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbaseautocomplete',

    cls: 'autocomplete',

    initComponent: function () {
        this.items = [this.createNegateOperatorField()];

        this.callParent(arguments);
    },

    listeners: {
        render: function (cmp, eOpts) {
            var el = this.getEl();

            // TODO: Use mon + delegate or some other ExtJS way of attaching listeners to elements.
            el.query('.x-boxselect-list')[0].onclick = function () {
                el.query('.x-boxselect-input-field')[0].focus();
            }
        }
    },

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
            height: 61,
            hideTrigger: true,
            minChars: 2,
            name: 'value',
            queryParam: 'searchQuery',

            // Default store required for boxselect to work, but quickly overridden by updateValueFieldStore.
            store: {
                fields: [{
                    name: 'key',
                    type: 'string'
                }, {
                    name: 'value',
                    type: 'string'
                }]
            },

            triggerOnClick: false,
            valueField: 'key'
        };
    },

    updateValueFieldStore: function (filter_record) {
        var field_id = filter_record.get('field_id'),
            filter_value = filter_record.get('value'),
            value_field = this.down('boxselect'),
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

                        value_field.select(filter_record.get('value').split(', '));
                    }
                }
            }
        });
    }
});