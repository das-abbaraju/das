Ext.define('PICS.view.report.filter.base.ListFilter', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportfilterbaselistfilter',
    
    requires: [
        'Ext.form.field.ComboBox'
    ],

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            Ext.Error.raise('Invalid filter record');
        }

        this.record.set('operator', 'In');

        var list = this.createList(this.record);

        this.add(list);
    },

    createList: function (record) {
        var value = record.get('value');
        var store = this.getStoreForList(record);

        return {
            xtype: 'combobox',
            displayField: 'value',
            editable: false,
            multiSelect: true,
            name: 'filter_value',
            store: store,
            value: value,
            valueField: 'key',
            width: 258
        };
    },

    getStoreForList: function (record) {
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
                url: 'Autocompleter.action?fieldType=' + field_type,
                reader: {
                    root: 'result',
                    type: 'json'
                }
            }
        };
    }
});