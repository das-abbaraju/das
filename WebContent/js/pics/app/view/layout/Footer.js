Ext.define('PICS.view.layout.Footer', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutfooter'],

    id: 'footer',
    height: 30,
    border: false,
    
    items: [{
        xtype: 'tbtext',
        text: '&copy; 2012'
    }]
});