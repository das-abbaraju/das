Ext.define('PICS.view.report.filter.BooleanFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.booleanfilter'],

    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        var booleanfilter = {
            xtype: 'form',
            border: 0,
            items: [{
                xtype: 'checkbox',
                boxLabel: 'True',
                margin: '0 5 0 10',
                name: 'filterValue',
                flex: 1.5,
                inputValue: null,
                listeners: {
                    change: function (obj, newval, oldval, options) {
                        var record = this.up('booleanfilter').record;
                        if (newval === false) {
                            record.set('value', 0);
                        } else {
                            record.set('value', 1);
                        }
                    }
                }
            }],
            layout: 'hbox'
        };
        this.add(booleanfilter);        
        
        this.child('panel displayfield[name=filterName]').fieldLabel = this.panelNumber;
        this.child('panel displayfield[name=filterName]').setValue(this.record.get('name'));
        this.child('panel checkbox[name=filterValue]').setValue(this.record.get('not'));
    }
});