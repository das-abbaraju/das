Ext.define('PICS.view.report.Header', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportheader'],

    border: false,
    id: 'reportHeader',
    items: [{
        xtype: 'panel',
        border: false,
        id: 'reportTitle',
        html: ''
    }, {
        xtype: 'tbfill'
    }],
    layout: {
        align: 'middle',
        type: 'hbox'
    },
    margin: '5 0',
    padding: '0 20',

    constructor: function () {
        this.callParent(arguments);

        //create report buttons
        var panel = Ext.create('Ext.panel.Panel', {border:false}),
            userStatus = PICS.app.configuration;

        if (userStatus.get_is_owner() || userStatus.get_is_developer()) {
            var edit = Ext.create('Ext.button.Button', {
                action: 'edit',
                margin: '0 5 0 0',
                text: 'Edit'
            });
            var save = Ext.create('Ext.button.Button', {
                action: 'save',
                text: 'Save'
            });
            panel.add(edit, save);
            
            var copy = Ext.create('Ext.button.Button', {
                action: 'copy',
                margin: '0 0 0 5',
                text: 'Copy'
            });        
            panel.add(copy);
        }
        this.add(panel);
    }
});