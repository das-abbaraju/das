Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],
    
    autoLoad: {
        renderer: function (loader, response, active) {
            var toolbar = loader.getTarget();

            toolbar.add(Ext.decode(response.responseText));
            
            toolbar.add({
                xtype: 'tbfill'
            },{
                xtype: 'textfield',
                name : 'searchTerm',
                emptyText: 'enter search term'
            },
            'Search');
        },
        url: 'Menu.action'
    },
    enableOverflow: true
});