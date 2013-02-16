Ext.define('PICS.view.report.settings.SettingsModalTabs', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.reportsettingsmodaltabs',

    requires: [
        'PICS.view.report.settings.CopySetting',
        'PICS.view.report.settings.EditSetting',
        'PICS.view.report.settings.ExportSetting',
        'PICS.view.report.settings.PrintSetting',
        'PICS.view.report.settings.share.ShareSetting'
    ],

    border: false,
    items: [{
        xtype: 'reporteditsetting'
    }, {
        xtype: 'reportcopysetting'
    }, {
        xtype: 'reportsharesetting'
    }, {
        xtype: 'reportexportsetting'
    }, {
        xtype: 'reportprintsetting'
    }],
    tabBar: {
        border: false,
        height: 60,
        id: 'report_settings_modal_tabbar',
        // controls default proportions for tab buttons
        defaults: {
            height: 60,
            width: 70
        }
    },
    tabPosition: 'bottom'
});