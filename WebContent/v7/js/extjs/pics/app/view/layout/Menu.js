Ext.override(Ext.menu.Menu, {
    showBy: function(cmp, pos, off) {
        var me = this,
            xy,
            region;

        if (me.floating && cmp) {
            me.layout.autoSize = true;

            // show off-screen first so that we can calc position without causing a visual jump
            me.doAutoRender();
            delete me.needsLayout;

            // Component or Element
            cmp = cmp.el || cmp;

            // Convert absolute to floatParent-relative coordinates if necessary.
            xy = me.el.getAlignToXY(cmp, pos || me.defaultAlign, off);
            if (me.floatParent) {
                region = me.floatParent.getTargetEl().getViewRegion();
                xy[0] -= region.x;
                xy[1] -= region.y;
            }

            // custom menu positioning
            xy[1] += 3;

            me.showAt(xy);
        }
        return me;
    },

    doConstrain : function() {
        var me = this,
            y = me.el.getY(),
            max, full,
            vector,
            returnY = y, normalY, parentEl, scrollTop, viewHeight;

        delete me.height;
        me.setSize();
        full = me.getHeight();
        if (me.floating) {
            //if our reset css is scoped, there will be a x-reset wrapper on this menu which we need to skip
            parentEl = Ext.fly(me.el.getScopeParent());
            scrollTop = parentEl.getScroll().top;
            viewHeight = parentEl.getViewSize().height;
            //Normalize y by the scroll position for the parent element.  Need to move it into the coordinate space
            //of the view.
            normalY = y - scrollTop;
            max = me.maxHeight ? me.maxHeight : viewHeight - normalY;
            if (full > viewHeight) {
                max = viewHeight;
                //Set returnY equal to (0,0) in view space by reducing y by the value of normalY
                returnY = y - normalY;
            } else if (max < full) {
                returnY = y - (full - max);
                max = full;
            }
        }else{
            max = me.getHeight();
        }
        // Always respect maxHeight
        if (me.maxHeight){
            max = Math.min(me.maxHeight, max);
        }
        if (full > max && max > 0){
            me.layout.autoSize = false;
            me.setHeight(max);
            if (me.showSeparator){
                me.iconSepEl.setHeight(me.layout.getRenderTarget().dom.scrollHeight);
            }
        }
        vector = me.getConstrainVector(me.el.getScopeParent());
        if (vector) {
            me.setPosition(me.getPosition()[0] + vector[0]);
        }
        me.el.setY(returnY);
    }
});

Ext.define('PICS.view.layout.Menu', {
    extend: 'Ext.toolbar.Toolbar',
    alias: ['widget.layoutmenu'],

    requires: [
        'PICS.view.layout.SearchBox'
    ],

    border: 0,
    enableOverflow: true,
    height: 50,
    id: 'site_menu',
    padding: 0,

    autoLoad: {
        url: '/Menu.action',

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

            // Remove the loading indicator
            toolbar.setLoading(false);
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
            menu_item.hrefTarget = '';
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
        user_menu.text += ' <i class="icon-cog icon-large"></i>';

        menu_items.push(user_menu);
    },

    styleDashboardMenu: function (dashboard_menu) {
        if (dashboard_menu === undefined) {
            return;
        }

        dashboard_menu.height = 50;

        if (Ext.supports.Svg) {
            dashboard_menu.icon = '/v7/img/logo/logo-icon.svg';
        } else {
            dashboard_menu.icon = '/v7/img/logo/logo-icon.png';
        }

        dashboard_menu.padding = '0px 10px 0px 20px';
        dashboard_menu.scale = 'large';
        dashboard_menu.cls = 'dashboard';
    },

    styleReportsMenu: function (report_menu) {
        var items = report_menu && report_menu.menu && report_menu.menu.items;
        
        // hack-ish to determine if we should "switch" out the menu separator and favorites label
        // backend includes at least 4 items (manage reports, legacy, reports, separator, favorites label)
        if (items === undefined || items.length < 4) {
            return;
        }

        items.splice(2, 1, {
            xtype: 'menuseparator'
        });

        items.splice(3, 1, {
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
