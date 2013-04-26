Ext.define('PICS.view.report.filter.base.Date', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbasedate',

    operator_store: [
        ['LessThan', PICS.text('Report.execute.dateFilter.lessThan')],
        ['GreaterThanOrEquals', PICS.text('Report.execute.dateFilter.greaterThanEquals')],
        ['Empty', PICS.text('Report.execute.dateFilter.empty')]
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
            xtype: 'datefield',
            flex: 1,
            format: 'Y-m-d',
            name: 'value',
            preventMark: true
        };
    },
    
    updateValueFieldFromOperatorValue: function (operator) {
        var input = this.down('datefield');
        
        if (operator == 'Empty') {
            input.setValue(null);
            input.hide();
        } else {
            input.show();
        }
    }
});