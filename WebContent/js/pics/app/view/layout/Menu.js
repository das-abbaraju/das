Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],
    
    autoLoad: {
        url: 'Menu.action',
        renderer: function (loader, response, active) {
            var toolbar = loader.getTarget();
            
            toolbar.add(Ext.decode(response.responseText));
        }
    }
});