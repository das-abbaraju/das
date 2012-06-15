Ext.scopeResetCSS = true;

Ext.Loader.setConfig({
    enabled: true,
    paths: {
        PICS: './js/pics/app'
    }
});

window.onload = function () {
    Ext.onReady(function ()  {
        var menu = Ext.create('PICS.view.layout.Menu', {
        	renderTo: 'site_navigation'
        });

        Ext.EventManager.onWindowResize(function () {
            menu.doLayout();
        });
    });
};