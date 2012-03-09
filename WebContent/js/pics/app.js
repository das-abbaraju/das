Ext.Loader.setConfig({
	enabled : true
});

Ext.application({
	name: 'PICS',
	appFolder: 'js/pics/app',
	
    controllers: [
        'report.ColumnSelectorController',
        // TODO: should be renamed ReportOptionsFilterController
        'report.FilterController',
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
        'report.Reports',
        'report.ReportsColumn',
        'report.ReportsFilter',
        'report.ReportData'
    ],
    
	launch: function() {
		Ext.create('PICS.view.report.Viewport');
	}
});