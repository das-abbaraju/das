Ext.define('PICS.view.report.filter.base.StringFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbasestringfilter'],

    border: 0,
    layout: 'hbox',

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            throw '';
        }

        var combobox = this.createCombobox(this.record);
        var textfield = this.createTextfield(this.record);

        this.add([
            combobox,
            textfield
        ]);
    },

    createCombobox: function (record) {
        var operator = record.get('operator');

        if (!operator) {
            operator = PICS.app.constants.TEXTSTORE[0][0];

            record.set('operator', operator);
        }

        return {
            xtype: 'combobox',
            editable: false,
            flex: 1.5,
            margin: '0 5 0 0',
            name: 'operator',
            store: PICS.app.constants.TEXTSTORE,
            value: operator
        };
    },

    createTextfield: function (record) {
        var value = record.get('value');

        return {
            xtype: 'textfield',
            flex: 2,
            name: 'filter_value',
            value: value
        };
    }
});