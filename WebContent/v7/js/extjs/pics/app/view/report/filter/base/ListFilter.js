Ext.define('PICS.view.report.filter.base.ListFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbaselistfilter'],

    constructor: function () {
        this.callParent(arguments);

        if (!this.record) {
            // die
        }

        //this.record.set('operator', 'In');

        var list = this.createList(this.record);

        this.add(list);
    },

    createList: function (record) {
        var value = record.get('value');
        var store = this.getStoreForList(record);

        return {
            xtype: 'combobox',
            editable: false,
            listeners: {
                change: function (obj, newval, oldval, options) {
                   //this.up('listfilter').record.set('value', newval);
                }
            },
            multiSelect: true,
            name: 'filter_value',
            store: store,
            value: value,
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
                url: 'ReportDynamic!list.action?report=' + url.report + '&fieldName=' + name,
                reader: {
                    root: 'result',
                    type: 'json'
                }
            },
            listeners: {
                load: function () {
                    //set valueField to pull from json id
                    //me.child('panel [name=filter_input] boxselect').valueField = 'id';
                }
            }
        };
    }
});