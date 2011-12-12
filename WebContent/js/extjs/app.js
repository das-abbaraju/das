Ext.application({
	name: 'PICS',
	//autoCreateViewport: true,
	launch: function() {
		Ext.create('Ext.container.Viewport', {
			title: 'Border',
			layout: 'border',
			items: [{
				region: 'north',
				html: '<header><h1>Header</h1></header>',
				height: 100,
				border: false
			}, {
				xtype: 'panel',
				region: 'center',
				layout: 'fit',
				title: 'Hello PICS',
				html: 'content',
				height: 100
			},{
				region: 'south',
				html: '<footer><ul><li>Footer</li></ul></footer>',
				height: 100,
				border: false
			}]
		});
	}
});