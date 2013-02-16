Ext.define('PICS.view.report.filter.base.Number', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbasenumber',

    operator_store: [
        ['Equals', '='],
        ['GreaterThan', '>'],
        ['LessThan', '<'],
        ['GreaterThanOrEquals', '>='],
        ['LessThanOrEquals', '<='],
        ['Empty', 'is empty']
    ],
    
    createOperatorField: function () {
        return {
            xtype: 'combobox',
            editable: false,
            width: 120,
            margin: '0 5 0 0',
            name: 'operator',
            store: this.operator_store
        };
    },
    
    createValueField: function () {
        return {
            xtype: 'numberfield',
            allowDecimals: true,
            flex: 1,
            hideTrigger: true,
            keyNavEnabled: false,
            mouseWheelEnabled: false,
            name: 'value'
        };
    },
    
    updateValueFieldFromOperatorValue: function (operator) {
        var input = this.down('numberfield');
        
        if (operator == 'Empty') {
            input.setValue(null);
            input.hide();
        } else {
            input.show();
        }
    }
});