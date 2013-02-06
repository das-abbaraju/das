Ext.define('PICS.view.report.filter.base.UserId', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbaseuserid',

    operator_store: [
        ['Equals', 'equals'],
        ['NotEquals', 'does not equal'],
        ['CurrentUser', 'current user']
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
            flex: 1, // TODO: Leaves too little room for all of the string operators
            name: 'value'
        };
    },
    
    updateValueFieldFromOperatorValue: function (operator) {
        var input = this.down('numberfield');
        
        if (operator == 'CurrentUser') {
            input.setValue(null);
            input.hide();
        } else {
            input.show();
        }
    }
});