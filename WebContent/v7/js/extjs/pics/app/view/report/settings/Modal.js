Ext.define('PICS.view.report.settings.Modal', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportsettingsmodal'],

    requires: [
        'PICS.view.report.settings.Tabs'
    ],

    draggable: false,
    height: 324,
    id: 'report_settings_modal',
    items: [{
        xtype: 'reportsettingstabs',
    }],
    layout: 'fit',
    modal: true,
    resizable: false,
    title: 'Edit Report',
    width: 352
});