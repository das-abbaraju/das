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
                        console.log('setting menu[' + index + '].menu.plain to true');
                        menu_items[index].menu.plain = true;
                    }
                }
            });

            toolbar.add({
                height: height,
                icon: 'js/pics/app/resources/images/logo.png',
                id: 'logo',
                padding: '0px 20px',
                scale: 'large',
                // TODO pass in translated "Dashboard"
                text: 'Dashboard',
                // TODO pass in actual home page
                url: 'Home.action'
            });

            // TODO check length of menu_items before adding favorites

            menu_items[0].menu.items.splice(1, 0, {
                xtype: 'menuseparator'
            });

            menu_items[0].menu.items.splice(2, 0, {
                xtype: 'tbtext',
                cls: 'menu-title',
                // TODO pass in translated "Favorites"
                text: 'Favorites'
            });

            var user_menu = menu_items.pop();
            user_menu.padding = '0px 20px';
            // Just for fun
            user_menu.text += ' \u2699';

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
                height: height,
                margin: '0px 0px 0px 20px'
            });

            toolbar.add(user_menu);
        }
    },

    border: 0,
    enableOverflow: true,
    height: 70,
    id: 'site_menu',
    padding: 0
});