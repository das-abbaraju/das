Ext.define('PICS.view.report.settings.SettingsModal', {
    extend: 'PICS.view.report.ReportModal',
    alias: ['widget.reportsettingsmodal'],

    requires: [
        'PICS.view.report.settings.SettingsTabs'
    ],

    draggable: false,
    height: 324,
    id: 'report_settings_modal',
    items: [{
        xtype: 'reportsettingstabs'
    }],
    layout: 'fit',
    modal: true,
    resizable: false,
    title: 'Edit Report',
    width: 352
});