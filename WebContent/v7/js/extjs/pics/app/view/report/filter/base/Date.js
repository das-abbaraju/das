Ext.define('PICS.view.report.filter.base.Date', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbasedate',

    operator_store: [
        ['LessThan', 'before'],
        ['GreaterThan', 'after'],
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
            xtype: 'datefield',
            flex: 1,
            format: 'Y-m-d',
            listeners: {
                render: function (cmp, eOpts) {
                    // by-pass setValue validation by modifying dom directly
                    cmp.el.down('input[name="filter_value"]').dom.value = value;
                }
            },
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