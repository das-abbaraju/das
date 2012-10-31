Ext.define('PICS.view.report.filter.base.DateFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbasedatefilter'],

    requires: [
        'Ext.form.field.Date'
    ],
    
    border: 0,
    layout: 'hbox',

    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            Ext.Error.raise('Invalid filter record');
        }

        var combobox = this.createCombobox(this.record);
        var datefield = this.createDatefield(this.record);

        this.add([
            combobox,
            datefield
        ]);
    },

    createCombobox: function (record) {
        var operator = record.get('operator');

        if (!operator) {
            operator = PICS.app.constants.DATESTORE[0][0];

            record.set('operator', operator);
        }

        return {
            xtype: 'combobox',
            editable: false,
            flex: 1.5,
            margin: '0 5 0 0',
            name: 'operator',
            store: PICS.app.constants.DATESTORE,
            value: operator
        };
    },

    createDatefield: function (record) {
        var value = record.get('value');

        return {
            xtype: 'datefield',
            flex: 2,
            format: 'Y-m-d',
            listeners: {
                render: function (cmp, eOpts) {
                    // by-pass setValue validation by modifying dom directly
                    cmp.el.down('input[name="filter_value"]').dom.value = value;
                }
            },
            name: 'filter_value',
            preventMark: true
        };
    }
});