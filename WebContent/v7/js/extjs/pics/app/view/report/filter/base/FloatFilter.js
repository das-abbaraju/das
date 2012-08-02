Ext.define('PICS.view.report.filter.base.FloatFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbasefloatfilter'],

    border: 0,
    layout: 'hbox',

    initComponent: function () {
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
                   //this.up('floatfilter').record.set('operator', newval);
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
            allowDecimals: true,
            flex: 2,
            hideTrigger: true,
            keyNavEnabled: false,
            listeners: {
                blur: function () {
                    //this.up('floatfilter').record.set('value', this.value);
                }
            },
            mouseWheelEnabled: false,
            name: 'filter_value',
            value: value
        };
    }
});