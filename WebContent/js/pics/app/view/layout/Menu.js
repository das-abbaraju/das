Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],

    autoLoad: {
        url: 'Menu.action',

        renderer: function (loader, response, active) {
            var toolbar = loader.getTarget();
            var menu_items = Ext.decode(response.responseText);

            menu_items = this.configureMenuItems(menu_items);

            console.log(menu_items);

            // configure all submenus

            // add logo + dashboard

            // modifications to reports menu
            // add favorites category

            // add fill

            // add search textfield

            // add separator

            // modifications to user menu
            // add gear to text

            toolbar.add({
                height: 70,
                icon: 'js/pics/app/resources/images/logo.png',
                id: 'logo',
                padding: '0px 20px',
                scale: 'large',
                // TODO pass in translated "Dashboard"
                text: 'Dashboard',
                // TODO pass in actual home page
                url: 'Home.action'
            });

            if (menu_items[0].menu.items.length > 1) {
                menu_items[0].menu.items.splice(1, 0, {
                    xtype: 'menuseparator',
                    width: 2
                });

                menu_items[0].menu.items.splice(2, 0, {
                    xtype: 'tbtext',
                    cls: 'menu-title',
                    // TODO pass in translated "Favorites"
                    text: 'Favorites'
                });
            }

            var user_menu = menu_items.pop();
            user_menu.padding = '0px 20px';

            menu_items.push({
               xtype: 'tbfill'
            });

            menu_items.push({
                xtype: 'textfield',
                name: 'searchTerm',
                emptyText: 'enter search term',
            });

//            menu_items[menu_items.length - 1].padding = '0px 20px';

            toolbar.add(menu_items);

            toolbar.add({
                xtype: 'tbseparator',
                border: 1,
                height: 70,
                margin: '0px 0px 0px 20px'
            });

            toolbar.add(user_menu);
        },

        configureMenuItems: function (menu_items) {
            var that = this;

            // top level items
            Ext.each(menu_items, function (menu_item, index) {
                var menu = menu_item.menu;

                if (menu != undefined) {
                    menu = that.configureSubmenuItems(menu);
                    menu.plain = true;
                }

                menu_item.height = 70;
                menu_item.menu = menu;
                menu_item.scale = 'large';

                menu[index] = menu_item;
            });

            return menu_items;
        },

        configureSubmenuItems: function (menu) {
            var menu_items = menu.items;

            if (menu_items != undefined) {
                // sub items
                Ext.each(menu_items, function (menu_item, index) {
                    var submenu = menu_item.menu;

                    if (submenu != undefined) {
                        submenu.plain = true;
                    }

                    menu_items[index].menu = submenu;
                });

                menu.items = menu_items;
            }

            return menu;
        }
    },

    border: 0,
    enableOverflow: true,
    height: 70,
    id: 'site_menu',
    padding: 0
});