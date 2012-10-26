Ext.define('PICS.view.report.filter.base.UserIDFilter', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterbaseuseridfilter'],

    border: 0,
    layout: 'hbox',
    value_field: null,
    
    initComponent: function () {
        this.callParent(arguments);

        if (!this.record) {
            Ext.Error.raise('Invalid filter record');
        }

        var combobox = this.createCombobox(this.record);
        
        this.add(combobox);
        
        if (this.record.get('fieldCompare')) {
            this.createFieldSelect(this.record);
        } else {
            this.createNumberfield(this.record);
        }
    },

    createCombobox: function (record) {
        var operator = record.get('operator');

        if (!operator) {
            record.set('operator', 'equals');
        }

        return {
            xtype: 'combobox',
            editable: false,
            flex: 1.5,
            margin: '0 5 0 0',
            name: 'operator',
            store: [
                ['Equals', 'equals'],
                ['NotEquals', 'does not equal'],
                ['CurrentUser', 'current user']
            ],
            value: operator
        };
    },

    createNumberfield: function (record) {
        
        if (this.value_field) {
            this.remove(this.value_field);
        }

        var value = record.get('value');

        this.value_field = this.add({
            xtype: 'numberfield',
            allowDecimals: false,
            blankText: 'Number',
            flex: 2,
            hideTrigger: true,
            keyNavEnabled: false,
            mouseWheelEnabled: false,
            name: 'filter_value',
            value: value
        });
    },
    
    createFieldSelect: function (record) {

        if (this.value_field) {
            this.remove(this.value_field);
        }

        var value = record.get('fieldCompare');
        
        this.value_field = this.add({
            xtype: 'textfield',
            blankText: 'Field',
            flex: 2,
            name: 'filter_field_compare',
            value: value
        });
    }
});