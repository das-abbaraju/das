Ext.define('PICS.view.layout.Footer', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.layoutfooter'],

    xtype: 'toolbar',
    region: 'south',
    
    id: 'footer',
    height: 30,
    border: false,
    
    html: '&copy; 2012'
});