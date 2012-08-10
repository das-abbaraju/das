Ext.define('PICS.view.report.filter.base.DateFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbasedatefilter'],

    border: 0,
    layout: 'hbox',

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            throw '';
        }

        var combobox = this.createCombobox(this.record);
        var datefield = this.createDatefield(this.record);

        this.add([
            combobox,
            datefield
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
            value: value
        };
    },

    createDatefield: function (record) {
        var value = record.get('value');

        return {
            xtype: 'datefield',
            flex: 2,
            format: 'Y-m-d',
            maxValue: new Date(),
            name: 'filter_value',
            value: value
        };
    }
});