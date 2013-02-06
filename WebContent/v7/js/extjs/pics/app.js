Ext.Loader.setConfig({
    enabled: true,
    disableCaching: false
});

Ext.application({
    name: 'PICS',

    requires: [
        'PICS.view.report.Viewport',
        'PICS.data.ServerCommunication',
        'PICS.data.ServerCommunicationUrl'
    ],

    constants: {
        APPFOLDER: '/v7/js/extjs/pics/app'
    },

    controllers: [
        'report.ColumnFilterModal',
        'report.ColumnFunctionModal',
        'report.DataTable',
        'report.Filter',
        'report.Header',
        'report.Report',
        'report.SettingsModal'
    ],

    init: function () {
        // Override CSS3BorderRadius value which caused render problems in <IE9 when false.
        Ext.supports['CSS3BorderRadius'] = true;

        // Hack-ish remove class sniffers from Ext.EventManager (which attaches modrnizer type classes onto the body)
        Ext.getBody().removeCls('x-nbr x-nlg');
    },

    launch: function () {
        // save reference to application
        PICS.app = this;
        PICS.app.configuration = {
            isEditable: function () { return true; },
            isFavorite: function () { return true; },
            setIsFavorite: function (bool) {
                return true;
            }
        };
        
        PICS.data.ServerCommunication.loadAll({
            callback: this.createViewport,
            scope: this
        });
    },

    createViewport: function () {
        Ext.create('PICS.view.report.Viewport', {
            listeners: {
                render: function (component, eOpts) {
                    // remove loading background
                    var loading = Ext.get('loading_page');

                    if(loading) {
                        loading.remove();
                    }
                }
            }
        });
    }
});