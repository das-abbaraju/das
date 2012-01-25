Ext.Loader.setConfig({
	enabled : true
});

Ext.application({
	name: 'PICS',
	appFolder: 'js/pics/app',
	models: [ 'report.Report', 'report.ReportRow', 'report.AvailableField', 'report.Parameter', 'report.SimpleField', 'report.SimpleFilter' ],
	stores: [ 'report.Reports', 'report.ReportData', 'report.AvailableFields' ],

	launch: function() {
		Ext.create('PICS.view.report.Viewport');
	}
});