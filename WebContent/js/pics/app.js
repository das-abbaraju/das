Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'Ext.ux': './js/pics/app/ux'
    }
});

Ext.application({
    name: 'PICS',
    appFolder: 'js/pics/app',

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
            ['BeginsWith', 'beginswith'],
            ['NotBeginsWith', 'does not begin with'],
            ['EndsWith', 'endswith'],
            ['NotEndsWith', 'does not end with'],
            ['Equals', 'equals'],
            ['NotEquals', 'does not equal'],
            ['Empty', 'blank']
        ]
    },

    configuration: null,

    controllers: [
        'report.ReportController',
        'report.DataSetController',
        'report.FilterController',
        'report.SortController',
        'report.ColumnSelectorController',
        'report.ReportHeaderController'
    ],

    models: [
        'report.AvailableField',
        'report.Report'
    ],

    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.DataSets',
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
           url: 'ReportDynamic!getUserStatus.action?report=' + url.report,
           success: function (result) {
               var result = Ext.decode(result.responseText);

               // configuration closure
               PICS.app.configuration = (function config() {
                   return {
                       isEditable: function () {
                           return result.is_editable;
                       }
                   };
               })();

               // success callback
               if (options && options.success && typeof options.success == 'function') {
            	   options.success();
               }
           }
        });
    },

    createViewport: function () {
    	var that = this;

    	Ext.create('PICS.view.report.Viewport', {
    		listeners: {
    			render: function (component, eOpts) {
    				// remove loading background
    				Ext.get('loadingPage').dom.hidden = true;
     		   }
     	   }
        });
    }
});