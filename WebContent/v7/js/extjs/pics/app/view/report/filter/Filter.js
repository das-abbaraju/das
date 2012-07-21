Ext.define('PICS.view.report.filter.Filter', {
    extend: 'Ext.form.Panel',
    alias: ['widget.reportfilterfilter'],

    requires: [
        'PICS.view.report.filter.FilterContent'
    ],

    bodyCls: 'filter-body',
    border: 0,
    cls: 'filter',
    height: 80,
    layout: {
        type: 'hbox',
        align: 'middle'
    },
    overCls: 'x-over',
    width: 320,

    constructor: function () {
        this.callParent(arguments);

        if (!this.record) {
            // die
        }

        if (!this.index) {
            // die
        }

        var filter_number = this.createFilterNumber(this.index);
        var filter_content = this.createFilterContent(this.record);

        this.add([
            filter_number,
            filter_content
        ]);
    },

    createFilterNumber: function (index) {
        return {
            xtype: 'displayfield',
            border: 0,
            cls: 'filter-number',
            fieldLabel: index.toString(),
            labelSeparator: '',
            labelWidth: 30,
            name: 'filter_number',
            width: 30
        };
    },

    createFilterContent: function (record) {
        return Ext.create('PICS.view.report.filter.FilterContent', {
            record: record
        });
    }
});