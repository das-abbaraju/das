Ext.define('PICS.view.report.FilterExpression', {
    extend: 'Ext.form.Panel',
    alias: ['widget.advancedfilter'],

    border: 0,
    id: 'report_filter_expression',
    dock: 'top',
    height: 80,
    items: [{
        xtype: 'form',
        border: 0,
        items: [{
            xtype: 'textfield',
            name: 'filterexpression',
            width: 220
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'button',
            action: 'update',
            cls: 'update',
            height: 26,
            text: 'Apply',
            tooltip: 'Apply Filter Expression',
            width: 50
        }],
        layout: 'hbox',
        width: 280
    }, {
        xtype: 'form',
        border: 0,
        cls: 'actions',
        items: [{
            xtype: 'button',
            action: 'hide',
            cls: 'hide',
            text: 'Cancel'
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'button',
            action: 'more-information',
            cls: 'more-information',
            text: 'More Information'
        }],
        layout: 'hbox',
        width: 220
    }],

    constructor: function (config) {
        this.callParent(arguments);

        if (config) {
            var expression = config.expression.toLowerCase();
            this.child('form textfield[name=filterexpression]').setValue(expression);
        }
    }
});