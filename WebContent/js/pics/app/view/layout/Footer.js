Ext.define('PICS.view.layout.Footer', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutfooter'],

    border: false,
    height: 30,
    id: 'footer',
    items: [{
        xtype: 'tbtext',
        text: '&copy; 2012'
    }]
});