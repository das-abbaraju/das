Ext.define('PICS.view.report.filter.base.NumberFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbasenumberfilter'],

    border: 0,
    layout: 'hbox',

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            // die
        }

        var combobox = this.createCombobox(record);
        var textfield = this.createTextfield(record);

        this.add([
            combobox,
            textfield
        ]);
    },

    createCombobox: function (record) {
        var operator = record.get('operator');

        return {
            xtype: 'combobox',
            editable: false,
            flex: 1.5,
            listeners: {
                change: function (obj, newval, oldval, options) {
                   //this.up('numberfilter').record.set('operator', newval);
                }
            },
            margin: '0 5 0 0',
            name: 'operator',
            store: PICS.app.constants.NUMBERSTORE,
            value: operator
        };
    },

    createTextfield: function (record) {
        var value = record.get('value');

        return {
            xtype: 'textfield',
            flex: 2,
            listeners: {
                blur: function () {
                    //this.up('numberfilter').record.set('value', this.value);
                }
            },
            name: 'filter_value',
            value: value
        };
    }
});