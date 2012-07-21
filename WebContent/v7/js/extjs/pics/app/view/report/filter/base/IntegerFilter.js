Ext.define('PICS.view.report.filter.base.IntegerFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbaseintegerfilter'],

    border: 0,
    layout: 'hbox',

    constructor: function () {
        this.callParent(arguments);

        if (!this.record) {
            // die
        }

        var combobox = this.createCombobox(this.record);
        var numberfield = this.createNumberfield(this.record);

        this.add([
            combobox,
            numberfield
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
                   //this.up('integerfilter').record.set('operator', newval);
                }
            },
            margin: '0 5 0 0',
            name: 'operator',
            store: PICS.app.constants.NUMBERSTORE,
            value: operator
        };
    },

    createNumberfield: function (record) {
        var value = record.get('value');

        return {
            xtype: 'numberfield',
            allowDecimals: false,
            flex: 2,
            hideTrigger: true,
            keyNavEnabled: false,
            listeners: {
                blur: function () {
                    //this.up('integerfilter').record.set('value', this.value);
                }
            },
            mouseWheelEnabled: false,
            name: 'filter_value',
            value: value
        };
    }
});