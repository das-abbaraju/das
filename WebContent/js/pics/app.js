Ext.Loader.setConfig({
	enabled: true,
	paths: {
        'Ext.ux': './js/pics/app/ux'
    }
});

Ext.application({
	name: 'PICS',
	appFolder: 'js/pics/app',
	
    controllers: [
        'report.ColumnSelectorController',
        // TODO: should be renamed ReportOptionsFilterController
        'report.FilterController',
        'report.SortController',        
        'report.ReportController',
        'report.ReportOptionsController'
        ],

	models: [
	    'report.AvailableField', 
        'report.Report',
        'report.SimpleField', 
        'report.SimpleFilter',
        'report.SimpleSort'
    ],
    
	stores: [
	    'report.AvailableFields',
	    'report.AvailableFieldsByCategory',
	    'report.ReportData',
        'report.Reports',
        'report.ReportsColumn',
        'report.ReportsFilter',
        'report.ReportsSort'
    ],
    
	launch: function() {
		Ext.create('PICS.view.report.Viewport');
	}
});