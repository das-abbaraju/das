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

    border: 0,
    tabBar: {
        border: 0,
        height: 60,
        id: 'report_settings_tabbar',
        defaults: {
            height: 60,
            width: 70
        }
    },
    tabPosition: 'bottom',

    constructor: function () {
        this.callParent(arguments);

        var config = PICS.app.configuration;

        if (config.isEditable()) {
            this.add({
                xtype: 'reportsettingsedit'
            });
        }

        this.add({
            xtype: 'reportsettingscopy'
        }, {
            xtype: 'reportsettingsexport'
        }, {
            xtype: 'reportsettingsshare',
        }, {
            xtype: 'reportsettingsprint'
        });

        this.setActiveTab(0);
    }
});