Ext.Loader.setConfig({
	enabled : true
});

Ext.application({
	name : 'PICS',
	appFolder : 'js/pics/app',
	models : [ 'report.ReportRow' ],
	stores : [ 'report.ReportData' ],

	launch : function() {
		Ext.create('PICS.view.report.Viewport');
	}
});