Ext.define('PICS.view.report.filter.FilterHeader', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportfilterheader'],

    border: 0,
    height: 50,
    items: [{
        xtype: 'button',
        id: 'report_filter_options_collapse',
        text: '<i class="icon-chevron-left icon-large"></i> Filter',
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        action: 'add-filter',
        cls: 'add-filter default',
        height: 26,
        text: '<i class="icon-plus icon-large"></i>Add Filter'
    }],
    layout: {
        type: 'hbox',
        align: 'middle'
    }
});