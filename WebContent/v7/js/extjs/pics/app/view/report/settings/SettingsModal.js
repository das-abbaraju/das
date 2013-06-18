Ext.define('PICS.view.report.settings.SettingsModal', {
    extend: 'PICS.ux.window.Window',
    alias: 'widget.reportsettingsmodal',

    requires: [
        'PICS.view.report.settings.SettingsModalTabs'
    ],

    closeAction: 'hide',
    draggable: false,
    height: 324,
    header: {
        tools:[{
            xtype: 'button',
            height: 25,
            id: 'settings_modal_report_info_button',
            text: '<i class="icon-info-sign"></i>',
            tooltip: PICS.text('Report.execute.settingsmodal.tooltipreportinfobutton'),
            width: 20,
            action: 'show-report-info'
        }, {
            type: 'close',
            handler: function () {
                this.up('window').close();
            }
        }]
    },
    id: 'report_settings_modal',
    items: [{
        xtype: 'reportsettingsmodaltabs'
    }],
    layout: 'fit',
    modal: true,
    resizable: false,
    width: 352,
    
    updateActiveTabFromAction: function (action) {
        var settings_modal_tabs = this.down('reportsettingsmodaltabs'),
            title;
    
        if (action == 'edit') {
            title = settings_modal_tabs.setActiveTab(0).modal_title;
        } else if (action == 'copy') {
            title = settings_modal_tabs.setActiveTab(1).modal_title;
        }
        
        this.setTitle(title);
    }
});