Ext.define('PICS.view.report.filter.BooleanFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.booleanfilter'],

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
    },{
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
    }],
    record: null,
    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        this.child('panel displayfield[name=filterName]').fieldLabel = this.panelNumber;
        this.child('panel displayfield[name=filterName]').setValue(this.record.get('column'));
        this.child('panel checkbox[name=filterValue]').setValue(this.record.get('not'));
    }
});