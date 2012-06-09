Ext.define('PICS.view.report.filter.StringFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.stringfilter'],

    record: null,

    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        var string_filter = {
            xtype: 'panel',
            border: 0,
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
                name: 'filter_value',
                listeners: {
                    blur: function () {
                        this.up('stringfilter').record.set('value', this.value);
                    }
                },
                value: null
            }],
            layout: 'hbox'
        };

        // add filter
        this.child('panel [name=filter_input]').add(string_filter);

        // set filter number
        this.child('displayfield[name=filter_number]').fieldLabel = this.panelNumber;

        // set filter name
        this.child('panel displayfield[name=filter_name]').setValue(this.record.get('name'));

        // set filter inputs
        this.child('panel combo[name=operator]').setValue(this.record.get('operator'));
        this.child('panel textfield[name=filter_value]').setValue(this.record.get('value'));
    }
});