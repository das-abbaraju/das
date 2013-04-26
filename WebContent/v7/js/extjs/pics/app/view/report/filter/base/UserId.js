Ext.define('PICS.view.report.filter.base.UserId', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbaseuserid',

    operator_store: [
        ['Equals', PICS.text('Report.execute.userIdFilter.equals')],
        ['NotEquals', PICS.text('Report.execute.userIdFilter.notEquals')],
        ['CurrentUser', PICS.text('Report.execute.userIdFilter.currentUser')]
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
            allowDecimals: false,
            flex: 1,
            hideTrigger: true,
            keyNavEnabled: false,
            mouseWheelEnabled: false,
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