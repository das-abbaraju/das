Ext.define('PICS.view.report.settings.SettingsModal', {
    extend: 'PICS.ux.window.Window',
    alias: 'widget.reportsettingsmodal',

    requires: [
        'PICS.view.report.settings.SettingsModalTabs'
    ],

    closeAction: 'hide',
    draggable: false,
    height: 324,
    id: 'report_settings_modal',
    items: [{
        xtype: 'reportsettingsmodaltabs'
    }],
    layout: 'fit',
    modal: true,
    resizable: false,
    title: 'Edit Report',
    width: 352,

    reset: function () {
        var settings_modal_tabs = this.down('reportsettingsmodaltabs'),
            active_tab = settings_modal_tabs.setActiveTab(1),
            copy_favorite_toggle = active_tab.down('reportfavoritetoggle');

        copy_favorite_toggle.toggleUnfavorite();
    },

    updateActiveTabFromAction: function (action) {
        var settings_modal_tabs = this.down('reportsettingsmodaltabs'),
            active_tab = {},
            title = "";

        if (action == 'edit') {
            active_tab = settings_modal_tabs.setActiveTab(0);
        } else if (action == 'copy') {
            active_tab = settings_modal_tabs.setActiveTab(1);
        }

        title = active_tab.modal_title;
        this.setTitle(title);
    }
});