Ext.define('PICS.view.report.filter.DateFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.datefilter'],

    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        var date_filter = {
            xtype: 'panel',
            border: 0,
            items: [{
                xtype: 'combo',
                editable: false,
                listeners: {
                    change: function (obj, newval, oldval, options) {
                       this.up('datefilter').record.set('operator', newval);
                    }
                },
                margin: '0 5 0 0',
                name: 'operator',
                store: PICS.app.constants.NUMBERSTORE,
                flex: 1.5,
                value: null
            }, {
                xtype: 'datefield',
                flex: 2,
                format: 'Y-m-d',
                listeners: {
                    blur: function () {
                        var datefilter = this.up('datefilter');
                        var values = datefilter.getValues();
                        console.log(values);

                        datefilter.record.set('value', values.filter_value);
                    }
                },
                maxValue: new Date(),
                name: 'filter_value',
                value: null
            }],
            layout: 'hbox'
        };

        // add filter
        this.child('panel [name=filter_input]').add(date_filter);

        // set filter number
        this.child('displayfield[name=filter_number]').fieldLabel = this.panelNumber;

        // set filter name
        this.child('panel displayfield[name=filter_name]').setValue(this.record.get('name'));

        // set filter inputs
        if (this.record.get('operator') === '') {
            var firstValue = this.child('panel combo[name=operator]').store.getAt(0).data.field1;
            this.child('panel combo[name=operator]').setValue(firstValue);
        } else {
            this.child('panel combo[name=operator]').setValue(this.record.get('operator'));
        }

        this.child('panel datefield[name=filter_value]').setValue(this.record.get('value'));
    }
});