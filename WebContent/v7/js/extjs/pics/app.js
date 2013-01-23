Ext.Loader.setConfig({
    enabled: true,
    disableCaching: false
});

Ext.application({
    name: 'PICS',

    requires: [
        'PICS.view.report.Viewport',
        'Ext.layout.container.Border',
        'Ext.resizer.Splitter',
        'Ext.grid.RowNumberer',
        'Ext.grid.column.Number',
        'PICS.ux.util.filter.FilterMultipleColumn',
        'PICS.data.ServerCommunication'
    ],

    constants: {
        APPFOLDER: '/v7/js/extjs/pics/app',
        DATESTORE: [
            ['LessThan', 'before'],
            ['GreaterThan', 'after'],
            ['Empty', 'is empty']
        ],
        NUMBERSTORE: [
            ['Equals', '='],
            ['GreaterThan', '>'],
            ['LessThan', '<'],
            ['GreaterThanOrEquals', '>='],
            ['LessThanOrEquals', '<='],
            ['Empty', 'is empty']
        ],
        TEXTSTORE: [
            ['Contains', 'contains'],
            ['NotContains', 'does not contain'],
            ['BeginsWith', 'begins with'],
            ['NotBeginsWith', 'does not begin with'],
            ['EndsWith', 'ends with'],
            ['NotEndsWith', 'does not end with'],
            ['Equals', 'equals'],
            ['NotEquals', 'does not equal'],
            ['Empty', 'is empty'],
            ['NotEmpty', 'is not empty']
        ]
    },

    controllers: [
        'report.ColumnFilterModal',
        'report.ColumnFunctionModal',
        'report.Filter',
        'report.Header',
        'report.Report',
        'report.ReportData',
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