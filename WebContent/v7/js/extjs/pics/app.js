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
        'PICS.ux.util.filter.FilterMultipleColumn'
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
        'report.Report',
        'report.ReportData',
        'report.ReportHeader',
        'report.SettingsModal'
    ],

    init: function () {
        // Override CSS3BorderRadius value which caused render problems in <IE9 when false.
        Ext.supports['CSS3BorderRadius'] = true;

        // Hack-ish remove class sniffers from Ext.EventManager (which attaches modrnizer type classes onto the body)
        Ext.getBody().removeCls('x-nbr x-nlg');
    },

    launch: function () {
        var that = this;

        // save reference to application
        PICS.app = this;
        PICS.app.configuration = {
            isEditable: function () { return true; },
            isFavorite: function () { return true; },
            setIsFavorite: function (bool) {
                return true;
            }
        };
        
        Ext.Ajax.request({
            url: '/ReportApi.action?report=1',
            params: {
                includeReport: true,
                includeColumns: true,
                includeFilters: true,
                includeData: true
            },
            success: function (response) {
                var data = response.responseText,
                    json = Ext.JSON.decode(data),
                    report_store = Ext.StoreManager.get('report.Reports'),
                    column_store = Ext.StoreManager.get('report.Columns'),
                    filter_store = Ext.StoreManager.get('report.Filters');

                report_store.loadRawData(json);
                column_store.loadRawData(json);
                filter_store.loadRawData(json);
                
                that.createViewport.apply(that);
            }
        });
    },

    getConfiguration: function (options) {
        var url = Ext.Object.fromQueryString(document.location.search);

        Ext.Ajax.request({
           url: '/ReportData!configuration.action?report=' + url.report,
           success: function (result) {
               var result = Ext.decode(result.responseText);

               // configuration closure
               PICS.app.configuration = (function config() {
                   return {
                       isEditable: function () {
                           return result.editable;
                       },
                       isFavorite: function () {
                           return result.favorite
                       },
                       setIsFavorite: function (bool) {
                           result.favorite = bool;
                       }
                   };
               }());

               // success callback
               if (options && options.success && typeof options.success == 'function') {
                   options.success();
               }
           }
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