Ext.define('PICS.view.report.filter.base.String', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbasestring',

    operator_store: [
        ['Contains', PICS.text('Report.execute.stringFilter.contains')],
        ['BeginsWith', PICS.text('Report.execute.stringFilter.beginsWith')],
        ['EndsWith', PICS.text('Report.execute.stringFilter.endsWith')],
        ['Equals', PICS.text('Report.execute.stringFilter.equals')],
        ['Empty', PICS.text('Report.execute.stringFilter.empty')],
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
        var input = this.down('textfield[name="value"]');

        if (operator == 'Empty' || operator == 'NotEmpty') {
            input.setValue(null);
            input.hide();
        } else {
            input.show();
        }
    }
});