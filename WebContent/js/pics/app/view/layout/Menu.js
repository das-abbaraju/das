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

            toolbar.addLogoAndDashboard(menu_items[0]);

            toolbar.addFavoritesLabel(menu_items[1]);

            toolbar.addFill(menu_items);

            toolbar.addSearchBox(menu_items);

            toolbar.addSeparator(menu_items);

            toolbar.addUserMenu(user_menu, menu_items);

            toolbar.add(menu_items);
        }
    },

    /** @param {Array<Object>} menu_items */
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

    /** @param {Object} menu */
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

    /** @param {Object} dashboard_button */
    addLogoAndDashboard: function (dashboard_button) {
        if (dashboard_button === undefined) {
            return;
        }

        dashboard_button.height = 70;
        dashboard_button.icon = 'js/pics/app/resources/images/logo.png';
        dashboard_button.padding = '0px 20px';
        dashboard_button.scale = 'large';
    },

    /** @param {Object} report_menu */
    addFavoritesLabel: function (report_menu) {
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

    /** @param {Array<Object>} menu_items */
    addFill: function (menu_items) {
        menu_items.push({
           xtype: 'tbfill'
        });
    },

    /** @param {Array<Object>} menu_items */
    addSearchBox: function (menu_items) {
        menu_items.push({
            xtype: 'textfield',
            name: 'searchTerm',
            // TODO pass in translated 'enter search term'
            emptyText: 'enter search term',
        });
    },

    /** @param {Array<Object>} menu_items */
    addSeparator: function (menu_items) {
        menu_items.push({
            xtype: 'tbseparator',
            border: 1,
            height: 70,
            margin: '0px 0px 0px 20px'
        });
    },

    /**
     * @param {Object} user_menu
     * @param {Array<Object>} menu_items
     */
    addUserMenu: function (user_menu, menu_items) {
        user_menu.padding = '0px 20px';
        // TODO add gear to text
        menu_items.push(user_menu);
    }
});