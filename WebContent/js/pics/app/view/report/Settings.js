Ext.define('PICS.view.report.Settings', {
    extend: 'Ext.window.Window',
    alias: ['widget.reportsettings'],
    requires: [
        'PICS.view.report.SettingsCopy',
        'PICS.view.report.SettingsEdit',
        'PICS.view.report.SettingsExport',
        'PICS.view.report.SettingsPrint',
        'PICS.view.report.SettingsShare'
    ],

    height: 264,
    id: 'report_settings',
    items: [{
        xtype: 'tabpanel',
        border: 0,
        items: [{
            xtype: 'reportsettingsedit'
        }, {
            xtype: 'reportsettingscopy'
        }, {
            xtype: 'reportsettingsexport'
        }, {
            xtype: 'reportsettingsshare',
        }, {
            xtype: 'reportsettingsprint'
        }],
        tabBar: {
            border: 0,
            height: 60,
            id: 'report_settings_tabbar',
            defaults: {
                height: 60,
                width: 70
            }
        },
        tabPosition: 'bottom'
    }],
    layout: 'fit',
    modal: true,
    width: 352,

    constructor: function () {
        this.callParent(arguments);

        //var report = Ext.StoreManager.get('report.Reports').first();

        //this.child('[name=report_name]').setValue(report.get('name'));
        //this.child('[name=report_description]').setValue(report.get('description'));
    }
});