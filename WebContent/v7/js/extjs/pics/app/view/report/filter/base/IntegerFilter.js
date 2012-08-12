Ext.define('PICS.view.report.filter.base.IntegerFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbaseintegerfilter'],

    border: 0,
    layout: 'hbox',

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            throw '';
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

        if (!operator) {
            operator = PICS.app.constants.NUMBERSTORE[0][0];

            record.set('operator', operator);
        }

        return {
            xtype: 'combobox',
            editable: false,
            flex: 1.5,
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
            mouseWheelEnabled: false,
            name: 'filter_value',
            value: value
        };
    }
});