Ext.define('PICS.view.report.filter.IntegerFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.integerfilter'],

    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        var integerFilter = {
            xtype: 'panel',
            items: [{
                xtype: 'combo',
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
                name: 'filterValue',
                value: null
            }],
            layout: 'hbox'
        };
        
        this.add(integerFilter);

        this.child('panel displayfield[name=filterName]').fieldLabel = this.panelNumber;
        this.child('panel displayfield[name=filterName]').setValue(this.record.get('name'));
        this.child('panel combo[name=operator]').setValue(this.record.get('operator'));
        this.child('panel numberfield[name=filterValue]').setValue(this.record.get('value'));
    }
});