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

            menu_items.splice(8, 0, {
                xtype: 'tbseparator',
                border: 1,
                height: 70,
                margin: '0px 0px 0px 20px'
            });

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