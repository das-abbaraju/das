Ext.Loader.setConfig({
    enabled: true,
    disableCaching: false
});

Ext.application({
    name: 'PICS',

    requires: [
        'PICS.view.report.Viewport',
        'PICS.Ajax',
        'PICS.data.Exception',
        'PICS.data.ServerCommunication',
        'PICS.data.ServerCommunicationUrl',
        'PICS.data.ColumnType',
        'PICS.data.FilterType'
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

        PICS.app.updateDocumentTitle = function () {
            var report_store = Ext.StoreManager.get('report.Reports'),
                report = report_store.first(),
                report_name = report.get('name');

            document.title = report_name;
        },

        PICS.data.ServerCommunication.loadAll({
            success_callback: this.createViewport,
            scope: this
        });
    },

    createViewport: function () {
        Ext.create('PICS.view.report.Viewport', {
            listeners: {
                render: function (component, eOpts) {
                    // remove loading background
                    var loading = Ext.get('loading_page');

                    PICS.app.updateDocumentTitle();

                    Ext.EventManager.on(window, 'beforeunload', function () {
                        var report_store = Ext.StoreManager.get('report.Reports'),
                            report = report_store.first();

                        if (report.has_unsaved_changes) {
                            return 'Your unsaved changes will be lost.';
                        } else {
                            return;
                        }
                    });

                    if (loading) {
                        loading.remove();
                    }
                }
            }
        });
    }
});