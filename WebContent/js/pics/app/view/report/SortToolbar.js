Ext.define('PICS.view.report.SortToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.reportsorttoolbar'],
    requires: ['PICS.view.report.SortButtons'],

    height: 50,
    id: 'report_toolbar',
    items: [{
        xtype: 'tbtext',
        cls: 'header',
        text: 'Sort Order:'
    }, {
        xtype: 'sortbuttons',
        id: 'report_sort'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        action: 'add-column',
        cls: 'add-column',
        height: 26,
        text: '<i class="icon-plus icon-large"></i>Add Column',
        width: 90
    }]
});
