Ext.define('PICS.view.report.filter.base.AutocompleteFilter', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportfilterbaseautocompletefilter',
    
    requires: [
        'Ext.form.field.ComboBox'
    ],

    cls: 'autocomplete-filter',

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            Ext.Error.raise('Invalid filter record');
        }

        this.record.set('operator', 'In');

        var autocomplete = this.createAutocomplete(this.record);

        this.add(autocomplete);
    },

    createAutocomplete: function (record) {
        var value = record.get('value');
        var store = this.getStoreForAutocomplete(record);

        return {
            xtype: 'combobox',
            displayField: 'value',
            editable: true,
            hideTrigger: true,
            multiSelect: false,
            name: 'filter_value',
            queryParam: 'searchQuery',
            store: store,
            value: value,
            valueField: 'key',
            width: 258
        };
    },

    getStoreForAutocomplete: function (record) {
        var field = record.getAvailableField(),
            field_type = field.get('fieldType');

        return {
            fields: [{
                name: 'key',
                type: 'string'
            }, {
                name: 'value',
                type: 'string'
            }],
            proxy: {
                type: 'ajax',
                url: 'ReportAutocomplete.action?fieldType=' + field_type,
                reader: {
                    root: 'result',
                    type: 'json'
                }
            }
        };
    }
});