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
            displayField: 'name',
            editable: false,
            multiSelect: true,
            name: 'filter_value',
            store: store,
            value: value,
            valueField: 'id',
            width: 258
        };
    },

    getStoreForList: function (record) {
        var url = Ext.Object.fromQueryString(document.location.search);
        var name = record.get('name');

        return {
            fields: [{
                name: 'id',
                type: 'string'
            }, {
                name: 'name',
                type: 'string'
            }],
            proxy: {
                type: 'ajax',
                // TODO: why does this require a report number
                url: 'ReportDynamic!list.action?report=' + url.report + '&fieldName=' + name,
                reader: {
                    root: 'result',
                    type: 'json'
                }
            }
        };
    }
});