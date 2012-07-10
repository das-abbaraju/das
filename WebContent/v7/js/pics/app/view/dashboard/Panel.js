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
    flex: 1,
    title: 'My Panel',
});