Ext.define('PICS.view.report.FilterOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.filteroptions'],

    autoScroll: true,
    collapsible: true,
    dockedItems: [/*{
        xtype: 'form',
        defaults: {
            border: 0
        },
        dock: 'bottom',
        items: [{
            xtype: 'button',
            cls: 'tooltipIcon',
            icon: 'js/pics/resources/images/tools/question_mark.png',
            iconAlign: 'right',
            margin: '0 0 5 0',
            padding: '0 10',
            scale: 'small',
            text: 'Advanced',
            tooltip: 'Advanced Filter Search'
        }, {
            xtype: 'form',
            items: [{
                xtype: 'textfield',
                margin: '0 5 0 0',
                name: 'filterexpression',
                size: 30
            }, {
                xtype: 'button',
                action: 'update',
                text: 'Update'
            }],
            layout: 'hbox',
            padding: '0 10 10 10'
        }]
    },*/ {
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
            width: 100
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'button',
            action: 'search',
            cls: 'search',
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