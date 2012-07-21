Ext.define('PICS.view.report.filter.base.StringFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbasestringfilter'],

    border: 0,
    layout: 'hbox',

    constructor: function () {
        this.callParent(arguments);

        if (!this.record) {
            // die
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

        return {
            xtype: 'combobox',
            editable: false,
            flex: 1.5,
            listeners: {
                change: function (obj, newval, oldval, options) {
                   //this.up('stringfilter').record.set('operator', newval);
                }
            },
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
            listeners: {
                blur: function () {
                    //this.up('stringfilter').record.set('value', this.value);
                }
            },
            name: 'filter_value',
            value: value
        };
    }
});