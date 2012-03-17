Ext.Loader.setConfig({
    enabled: true
});

Ext.application({
    name: 'PICS',
    appFolder: 'js/pics/app',

    controllers: [ 'dashboard.DashboardController' ],
    models: [],
    stores: [],
    
    launch: function() {
        Ext.create('PICS.view.dashboard.Viewport');
    }
});
