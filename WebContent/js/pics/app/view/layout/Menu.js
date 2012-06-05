Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],

    autoLoad: {
        url: 'Menu.action',

        renderer: function (loader, response, active) {
            var toolbar = loader.getTarget();
            var menu_items = Ext.decode(response.responseText);

            Ext.each(menu_items, function (value, index) {
                if (menu_items[index].xtype == undefined) {
                    menu_items[index].height = 70;
                    menu_items[index].margin = 0;
                    menu_items[index].padding = '0px 10px';
                    menu_items[index].scale = 'large';
                } else if (menu_items[index].xtype == 'tbseparator') {
                    delete menu_items[index];
                }
            });

            toolbar.add({
                xtype: 'tbtext',
                text: '<a href="/"><img src="js/pics/app/resources/images/logo.png" /></a>',
                height: 30,
                id: 'logo',
                margin: '18px 20px',
                width: 30
            });

            toolbar.add(menu_items);
        }
    },
    border: 0,
    enableOverflow: true,
    height: 70,
    id: 'site_menu',
    padding: 0
});