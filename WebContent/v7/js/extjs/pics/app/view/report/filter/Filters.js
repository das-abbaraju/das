Ext.define('PICS.view.report.filter.Filters', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportfilterfilters'],
    requires: [
        'PICS.view.report.filter.Filter'
    ],

    border: 0,
    id: 'report_filters',

    constructor: function () {
        this.callParent(arguments);

        // filter store
        if (!this.store) {
            // die
        }

        var that = this;
        var index = 1;

        this.store.each(function (record) {
            var filter = Ext.create('PICS.view.report.filter.Filter', {
                index: index,
                record: record
            });

            that.add(filter);

            index += 1;
        });
    }
});