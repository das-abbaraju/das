Ext.define('PICS.view.report.FilterOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.filteroptions'],

    autoScroll: true,
    collapsible: true,
    defaults: {
        border: false
    },
    /*Do not show until implemented*/
    /*dockedItems: [{
        xtype: 'form',
        defaults: {
            border: false
        },
        dock: 'bottom',
        items: [{
            html: 'Advanced',
            margin: '0 0 5 0',
            padding: '0 10'
        }, {
            xtype: 'form',
            items: [{
                xtype: 'textfield',
                margin: '0 5 0 0',
                size: 30
            }, {
                xtype: 'button',
                action: 'Update',
                text: 'Update'
            }],
            layout: 'hbox',
            padding: '0 10 10 10'
        }]
    }],*/
    floatable: false,
    id: 'filteroptions',
    items: [{
        buttonAlign: 'right',
        buttons: [{
            action: 'search',
            text: 'Search'
        }],
        width: 300
    }, {
        xtype: 'panel',
        id: 'filterDetails'
    }, {
        buttonAlign: 'right',
        buttons: [{
            action: 'search',
            text: 'Search'
        }],
        width: 300
    }],
    tbar: [{
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        action: 'add-filter',
        icon: 'js/pics/resources/images/dd/drop-add.gif',
        text: 'Add Filter'
    }],
    title: 'Filter Options',
    width: 320,

    constructor: function () {
        this.callParent(arguments);
        /*var filter = Ext.create('PICS.view.report.filter.StringFilter');
        this.add(filter);*/
    }
});