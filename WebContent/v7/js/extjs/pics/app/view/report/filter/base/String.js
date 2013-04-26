Ext.define('PICS.view.report.filter.base.String', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbasestring',

    operator_store: [
        ['Contains', PICS.text('Report.execute.stringFilter.contains')],
        ['NotContains', PICS.text('Report.execute.stringFilter.notContains')],
        ['BeginsWith', PICS.text('Report.execute.stringFilter.beginsWith')],
        ['NotBeginsWith', PICS.text('Report.execute.stringFilter.notBeginsWith')],
        ['EndsWith', PICS.text('Report.execute.stringFilter.endsWith')],
        ['NotEndsWith', PICS.text('Report.execute.stringFilter.notEndsWith')],
        ['Equals', PICS.text('Report.execute.stringFilter.equals')],
        ['NotEquals', PICS.text('Report.execute.stringFilter.notEquals')],
        ['Empty', PICS.text('Report.execute.stringFilter.empty')],
        ['NotEmpty', PICS.text('Report.execute.stringFilter.notEmpty')]
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