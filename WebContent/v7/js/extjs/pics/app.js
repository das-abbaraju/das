Ext.Loader.setConfig({
    enabled: true,
    disableCaching: false
});

Ext.application({
    name: 'PICS',

    requires: [
        'PICS.view.report.Viewport',
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

        // Attached to PICS global b/c IE8's console does not display properties of a returned object (e.g., from ExtJS's class getters)
        Ext.apply(PICS, {
            _stack_trace: [], // Cannot make it truly private for same reason as above

            createStackTrace: function () {
                this._stack_trace = arguments.callee.caller.trace();
            },

            getStackTrace: function () {
                return this._stack_trace;
            }
        });

        // Attached to PICS global b/c IE8's console does not display properties of a returned object (e.g., from ExtJS's class getters)
        Ext.apply(PICS, {
            _stack_trace: [], // Cannot make it truly private for same reason as above

            createStackTrace: function () {
                this._stack_trace = arguments.callee.caller.trace();
            },

            getStackTrace: function () {
                return this._stack_trace;
            }
        });

        Ext.apply(PICS, {
            updateDocumentTitle: this.updateDocumentTitle
        });
        
        PICS.data.ServerCommunication.loadAll({
            success_callback: this.createViewport,
            scope: this
        });
    },

    createViewport: function () {
        Ext.create('PICS.view.report.Viewport', {
            listeners: {
                render: function (component, eOpts) {
                    var loading = Ext.get('loading_page'),
                        report_store = Ext.StoreManager.get('report.Reports'),
                        report = report_store.first();

                    PICS.updateDocumentTitle();

                    // ExtJS has its own onbeforeunload, but it doesn't work in FF.
                    // The native onbeforeunload used here, however, is broadly compatible.
                    window.onbeforeunload = function () {
                        var report_store = Ext.StoreManager.get('report.Reports'),
                            report = report_store.first(),
                            report_has_unsaved_changes = report.getHasUnsavedChanges();

                        if (report_has_unsaved_changes) {
                            return PICS.text('Report.execute.unsavedChanges.title');
                        }
                    };

                    if (loading) {
                        loading.remove();
                    }
                }
            }
        });
    },
    
    updateDocumentTitle: function () {
        var report_store = Ext.StoreManager.get('report.Reports'),
            report = report_store.first(),
            report_name = report.get('name');
        
        document.title = report_name;
    }
});