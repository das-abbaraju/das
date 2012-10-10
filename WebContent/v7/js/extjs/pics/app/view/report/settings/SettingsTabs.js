Ext.define('PICS.view.report.settings.Tabs', {
    extend: 'Ext.tab.Panel',
    alias: ['widget.reportsettingstabs'],

    requires: [
        'PICS.view.report.settings.CopySettings',
        'PICS.view.report.settings.EditSettings',
        'PICS.view.report.settings.ExportSettings',
        'PICS.view.report.settings.PrintSettings',
        'PICS.view.report.settings.share.ShareSettings'
    ],

    border: false,
    items: [{
        xtype: 'reportsettingsedit'
    }, {
        xtype: 'reportsettingscopy'
    }, {
        xtype: 'reportsettingsshare',
    }, {
        xtype: 'reportsettingsexport'
    }, {
        xtype: 'reportsettingsprint'
    }],
    tabBar: {
        border: false,
        height: 60,
        id: 'report_settings_tabbar',
        // controls default proportions for tab buttons
        defaults: {
            height: 60,
            width: 70
        }
    },
    tabPosition: 'bottom'
});