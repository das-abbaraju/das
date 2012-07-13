Ext.define('PICS.view.report.FilterOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.filteroptions'],

    autoScroll: true,
    collapsed: true,
    collapsible: true,
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        height: 50,
        id: 'report_filter_actions',
        items: [{
            xtype: 'splitbutton',
            action: 'add-filter',
            cls: 'add-filter default',
            height: 26,
            menu: new Ext.menu.Menu({
                items: [{
                    action: 'toggle-advanced-filtering',
                    cls: 'toggle-advanced-filtering',
                    text: 'Advanced Filtering'
                }],
                plain: true
            }),
            text: '<i class="icon-plus icon-large"></i>Add Filter',
            width: 90
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'button',
            action: 'search',
            cls: 'search success',
            height: 26,
            text: 'Update Results'
        }]
    }],
    floatable: false,
    header: {
        height: 50
    },
    id: 'report_filter_options',
    items: [{
        border: 0,
        id: 'report_filters'
    }],
    margin: '0px 0px 20px',
    title: 'Filter',
    width: 320
});