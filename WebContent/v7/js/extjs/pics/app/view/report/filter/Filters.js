Ext.define('PICS.view.report.filter.Filters', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reportfilters',

    requires: [
        'PICS.view.report.filter.Filter'
    ],

    border: 0,
    bodyBorder: 0,
    id: 'report_filters',

    initComponent: function () {
        if (Ext.getClassName(this.store) != 'Ext.data.Store') {
            Ext.Error.raise('Invalid Filter Store');
        }

        var index = 1,
            items = [],
            that = this;

        this.store.each(function (record) {
            var filter = Ext.create('PICS.view.report.filter.Filter', {
                index: index,
                filter: record
            });

            items.push(filter);

            index += 1;
        });
        
        this.items = items;
        
        this.callParent(arguments);
    },
    
    hideFilterNumbers: function () {
        this.removeCls('x-active');
    },
    
    showFilterNumbers: function () {
        this.addCls('x-active');
    }
});