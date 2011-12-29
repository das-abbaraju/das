Ext.define('PICS.view.layout.Header', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.layoutheader'],
    
    xtype: 'box',
    region: 'north',
   
    id: 'header',
    height: 35,
    border: false,
    
    html: '<header><img src="http://localhost:8080/picsWeb2/images/logo_sm.png" /></header>'
});