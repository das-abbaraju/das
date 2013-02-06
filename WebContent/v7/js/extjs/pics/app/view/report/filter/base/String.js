Ext.define('PICS.view.report.filter.base.String', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbasestring',

    operator_store: [
        ['Contains', 'contains'],
        ['NotContains', 'does not contain'],
        ['BeginsWith', 'begins with'],
        ['NotBeginsWith', 'does not begin with'],
        ['EndsWith', 'ends with'],
        ['NotEndsWith', 'does not end with'],
        ['Equals', 'equals'],
        ['NotEquals', 'does not equal'],
        ['Empty', 'is empty'],
        ['NotEmpty', 'is not empty']
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
            xtype: 'textfield',
            flex: 1, // TODO: Leaves too little room for all of the string operators
            name: 'value'
        };
    },
    
    updateValueFieldFromOperatorValue: function (operator) {
        var input = this.down('textfield');
        
        if (operator == 'Empty') {
            input.setValue(null);
            input.hide();
        } else {
            input.show();
        }
    }
});