Ext.define('PICS.controller.report.SettingsModal', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportSettingsModal',
        selector: 'reportsettingsmodal'
    }, {
        ref: 'reportSettingsTabs',
        selector: 'reportsettingstabs'
    }, {
        ref: 'reportSettingsEdit',
        selector: 'reportsettingsedit'
    }, {
        ref: 'reportSettingsShare',
        selector: 'reportsettingsshare'
    }, {
        ref: 'reportSettingsNoPermission',
        selector: 'reportsettingsmodal #settings_no_permission'
    }, {
        ref: 'reportNameEdit',
        selector: 'reportsettingsedit [name=report_name]'
    }, {
        ref: 'reportDescriptionEdit',
        selector: 'reportsettingsedit [name=report_description]'
    }, {
        ref: 'reportNameCopy',
        selector: 'reportsettingscopy [name=report_name]'
    }, {
        ref: 'reportDescriptionCopy',
        selector: 'reportsettingscopy [name=report_description]'
    }, {
        ref: 'editFavoriteToggle',
        selector: 'reportsettingsedit favoritetoggle'
    }, {
        ref: 'copyFavoriteToggle',
        selector: 'reportsettingscopy favoritetoggle'
    }],

    stores: [
        'report.Reports'
    ],
    
    views: [
        'PICS.view.report.settings.SettingsModal'
    ],

    init: function () {
        this.control({
            'reportsettingsmodal tabpanel': {
                beforerender: this.onReportSettingsTabsBeforeRender
            },

            'reportsettingsmodal button[action=cancel]':  {
                click: this.onReportModalCancelClick
            },

            'reportsettingsmodal reportsettingsedit': {
                beforerender: this.onReportModalEditBeforeRender
            },

            'reportsettingsmodal favoritetoggle': {
                favorite: this.onReportFavorite,
                unfavorite: this.onReportUnFavorite
            },

            'reportsettingsmodal reportsettingsedit button[action=edit]':  {
                click: this.onReportModalEditClick
            },

            'reportsettingsmodal reportsettingscopy button[action=copy]':  {
                click: this.onReportModalCopyClick
            },

            'reportsettingsmodal #report_settings_tabbar tab': {
                click: this.onReportModalTabClick
            },

            'reportsettingsmodal reportsettingsexport button[action=export]':  {
                click: this.onReportModalExportClick
            },

            'reportsettingsmodal reportsettingsprint button[action=print-preview]':  {
                click: this.onReportModalPrintPreviewClick
            },

            'reportsettingsmodal reportsettingsshare sharesearchbox': {
                beforerender: this.onReportModalShareSearchboxRender,
                select: this.onReportModalShareSearchboxSelect,
                specialkey: this.onReportModalShareSearchboxSpecialKey
            },

            'reportsettingsmodal reportsettingsshare button[action=share]': {
                click: this.onReportModalShareClick
            }
        });

        this.application.on({
            showsettingsmodal: this.showSettingsModal,
            scope: this
        });
    },

    getReportId: function () {
        var store = this.getReportReportsStore(),
            report = store.first();

        return report.get('id');
    },
   
    onReportFavorite: function () {
        this.application.fireEvent('favoritereport');
    },

    onReportUnFavorite: function () {
        this.application.fireEvent('unfavoritereport');
    },
    
    onReportModalCancelClick: function (cmp, e, eOpts) {
        var modal = this.getReportSettingsModal();
    
        modal.close();
    },
    
    onReportModalCopyClick: function (cmp, e, eOpts) {
        var store = this.getReportReportsStore(),
            report = store.first(),
            report_name = this.getReportNameCopy().getValue(),
            report_description = this.getReportDescriptionCopy().getValue();
    
        this.setFavoriteStatus('copy');
    
        report.set('name', report_name);
        report.set('description', report_description);
        
        // form reset
        // TODO: put this in the view
        cmp.up('form').getForm().reset();
        
        this.application.fireEvent('createreport');
    },
    
    onReportModalEditClick: function (cmp, e, eOpts) {
        var store = this.getReportReportsStore(),
            report = store.first(),
            report_name = this.getReportNameEdit().getValue(),
            report_description = this.getReportDescriptionEdit().getValue();
    
        this.setFavoriteStatus('edit');
    
        report.set('name', report_name);
        report.set('description', report_description);
        
        this.getReportSettingsModal().close();
        
        this.application.fireEvent('updatereportsummary');
        this.application.fireEvent('savereport');
    },
    
    onReportModalEditBeforeRender: function (cmp, eOpts) {
        var store = this.getReportReportsStore(),
            report_settings_edit = this.getReportSettingsEdit(),
            report_no_permission_edit = this.getReportSettingsNoPermission();
    
        // if there is no form - do nothing
        if (!report_no_permission_edit) {
            if (!store.isLoaded()) {
                store.on('load', function (store, records, successful, eOpts) {
                    var report = store.first();
    
                    report_settings_edit.update(report);
                });
            } else {
                var report = store.first();
    
                report_settings_edit.update(report);
            }
        }
    },
    
    onReportModalTabClick: function (cmp, e, eOpts) {
        var modal = this.getReportSettingsModal(),
            title = cmp.card.modal_title;
    
        modal.setTitle(title);        
    },
    
    onReportModalExportClick: function (cmp, e, eOpts) {
        this.application.fireEvent('downloadreport');
    },
    
    onReportModalPrintPreviewClick: function (cmp, e, eOpts) {
        this.application.fireEvent('printreport');
    },
    
    onReportSettingsTabsBeforeRender: function (cmp, eOpts) {
        var modal = this.getReportSettingsModal(),
            title = cmp.getActiveTab().modal_title;
    
        modal.setTitle(title);
    },
    
    setFavoriteStatus: function (action) {
        if (action == 'edit') {
            favorite_toggle = this.getEditFavoriteToggle();
        } else if (action == 'copy') {
            favorite_toggle = this.getCopyFavoriteToggle();
        }
    
        var is_favorite_on = favorite_toggle.isFavoriteOn();
    
        favorite_toggle.saveFavoriteStatus(is_favorite_on);
    },
    
    showSettingsModal: function (action) {
        var settings_modal = Ext.create('PICS.view.report.settings.SettingsModal');
    
        if (action == 'edit') {
            this.getReportSettingsTabs().setActiveTab(0);
        } else if (action == 'copy') {
            this.getReportSettingsTabs().setActiveTab(1);
        }
    
        settings_modal.show();
    },
    
    /**
     * Share
     */
    
    onReportModalShareSearchboxRender: function (cmp, eOpts) {
        cmp.store.getProxy().url = 'Autocompleter!reportSharingAutocomplete.action?reportId=' + this.getReportId();
        cmp.store.load();
    },
    
    onReportModalShareSearchboxSelect: function (combo, records, eOpts) {
        var record = records[0];
    
        if (record) {
            var report_settings_share = this.getReportSettingsShare();
            
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
        var report_settings_share = this.getReportSettingsShare(),
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