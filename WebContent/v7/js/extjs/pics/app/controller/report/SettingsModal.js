Ext.define('PICS.controller.report.SettingsModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'settingsModal',
        selector: 'reportsettingsmodal'
    }, {
        ref: 'settingsModalTabs',
        selector: 'reportsettingsmodaltabs'
    }, {
        ref: 'editSetting',
        selector: 'reporteditsetting'
    }, {
        ref: 'copySetting',
        selector: 'reportcopysetting'
    }, {
        ref: 'shareSetting',
        selector: 'reportsharesetting'
    }],

    stores: [
        'report.Reports'
    ],
    
    views: [
        'PICS.view.report.settings.SettingsModal'
    ],

    init: function () {
        this.control({
            'reportsettingsmodal': {
                close: this.settingsModalClose
            },
            
            'reportsettingsmodal button[action=cancel]':  {
                click: this.cancelSettingsModal
            },
            
            'reportsettingsmodaltabs': {
                tabchange: this.changeSettingsModalTab
            },
            
            'reportsettingsmodal reporteditsetting': {
                afterrender: this.afterEditSettingRender,
                beforerender: this.beforeEditSettingRender
            },
            
            'reportsettingsmodal reporteditsetting button[action=edit]':  {
                click: this.editReport
            },
            
            'reportsettingsmodal reportcopysetting button[action=copy]':  {
                click: this.copyReport
            },

            'reportsettingsmodal reportfavoritetoggle': {
                favorite: this.favoriteReport,
                unfavorite: this.unfavoriteReport
            },

            'reportsettingsmodal reportsettingsexport button[action=export]':  {
                click: this.exportReport
            },

            'reportsettingsmodal reportsettingsprint button[action=print-preview]':  {
                click: this.printReport
            },

            'reportsettingsmodal reportsharesetting sharesearchbox': {
                beforerender: this.onReportModalShareSearchboxRender,
                select: this.onReportModalShareSearchboxSelect,
                specialkey: this.onReportModalShareSearchboxSpecialKey
            },

            'reportsettingsmodal reportsharesetting button[action=share]': {
                click: this.onReportModalShareClick
            }
        });

        this.application.on({
            opensettingsmodal: this.openSettingsModal,
            scope: this
        });
    },
    
    afterEditSettingRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            is_favorite = report.get('is_favorite'),
            edit_favorite_toggle = cmp.down('reportfavoritetoggle');

        if (is_favorite) {
            edit_favorite_toggle.toggleFavorite();
        }
    },
    
    beforeEditSettingRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            edit_setting_view = this.getEditSetting();

        if (edit_setting_view) {
            edit_setting_view.loadFormRecord(report);
        }
    },
    
    settingsModalClose: function (cmp, eOpts) {
        var settings_modal_view = this.getSettingsModal();

        settings_modal_view.reset();
    },

    cancelSettingsModal: function (cmp, e, eOpts) {
        var settings_modal_view = this.getSettingsModal();
    
        settings_modal_view.close();
    },
    
    changeSettingsModalTab: function (cmp, nextCard, oldCard, eOpts) {
        var settings_modal_view = this.getSettingsModal(),
            title = nextCard.modal_title;
        
        settings_modal_view.setTitle(title);
    },
    
    copyReport: function (cmp, e, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            copy_setting_view = cmp.up('reportcopysetting');

        copy_setting_view.updateFormRecord(report);
        
        this.application.fireEvent('createreport');
    },
    
    editReport: function (cmp, e, eOpts) {
        var settings_modal_view = this.getSettingsModal(),
            edit_setting_view = cmp.up('reporteditsetting');

        edit_setting_view.updateFormRecord();

        settings_modal_view.close();
    
        this.application.fireEvent('updatepageheader');
        
        this.application.fireEvent('savereport');
    },
    
    exportReport: function (cmp, e, eOpts) {
        this.application.fireEvent('downloadreport');
    },

    favoriteReport: function (cmp, eOpts) {
        var active_tab_class_name = this.getActiveTabClassName();

        if (active_tab_class_name == 'PICS.view.report.settings.EditSetting') {
            this.application.fireEvent('favoritereport');
        }
    },

    getActiveTabClassName: function () {
        var settings_modal_tabs = this.getSettingsModalTabs(),
            active_tab = settings_modal_tabs.getActiveTab(),
            active_tab_class_name = Ext.getClassName(active_tab);

        return active_tab_class_name;
    },

    openSettingsModal: function (action) {
        var settings_modal_view = this.getSettingsModal();
        
        if (!settings_modal_view) {
            settings_modal_view = Ext.create('PICS.view.report.settings.SettingsModal');
        }
        
        settings_modal_view.updateActiveTabFromAction(action);
    
        settings_modal_view.show();
    },
    
    printReport: function (cmp, e, eOpts) {
        this.application.fireEvent('printreport');
    },

    unfavoriteReport: function (cmp, eOpts) {
        var active_tab_class_name = this.getActiveTabClassName();

        if (active_tab_class_name == 'PICS.view.report.settings.EditSetting') {
            this.application.fireEvent('unfavoritereport');
        }
    },
    
    /**
     * Share
     */
    
    onReportModalShareSearchboxRender: function (cmp, eOpts) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_id = report.get('id');
        
        cmp.store.getProxy().url = 'Autocompleter!reportSharingAutocomplete.action?reportId=' + report_id;
        cmp.store.load();
    },
    
    onReportModalShareSearchboxSelect: function (combo, records, eOpts) {
        var record = records[0];
    
        if (record) {
            var report_settings_share = this.getShareSetting();
            
            var account = {
                name: record.get('result_name'),
                at: record.get('result_at')
            };
    
            // Save the record data needed for sharing.
            report_settings_share.request_data = {
                account_id: record.get('result_id'),
                account_type: record.get('search_type')
            };
        
            // Show the selection.
            report_settings_share.update(account);
        }
    },
    
    onReportModalShareSearchboxSpecialKey: function (cmp, e, eOpts) {
        if (e.getKey() === e.ENTER) {
            var term = cmp.getValue();
            
            this.search(term);
        } else if (e.getKey() === e.BACKSPACE && cmp.getRawValue().length <= 1) {
            cmp.collapse();
        }
    },
    
    onReportModalShareClick: function (cmp, e, eOpts) {
        var report_settings_share = this.getShareSetting(),
            data = report_settings_share.request_data;
        
        // Abort if no account has been selected.
        if (typeof data == 'undefined') {
            return;
        }
        
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_id = report.get('id'),
            report_settings_share_element = report_settings_share.getEl(),
            account_id = data.account_id,
            account_type = data.account_type,
            is_editable = report_settings_share_element.down('.icon-edit.selected') ? true : false;
    
        this.application.fireEvent('sharereport', {
            report_id: report_id,
            account_id: account_id,
            account_type: account_type,
            is_editable: is_editable
        });
    }
});