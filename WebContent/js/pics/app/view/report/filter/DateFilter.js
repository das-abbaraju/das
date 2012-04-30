Ext.define('PICS.view.report.filter.DateFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.datefilter'],

    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        var dateFilter = {
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
     
                        datefilter.record.set('value', values.filterValue);
                    }
                },
                maxValue: new Date(),
                name: 'filterValue',
                value: null
            }],
            layout: 'hbox'        
        };
        this.add(dateFilter);
        
        this.child('panel displayfield[name=filterName]').fieldLabel = this.panelNumber;
        this.child('panel displayfield[name=filterName]').setValue(this.record.get('column'));
        this.child('panel combo[name=operator]').setValue(this.record.get('operator'));
        this.child('panel datefield[name=filterValue]').setValue(this.record.get('value'));
    }
});