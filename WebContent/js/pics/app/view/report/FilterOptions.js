Ext.define('PICS.view.report.FilterOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.filteroptions'],

    buttonAlign: 'right',
    collapsible: true,
    floatable: false,
    id: 'filteroptions',
    tbar: [{
        xtype: 'tbfill'
    },{
        action: 'add-filter',
        icon: 'js/pics/resources/images/dd/drop-add.gif',
        text: 'Add Filter'
    }],
    title: 'Filter Options',
    width: 300,
    
    constructor: function () {
        this.callParent(arguments);
        /*var filter = Ext.create('PICS.view.report.filter.StringFilter');
        this.add(filter);*/
    }
});    