Ext.define('PICS.view.report.filter.StringFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.stringfilter'],

    record: null,
    
    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);
        
        var stringFilter = {
            xtype: 'panel',
            items: [{
                xtype: 'combo',
                editable: false,
                listeners: {
                    change: function (obj, newval, oldval, options) {
                       this.up('stringfilter').record.set('operator', newval);
                    }
                },
                margin: '0 5 0 0',
                name: 'operator',
                store: PICS.app.constants.TEXTSTORE,
                flex: 1.5,
                value: null
            }, {
                xtype: 'textfield',
                flex: 2,
                name: 'filterValue',
                listeners: {
                    blur: function () {
                        this.up('stringfilter').record.set('value', this.value);
                    }
                },
                value: null
            }],
            layout: 'hbox'
        };
        this.add(stringFilter);
       
        this.child('panel displayfield[name=filterName]').fieldLabel = this.panelNumber;
        this.child('panel displayfield[name=filterName]').setValue(this.record.get('name'));
        this.child('panel combo[name=operator]').setValue(this.record.get('operator'));
        this.child('panel textfield[name=filterValue]').setValue(this.record.get('value'));        
    }
});