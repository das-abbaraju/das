Ext.define('PICS.view.report.filter.base.Filter', {
    extend: 'Ext.form.Panel',
    alias: 'widget.reportfilterbasefilter',

    border: 0,
    draggable: false,
    layout: 'hbox',
    name: 'filter_input',

    initComponent: function () {
        if (typeof this.createOperatorField != 'function') {
            Ext.Error.raise('Method createOperatorField missing');
        }

        if (typeof this.createValueField != 'function') {
            Ext.Error.raise('Method createValueField missing');
        }

        var negate_operator_field = {
            xtype: 'hiddenfield',
            name: 'negate_operator'
        };

        var operator_field = this.createOperatorField();
        var value_field = this.createValueField();

        this.items = [
            negate_operator_field,
            operator_field,
            value_field
        ];

        this.callParent(arguments);
    }
});