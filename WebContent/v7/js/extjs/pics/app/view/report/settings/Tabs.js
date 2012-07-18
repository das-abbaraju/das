Ext.define('PICS.view.report.settings.Tabs', {
    extend: 'Ext.tab.Panel',
    alias: ['widget.reportsettingstabs'],

    requires: [
        'PICS.view.report.settings.Copy',
        'PICS.view.report.settings.Edit',
        'PICS.view.report.settings.Export',
        'PICS.view.report.settings.Print',
        'PICS.view.report.settings.Share'
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