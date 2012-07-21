Ext.define('PICS.view.report.filter.BaseFilter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.basefilter'],

    record: null,

    bodyCls: 'filter-body',
    border: 0,
    cls: 'filter',
    height: 80,
    items: [{
        xtype: 'displayfield',
        border: 0,
        cls: 'filter-number',
        labelSeparator: '',
        labelWidth: 30,
        name: 'filter_number',
        width: 30
    }, {
        border: 0,
        cls: 'filter-content',
        items: [{
            border: 0,
            height: 30,
            items: [{
                xtype: 'displayfield',
                cls: 'filter-name',
                name: 'filter_name'
            }, {
               xtype: 'tbfill'
            }, {
                xtype: 'button',
                action: 'remove-filter',
                cls: 'remove-filter',
                height: 16,
                text: '<i class="icon-remove-sign"></i>',
                tooltip: 'Remove',
                width: 16
            }],
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            name: 'filter_title'
        }, {
            border: 0,
            name: 'filter_input',
            draggable: false
        }],
        name: 'filter_content',
        width: 258
    }],
    layout: {
        type: 'hbox',
        align: 'middle'
    },
    overCls: 'x-over',
    width: '100%',

    constructor: function () {
        this.callParent(arguments);

        // set filter name
        this.query('displayfield[name=filter_name]')[0].setValue(this.record.get('text'));

        // set filter number
        this.query('displayfield[name=filter_number]')[0].setFieldLabel(this.panelNumber.toString());
    }
});