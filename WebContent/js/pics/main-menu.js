Ext.onReady(function() {
    Ext.create('Ext.toolbar.Toolbar', {
        autoLoad: {
            enableOverflow: true,
            renderer: function (loader, response, active) {
                var toolbar = loader.getTarget();
                toolbar.add(Ext.decode(response.responseText));
            },
            url: 'Menu.action'
        },
        renderTo: "MainMenu"
    });
});
