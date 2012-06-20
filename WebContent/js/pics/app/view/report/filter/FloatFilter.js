Ext.define('PICS.view.report.filter.FloatFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.floatfilter'],

    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        var float_filter = {
            xtype: 'panel',
            border: 0,
            items: [{
                xtype: 'combo',
                editable: false,
                listeners: {
                    change: function (obj, newval, oldval, options) {
                       this.up('floatfilter').record.set('operator', newval);
                    }
                },
                margin: '0 5 0 0',
                name: 'operator',
                store: PICS.app.constants.NUMBERSTORE,
                flex: 1.5,
                value: null
            }, {
                xtype: 'numberfield',

                allowDecimals: true,
                flex: 2,
                hideTrigger: true,
                keyNavEnabled: false,
                listeners: {
                    blur: function () {
                        this.up('floatfilter').record.set('value', this.value);
                    }
                },
                mouseWheelEnabled: false,
                name: 'filter_value',
                value: null
            }],
            layout: 'hbox'
        };

        // add filter
        this.child('panel [name=filter_input]').add(float_filter);

        // set filter number
        this.child('displayfield[name=filter_number]').fieldLabel = this.panelNumber;

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