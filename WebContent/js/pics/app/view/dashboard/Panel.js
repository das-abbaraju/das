Ext.define('PICS.view.dashboard.Panel', {
    extend: 'Ext.panel.Panel',
	alias: ['widget.dashboardpanel'],

    draggable: true,
    margin: 2,
    layout: {
        type: 'fit'
    },
    closable: true,
    collapsible: true,
    title: 'My Panel',
    flex: 1
});