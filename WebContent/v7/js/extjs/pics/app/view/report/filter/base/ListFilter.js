Ext.define('PICS.view.report.filter.base.ListFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbaselistfilter'],

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            throw '';
        }

        // TODO: shouldn't the server do this?
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
        var url = Ext.Object.fromQueryString(document.location.search);
        var name = record.get('name');

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
                // TODO: why does this require a report number
                url: 'ReportAutocomplete.action?report=' + url.report + '&fieldName=' + name,
                reader: {
                    root: 'result',
                    type: 'json'
                }
            }
        };
    }
});