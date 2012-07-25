Ext.define('PICS.view.report.filter.Header', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportfilterheader'],

    height: 50,
    items: [{
        xtype: 'button',
        height: 15,
        id: 'report_filter_options_collapse',
        text: '<i class="icon-chevron-left icon-large"></i>',
        width: 10
    }, {
        xtype: 'tbtext',
        cls: 'header',
        text: 'Filter'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        action: 'add-filter',
        cls: 'add-filter default',
        height: 26,
        text: '<i class="icon-plus icon-large"></i>Add Filter'
    }]
});