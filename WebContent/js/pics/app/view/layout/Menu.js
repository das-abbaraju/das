Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],

    border: 0,
    enableOverflow: true,
    height: 70,
    id: 'site_menu',
    padding: 0,

    autoLoad: {
        url: 'Menu.action',

        renderer: function (loader, response, active) {
            var toolbar = loader.getTarget();
            var menu_items = Ext.decode(response.responseText);

            toolbar.configureToolbarMenuItems(menu_items);

            // Save the user menu to add to the end later, only after configuring
            var user_menu = menu_items.pop();

            toolbar.styleDashboardMenu(menu_items[0]);

            toolbar.styleReportsMenu(menu_items[1]);

            toolbar.addFill(menu_items);

            toolbar.addSearchBox(menu_items);

            toolbar.addSeparator(menu_items);

            toolbar.addUserMenu(user_menu, menu_items);

            // Add all menus items to toolbar
            toolbar.add(menu_items);

            cmp = toolbar;

            if (cmp.enableOverflow) {
                var handler = cmp.layout.overflowHandler;

                if (handler && handler.menu) {
                    h = handler;

                    handler.menu.addClass('x-menu-plain');
                    handler.menu.addClass('candy-menu');

                    if (handler.menu.items.length) {
                        handler.menuItems[0].height = 10;
                        handler.menuItems[0].padding = 0;

                        /*handler.menuItems[0] = Ext.create('Ext.button.Button', {
                            text: 'test'
                        });*/
                    }
                }
            }
        }
    },

    configureToolbarMenuItems: function (menu_items) {
        var that = this;

        Ext.each(menu_items, function (menu_item, index) {
            // Check if this is a submenu
            if (menu_item.menu !== undefined) {
                that.configureSubmenu(menu_item.menu);
            }

            // Only apply this styling to top-level toolbar menu items
            menu_item.height = 70;
            menu_item.scale = 'large';
        });
    },

    configureSubmenu: function (menu) {
        var that = this;

        // All menus get this styling
        menu.plain = true;
        menu.hideMode = 'display';

        Ext.each(menu.items, function (menu_item, index) {
            // Check if this is a submenu
            if (menu_item.menu !== undefined) {
                that.configureSubmenu(menu_item.menu);
            }
        });
    },

    styleDashboardMenu: function (dashboard_menu) {
        if (dashboard_menu === undefined) {
            return;
        }

        dashboard_menu.height = 70;
        dashboard_menu.icon = 'js/pics/app/resources/images/logo.png';
        dashboard_menu.padding = '0px 20px';
        dashboard_menu.scale = 'large';
    },

    styleReportsMenu: function (report_menu) {
        var items = report_menu && report_menu.menu && report_menu.menu.items;

        if (items === undefined || items.length < 2) {
            return;
        }

        items.splice(1, 0, {
            xtype: 'menuseparator',
            width: 2
        });

        items.splice(2, 0, {
            xtype: 'tbtext',
            cls: 'menu-title',
            // TODO pass in translated "Favorites"
            text: 'Favorites'
        });
    },

    addFill: function (menu_items) {
        menu_items.push({
           xtype: 'tbfill'
        });
    },

    addSearchBox: function (menu_items) {
        menu_items.push({
            xtype: 'textfield',
            name: 'searchTerm',
            // TODO pass in translated 'enter search term'
            emptyText: 'enter search term',
        });
    },

    addSeparator: function (menu_items) {
        menu_items.push({
            xtype: 'tbseparator',
            border: 1,
            height: 70,
            margin: '0px 0px 0px 20px'
        });
    },

    addUserMenu: function (user_menu, menu_items) {
        user_menu.padding = '0px 20px';
        user_menu.text += ' <i class="icon-cog icon-large"></i>';

        menu_items.push(user_menu);
    }
});
