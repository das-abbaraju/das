Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'Ext.ux': './v7/js/extjs/pics/app/ux',
        PICS: './v7/js/extjs/pics/app'
    }
});

Ext.require([
    'PICS.store.report.AvailableFields',
    'PICS.store.report.AvailableFieldsByCategory',
    'PICS.store.report.ReportDatas',
    'PICS.store.report.Reports',
    'PICS.view.layout.SearchBox'
    //'PICS.view.report.Viewport'
]);

Ext.application({
    name: 'PICS',
    appFolder: 'v7/js/extjs/pics/app',

    constants: {
        NUMBERSTORE: [
            ['Equals', '='],
            ['GreaterThan', '>'],
            ['LessThan', '<'],
            ['GreaterThanOrEquals', '>='],
            ['LessThanOrEquals', '<='],
            ['Empty', 'blank']
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
            ['Empty', 'blank']
        ]
    },

    configuration: null,

    controllers: [
        'report.AvailableFieldModal',
        'report.Filter',
        'report.Report',
        'report.ReportData',
        'report.ReportHeader',
        'report.SettingsModal'
    ],

    models: [
        'report.AvailableField',
        'report.Report'
    ],

    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.ReportDatas',
        'report.Reports'
    ],

    launch: function () {
    	var that = this;

    	// save reference to application
        PICS.app = this;

        this.getConfiguration({
        	success: function () {
        		that.createViewport.apply(that);
        	}
        });
    },

    getConfiguration: function (options) {
        var url = Ext.Object.fromQueryString(document.location.search);

        Ext.Ajax.request({
           url: 'ReportDynamic!configuration.action?report=' + url.report,
           success: function (result) {
               var result = Ext.decode(result.responseText);

               // configuration closure
               PICS.app.configuration = (function config() {
                   return {
                       isEditable: function () {
                           return result.is_editable;
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
    				Ext.get('loading_page').remove();
     		   }
     	   }
        });
    }
});