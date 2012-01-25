Ext.Loader.setConfig({
	enabled : true
});

Ext.application({
	name: 'PICS',
	appFolder: 'js/pics/app',
	
	//controllers: [ 'report.Reports' ],
	models: [ 'report.Report', 'report.ReportRow', 'report.AvailableField', 'report.SimpleField', 'report.SimpleFilter', 'report.SimpleSort' ],
	stores: [ 'report.Reports', 'report.ReportData', 'report.AvailableFields' ],

	launch: function() {
		Ext.create('PICS.view.report.Viewport');
	}
});