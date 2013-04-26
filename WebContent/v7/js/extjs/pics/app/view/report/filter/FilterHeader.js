Ext.define('PICS.view.report.filter.FilterHeader', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportfilterheader'],

    border: 0,
    height: 50,
    items: [{
        xtype: 'button',
        id: 'report_filter_options_collapse',
        text: '<i class="icon-chevron-left icon-large"></i> ' + PICS.text('Report.execute.filterHeader.buttonFilter')
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        action: 'add-filter',
        cls: 'add-filter default',
        height: 26,
        text: '<i class="icon-plus icon-large"></i> ' + PICS.text('Report.execute.filterHeader.buttonAddFilter')
    }],
    layout: {
        type: 'hbox',
        align: 'middle'
    }
});