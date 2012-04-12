Ext.define('PICS.view.report.FilterOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.filteroptions'],

    buttonAlign: 'right',
    collapsible: true,
    floatable: false,
    id: 'filteroptions',
    
    bodyCls: 'ext-no-bottom-border',
    dockedItems: [{
        bodyCls: 'ext-no-bottom-border',
        dock: 'bottom',
        defaults: {
            border: false
        },
        xtype: 'form',
        items: [{
            html: 'Advanced',
            margin: '0 0 5 0',
            padding: '0 10'            
        },{
            xtype: 'form',
            padding: '0 10 10 10',
            items: [{
                xtype: 'textfield',
                margin: '0 5 0 0',
                size: 30
            },{
                xtype: 'button',
                action: 'Update',
                text: 'Update'
            }],
            layout: 'hbox',
                style: 'border-bottom-width: 0px',
        }]
    }],
    tbar: [{
        xtype: 'tbfill'
    },{
        xtype: 'button',
        action: 'add-filter',
        icon: 'js/pics/resources/images/dd/drop-add.gif',
        text: 'Add Filter'
    }],
    title: 'Filter Options',
    width: 300,
    style: 'border-bottom-width: 0px',
    constructor: function () {
        this.callParent(arguments);
        /*var filter = Ext.create('PICS.view.report.filter.StringFilter');
        this.add(filter);*/
    }
});    