Ext.define('PICS.view.report.filter.FloatFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.floatfilter'],

    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        var floatFilter = {
            xtype: 'panel',
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
                name: 'filterValue',
                value: null
            }],
            layout: 'hbox'
        };
        
        this.add(floatFilter);

        this.child('panel displayfield[name=filterName]').fieldLabel = this.panelNumber;
        this.child('panel displayfield[name=filterName]').setValue(this.record.get('column'));
        this.child('panel combo[name=operator]').setValue(this.record.get('operator'));
        this.child('panel numberfield[name=filterValue]').setValue(this.record.get('value'));
    }
});