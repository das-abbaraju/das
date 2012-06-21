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
                    menu_items[index].scale = 'large';

                    if (menu_items[index].menu != undefined) {
                    	menu_items[index].menu.plain = true;
                    }
                }
            });

            toolbar.add({
                height: 70,
                icon: 'js/pics/app/resources/images/logo.png',
                id: 'logo',
                padding: '0px 20px',
                scale: 'large',
                text: 'Dashboard',
                url: 'Home.action'
            });

            menu_items.splice(8, 0, {
                xtype: 'tbseparator',
                border: 1,
                height: 70,
                margin: '0px 0px 0px 20px'
            });

            menu_items[menu_items.length - 1].padding = '0px 20px';

            menu_items[1].menu.items.splice(1, 0, {
                xtype: 'menuseparator'
            });

            menu_items[1].menu.items.splice(2, 0, {
                xtype: 'tbtext',
                cls: 'menu-title',
                text: 'Favorites'
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