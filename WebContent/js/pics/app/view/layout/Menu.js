Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],

    autoLoad: {
        url: 'Menu.action',

        renderer: function (loader, response, active) {
            var toolbar = loader.getTarget(),
                menu_items = Ext.decode(response.responseText),
                height = 70;

            Ext.each(menu_items, function (value, index) {
                if (menu_items[index].xtype == undefined) {
                    menu_items[index].height = height;
                    menu_items[index].scale = 'large';

                    if (menu_items[index].menu != undefined) {
//                        console.log('setting menu[' + index + '].menu.plain to true');
                        menu_items[index].menu.plain = true;
                    }
                }
            });

            // Add the logo
            toolbar.add({
                height: height,
                icon: 'js/pics/app/resources/images/logo.png',
                id: 'logo',
                padding: '0px 20px',
                scale: 'large',
                text: 'Dashboard',
                url: 'Home.action'
            });

            // Insert a separator
            menu_items[0].menu.items.splice(1, 0, {
                xtype: 'menuseparator'
            });

            // Insert a favorites item
            menu_items[0].menu.items.splice(2, 0, {
                xtype: 'tbtext',
                cls: 'menu-title',
                text: 'Favorites'
            });

            menu_items.splice(5, 0, {
               xtype: 'tbfill'
            });

            // Search
            menu_items.splice(6, 0, {
                xtype: 'textfield',
                name: 'searchTerm',
                emptyText: 'enter search term'
            });

            // Insert a separator
            menu_items.splice(7, 0, {
                xtype: 'tbseparator',
                border: 1,
                height: height,
                margin: '0px 0px 0px 20px'
            });

            // Add padding to the last menu item
            menu_items[menu_items.length - 1].padding = '0px 20px';

            toolbar.add(menu_items);
        }
    },

    border: 0,
    enableOverflow: true,
    height: 70,
    id: 'site_menu',
    padding: 0
});