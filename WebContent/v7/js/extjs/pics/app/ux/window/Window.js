Ext.define('PICS.ux.window.Window', {
    extend: 'Ext.window.Window',

    initComponent: function () {
        this.listeners = {
            show: function (cmp, eOpts) {
                var body_el = Ext.getBody();

                this.mon(body_el, 'click', function (event, html, eOpts) {
                    cmp.close();
                }, cmp, {
                    delegate: '.x-mask'
                });
            }
        };

        this.callParent(arguments);
    }
});