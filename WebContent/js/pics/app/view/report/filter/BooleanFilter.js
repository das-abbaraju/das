Ext.define('PICS.view.report.filter.BooleanFilter', {
    extend: 'PICS.view.report.filter.BaseFilter',
    alias: ['widget.booleanfilter'],

    constructor: function (data) {
        this.record = data.record;

        this.callParent(arguments);

        var boolean_filter = {
            xtype: 'checkbox',
            boxLabel: 'True',
            name: 'filter_value',
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
        };

        // add filter
        this.child('panel [name=filter_input]').add(boolean_filter);

        // set filter number
        this.child('displayfield[name=filter_number]').fieldLabel = this.panelNumber;

        // set filter name
        this.child('panel displayfield[name=filter_name]').setValue(this.record.get('name'));

        // set filter inputs
        this.child('panel checkbox[name=filter_value]').setValue(this.record.get('not'));
    }
});