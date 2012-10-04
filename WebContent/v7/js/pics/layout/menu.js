Ext.scopeResetCSS = true;

Ext.Loader.setConfig({
    enabled: true,
    paths: {
        PICS: './v7/js/extjs/pics/app'
    }
});

window.onload = function () {
    Ext.onReady(function ()  {

        if (Ext.query('#site_navigation').length) {
            var menu = Ext.create('PICS.view.layout.Menu', {
                renderTo: 'site_navigation',
            });

            Ext.EventManager.onWindowResize(function () {
                menu.doLayout();
            });

            menu.setLoading('Loading menu...');
        }
    });
};