Ext.application({
	name: 'PICS',
	
	//autoCreateViewport: true,
	launch: function() {
		Ext.create('Ext.container.Viewport', {
			title: 'Main',
			layout: {
				type: 'border',
				padding: 5
			},
			items: [{
				id: 'header',
				xtype: 'box',
				region: 'north',
				height: 40,
				border: false,
				html: '<header><h1>Header</h1></header>'
			}, {
				id: 'content',
                region: 'center',
                layout: 'fit',
                bodyPadding: 5,
				items: [{
					xtype: 'panel',
					region: 'center',
					layout: 'border',
					border: false,
					items: [{
						region: 'north',
						bodyPadding: 5,
						height: 100,
						collapsible: true,
						title: 'Search',
						html: 'filter'
					}, {
						region: 'center',
						bodyPadding: 5,
						border: false,
						html: 'list'
					}]
				}]
			}, {
				id: 'footer',
				xtype: 'box',
				region: 'south',
				height: 100,
				border: false,
				html: '<footer><ul><li>Footer</li></ul></footer>'
			}]
		});
	}
});