Ext.define('PICS.view.layout.Header', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.layoutheader'],

    requires: [
       'PICS.view.layout.Menu'
    ],

    border: 0,
    id: 'header',
    height: 52, // hack to get box-shadow (should be 50)
    items: [{
        xtype: 'layoutmenu'
    }]
});