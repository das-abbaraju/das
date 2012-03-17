Ext.define('PICS.view.dashboard.Viewport', {
	extend: 'Ext.container.Viewport',
    requires: [
        'PICS.view.dashboard.Panel',
        'PICS.view.dashboard.Column'
    ],
	layout: {
		align: 'stretch',
		type: 'hbox'
	},
});