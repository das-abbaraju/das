Ext.Loader.setConfig({
	enabled : true
});

Ext.application({
	name: 'PICS',
	appFolder: 'js/pics/app',
	
	//controllers: [ 'report.Reports' ],
	
	models: [
	    'report.AvailableField', 
        'report.Report', 
        'report.ReportRow', 
        'report.SimpleField', 
        'report.SimpleFilter', 
        'report.SimpleSort' 
    ],
    
	stores: [
	    'report.AvailableFields',
        'report.Reports', 
        'report.ReportData'
    ],

	launch: function() {
		Ext.create('PICS.view.report.Viewport');
	}
});