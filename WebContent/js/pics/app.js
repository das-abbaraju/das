Ext.application({
	name: 'PICS',
	
	models: ['report.Report'],
	stores: ['report.Reports'],
	
	launch: function() {
		alert('hi');
	}
});