Ext.define('PICS.view.report.filter.base.AccountIDFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbaseaccountidfilter'],

    border: 0,
    layout: 'hbox',

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            Ext.Error.raise('Invalid filter record');
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
            record.set('operator', 'equals');
        }

        return {
            xtype: 'combobox',
            editable: false,
            flex: 1.5,
            margin: '0 5 0 0',
            name: 'operator',
            store: [
                ['Equals', 'equals'],
                ['NotEquals', 'does not equal'],
                ['CurrentAccount', 'current account']
            ],
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