Ext.define('PICS.view.report.filter.base.DateFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbasedatefilter'],

    border: 0,
    layout: 'hbox',

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            // die
        }

        var combobox = this.createCombobox(this.record);
        var datefield = this.createDatefield(this.record);

        this.add([
            combobox,
            datefield
        ]);
    },

    createCombobox: function (record) {
        var value = record.get('operator');

        return {
            xtype: 'combobox',
            editable: false,
            flex: 1.5,
            listeners: {
                change: function (obj, newval, oldval, options) {
                   //this.up('datefilter').record.set('operator', newval);
                }
            },
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
            listeners: {
                blur: function () {
                    /*var datefilter = this.up('datefilter');
                    var values = datefilter.getValues();
                    console.log(values);

                    datefilter.record.set('value', values.filter_value);*/
                }
            },
            maxValue: new Date(),
            name: 'filter_value',
            value: value
        };
    }
});