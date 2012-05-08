Ext.define('PICS.view.report.filter.BaseFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.basefilter'],

    border: false,
    bodyPadding: '5 10',
    cls: 'baseFilter',
    defaults: {
        anchor: '100%',
        border: 0
    },

   items: [{
        layout: {
            type: 'hbox',
            align: 'middle'
        },
        items: [{
            xtype: 'displayfield',
            fieldLabel: null,
            labelSeparator: '',
            labelPad: 5,
            labelWidth: 'auto',
            name: 'filterName',
            value: null
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'button',
            action: 'remove-filter',
            icon: 'images/cross.png',
            iconCls: 'remove-filter',
            tooltip: 'Remove'
        }]
    }],
    layout: 'anchor',
    record: null,
    width: 300
});