Ext.Loader.setConfig({
    enabled: true
});

Ext.application({
	name: 'PICS',
	
	appFolder: 'js/pics/app',
	
	models: ['report.Report'],
	stores: ['report.Reports'],
	
	launch: function() {
	    Ext.create('PICS.view.report.Viewport');
	}
});