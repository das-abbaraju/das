Ext.define('PICS.view.report.filter.base.Boolean', {
    extend: 'PICS.view.report.filter.base.Filter',
    alias: 'widget.reportfilterbaseboolean',
    
    createOperatorField: function () {
        return {
            xtype: 'hiddenfield',
            name: 'operator'
        };
    },

    createValueField: function () {
        return {
            xtype: 'checkbox',
            boxLabel: 'True',
            inputValue: true,
            name: 'value',
            uncheckedValue: false
        };
    }
});