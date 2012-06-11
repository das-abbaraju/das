Ext.define('PICS.view.report.FilterOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.filteroptions'],

    autoScroll: true,
    collapsible: true,
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        height: 50,
        id: 'report_filter_actions',
        items: [{
            xtype: 'splitbutton',
            action: 'add-filter',
            cls: 'add-filter',
            height: 26,
            menu: new Ext.menu.Menu({
                items: [{
                    action: 'toggle-advanced-filtering',
                    cls: 'toggle-advanced-filtering',
                    text: 'Advanced Filtering'
                }]
            }),
            text: '<i class="icon-plus icon-large"></i>Add Filter',
            width: 90
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'button',
            action: 'search',
            cls: 'search',
            height: 26,
            text: 'Update Results'
        }]
    }, {
        xtype: 'form',
        border: 0,
        id: 'report_filter_expression',
        dock: 'top',
        height: 80,
        items: [{
            xtype: 'form',
            border: 0,
            items: [{
                xtype: 'textfield',
                name: 'filterexpression',
                width: 220
            }, {
                xtype: 'tbfill'
            }, {
                xtype: 'button',
                action: 'update',
                cls: 'update',
                height: 26,
                text: 'Apply',
                tooltip: 'Apply Filter Expression',
                width: 50
            }],
            layout: 'hbox',
            width: 280
        }, {
            xtype: 'form',
            border: 0,
            cls: 'actions',
            items: [{
                xtype: 'button',
                action: 'hide',
                cls: 'hide',
                text: 'Cancel'
            }, {
                xtype: 'tbfill'
            }, {
                xtype: 'button',
                action: 'more-information',
                cls: 'more-information',
                text: 'More Information'
            }],
            layout: 'hbox',
            width: 220
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