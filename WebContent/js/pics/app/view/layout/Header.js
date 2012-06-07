Ext.define('PICS.view.layout.Header', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.layoutheader'],

    requires: [
       'PICS.view.layout.Menu'
    ],

    border: 0,
    id: 'header',
    height: 72, // hack to get box-shadow (should be 70)
    items: [{
        xtype: 'layoutmenu'
    }]
});