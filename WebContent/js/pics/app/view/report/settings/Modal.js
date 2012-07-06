Ext.define('PICS.view.report.settings.Modal', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportsettingsmodal'],
    requires: [
        'PICS.view.report.settings.Tabs',
    ],

    height: 264,
    id: 'report_settings_modal',
    items: [{
        xtype: 'reportsettingstabs',
    }],
    layout: 'fit',
    modal: true,
    title: 'Edit Report',
    width: 352
});