Ext.define('PICS.view.report.filter.NumberFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.numberfilter'],

    border: false,
    items: [{
        layout: {
            type: 'hbox',
            align: 'middle'
        },
        items: [{
            xtype: 'displayfield',
            fieldLabel: null,
            labelSeparator: '',
            labelPad: 5,
            labelWidth: 'auto',
            name: 'filterName',
            value: null
        }]
    }, {
        xtype: 'panel',
        items: [{
            xtype: 'combo',
            editable: false,
            listeners: {
                change: function (obj, newval, oldval, options) {
                   this.up('numberfilter').record.set('operator', newval);
                }
            },
            margin: '0 5 0 0',
            name: 'operator',
            store: PICS.app.constants.NUMBERSTORE,
            flex: 1.5,
            value: null
        }, {
            xtype: 'textfield',
            flex: 2,
            name: 'filterValue',
            listeners: {
                blur: function () {
                    this.up('numberfilter').record.set('value', this.value);
                }
            },
            value: null
        }],
        layout: 'hbox'
    }],
    record: null,
    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        if (this.record.get('operator') === '') {
            var firstValue = this.child('panel combo[name=operator]').store.getAt(0).data.field1;
            this.child('panel combo[name=operator]').setValue(firstValue);
        } else {
            this.child('panel combo[name=operator]').setValue(this.record.get('operator'));
        }

        this.child('panel textfield[name=filterValue]').setValue(this.record.get('value'));
    }
});