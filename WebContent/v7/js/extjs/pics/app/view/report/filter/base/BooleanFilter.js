Ext.define('PICS.view.report.filter.base.BooleanFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbasebooleanfilter'],

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            // die
        }

        var checkbox = this.createCheckbox(this.record);

        this.add(checkbox);
    },

    createCheckbox: function (record) {
        var value = record.get('not');

        return {
            xtype: 'checkbox',
            boxLabel: 'True',
            inputValue: null,
            listeners: {
                change: function (obj, newval, oldval, options) {
                    /*var record = this.up('booleanfilter').record;
                    if (newval === false) {
                        record.set('value', 0);
                    } else {
                        record.set('value', 1);
                    }*/
                }
            },
            name: 'filter_value',
            value: value
        };
    }
});