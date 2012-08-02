Ext.define('PICS.view.report.filter.IntegerFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.integerfilter'],

    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        var integer_filter = {
            xtype: 'panel',
            border: 0,
            items: [{
                xtype: 'combobox',
                editable: false,
                listeners: {
                    change: function (obj, newval, oldval, options) {
                       this.up('integerfilter').record.set('operator', newval);
                    }
                },
                margin: '0 5 0 0',
                name: 'operator',
                store: PICS.app.constants.NUMBERSTORE,
                flex: 1.5,
                value: null
            }, {
                xtype: 'numberfield',

                allowDecimals: false,
                flex: 2,
                hideTrigger: true,
                keyNavEnabled: false,
                listeners: {
                    blur: function () {
                        this.up('integerfilter').record.set('value', this.value);
                    }
                },
                mouseWheelEnabled: false,
                name: 'filter_value',
                value: null
            }],
            layout: 'hbox'
        };

        // add filter
        this.child('panel [name=filter_input]').add(integer_filter);

        // set filter inputs
        if (this.record.get('operator') === '') {
            var firstValue = this.child('panel combo[name=operator]').store.getAt(0).data.field1;
            this.child('panel combo[name=operator]').setValue(firstValue);
        } else {
            this.child('panel combo[name=operator]').setValue(this.record.get('operator'));
        }

        this.child('panel numberfield[name=filter_value]').setValue(this.record.get('value'));
    }
});