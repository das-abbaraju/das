Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],
    
    autoLoad: {
        renderer: function (loader, response, active) {
            var toolbar = loader.getTarget();
            
            toolbar.add({
                xtype: 'tbtext',
                html: '<div style="background-color: #FFF; border: 1px solid #3884C7; border-top: 1px solid #2D6A9F; border-bottom: 1px solid #4C91CD;padding: 0px 2px;"><img src="images/logo_sm.png" style="position: relative; top: 2px; width: 60px;" /></div>'
            });
            
            toolbar.add(Ext.decode(response.responseText));
            
            toolbar.add({
                xtype: 'textfield',
                name : 'searchTerm',
                emptyText: 'enter search term'
            });
            
            toolbar.add({
                text: 'Search'
            });
        },
        url: 'Menu.action'
    },
    enableOverflow: true
});