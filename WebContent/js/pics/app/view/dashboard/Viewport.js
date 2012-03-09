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
	items: [{
		xtype: 'dashboardcolumn',
        items: [{
    		xtype: 'dashboardpanel',
    		title: 'Contractor Summary',
    		items: [{
                // xtype: 'reportdatagrid'
    		}]
    	},{
    		xtype: 'dashboardpanel',
    		title: 'Flags'
        }]
	},{
		xtype: 'dashboardcolumn',
        items: [{
    		xtype: 'dashboardpanel',
    		title: 'Contractor Summary'
    	},{
    		xtype: 'dashboardpanel',
    		title: 'Flags'
        }]
	}]
});