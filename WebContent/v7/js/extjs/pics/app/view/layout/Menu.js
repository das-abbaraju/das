Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],

    border: 0,
    enableOverflow: true,
    height: 50,
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

            toolbar.styleOverflowMenu();
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
            menu_item.height = 50;
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

    addFill: function (menu_items) {
        menu_items.push({
           xtype: 'tbfill',
           minWidth: 50
        });
    },

    addSearchBox: function (menu_items) {
        var search_box = Ext.create('PICS.view.layout.SearchBox');

        menu_items.push(search_box);
    },

    addSeparator: function (menu_items) {
        menu_items.push({
            xtype: 'tbseparator',
            border: 1,
            height: 50,
            margin: '0px 0px 0px 20px'
        });
    },

    addUserMenu: function (user_menu, menu_items) {
        user_menu.padding = '0px 20px 0px 20px';
        user_menu.text += '<i class="icon-cog icon-large"></i>';

        menu_items.push(user_menu);
    },

    styleDashboardMenu: function (dashboard_menu) {
        if (dashboard_menu === undefined) {
            return;
        }

        dashboard_menu.height = 50;
        dashboard_menu.icon = 'v7/js/extjs/pics/app/resources/images/logo.png';
        dashboard_menu.padding = '0px 10px 0px 20px';
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

    styleOverflowMenu: function () {
        if (!this.enableOverflow) {
            return;
        }

        function removeMenuItems(menu_trigger, items) {
            var remove_items = [];

            Ext.each(items, function (item, index) {
                if (item.xtype == 'menuseparator' || item.xtype == 'tbfill') {
                    remove_items.push(item);
                }
            });

            Ext.each(remove_items, function (item, index) {
                menu_trigger.remove(item);
            });
        }

        function styleMenuItems(items) {
            Ext.each(items, function (item, index) {
                item.height = 'auto';
                item.padding = '0px';

                if (item.xtype == 'textfield') {
                    item.labelWidth = 0;
                    item.margin = '5px';
                }
            });
        }

        var handler = this.layout && this.layout.overflowHandler;
        var menu = handler && handler.menu;

        if (menu !== undefined) {
            var menu_trigger = handler && handler.menuTrigger;
            var menu_trigger_menu = menu_trigger.menu;

            menu_trigger_menu.on('beforeshow', function (cmp, eOpts) {
                var items = cmp.items && cmp.items.items;

                if (items) {
                    removeMenuItems(cmp, items);
                    styleMenuItems(items);
                }
            });

            menu.addClass('x-menu-plain');
            menu.addClass('x-menu-overflow');
        }
    }
});
