Ext.define('PICS.view.report.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reporttoolbar'],
    requires: [
       'PICS.view.report.Sorts'
    ],

    height: 50,
    id: 'report_toolbar',
    items: [{
        xtype: 'tbtext',
        cls: 'header',
        text: 'Sort Order:'
    }, {
        xtype: 'reportsorts',
        id: 'report_sorts'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        action: 'add-column',
        cls: 'add-column default',
        height: 26,
        text: '<i class="icon-plus icon-large"></i>Add Column'
    }]
});
