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
    }],

    stores: [
        'report.Reports'
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
                beforerender: this.onReportModalEditBeforeRender,
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

            'reportsettingsmodal reportsettingsprint button[action=print]':  {
                click: this.onReportModalPrintClick
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

   showMoreResults: function (e, t, eOpts) {
       var cmp = Ext.ComponentQuery.query('searchbox')[0];
       var term = cmp.inputEl.getValue();

       cmp.search(term);
   },

   getReportId: function () {
       var store = this.getReportReportsStore(),
           report = store.first();

       return report.get('id');
   },
   
   onReportFavorite: function () {
       var report_id = this.getReportId();
       
        Ext.Ajax.request({
            url: 'ManageReports!favorite.action?reportId=' + report_id
        });
    },

    onReportUnFavorite: function () {
        var store = this.getReportReportsStore(),
            report = store.first(),
            report_id = report.get('id');

        Ext.Ajax.request({
            url: 'ManageReports!unfavorite.action?reportId=' + report_id
        });
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

        report.set('name', report_name);
        report.set('description', report_description);

        this.application.fireEvent('createreport');

        // form reset
        cmp.up('form').getForm().reset();
    },

    onReportModalEditClick: function (cmp, e, eOpts) {
        var store = this.getReportReportsStore(),
            report = store.first(),
            report_name = this.getReportNameEdit().getValue(),
            report_description = this.getReportDescriptionEdit().getValue(),
            edit_settings = cmp.up('#report_edit'),
            is_favorite = edit_settings.checkFavoriteStatus();
            config = PICS.app.configuration;

        if (is_favorite) {
            edit_settings.fireEvent('favorite');
            config.setIsFavorite(true);
        } else {
            edit_settings.fireEvent('unfavorite');
            config.setIsFavorite(false);
        }

        report.set('name', report_name);
        report.set('description', report_description);

        this.application.fireEvent('updatereportsummary');
        this.application.fireEvent('savereport');

        this.getReportSettingsModal().close();
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
        var store = this.getReportReportsStore(),
            report = store.first();

        //TODO: Change this to a post and include parameters.
        window.open('ReportData!download.action?report=' + report.get('id'));
    },

    onReportModalPrintClick: function (cmp, e, eOpts) {
        var store = this.getReportReportsStore(),
            report = store.first();

        //TODO: Change this to a post and include parameters.
        window.open('ReportData!print.action?report=' + report.get('id'));
    },

    onReportSettingsTabsBeforeRender: function (cmp, eOpts) {
        var modal = this.getReportSettingsModal(),
            title = cmp.getActiveTab().modal_title;

        modal.setTitle(title);
    },
    
    showSettingsModal: function (action) {
        var modal = Ext.create('PICS.view.report.settings.SettingsModal');

        if (action == 'edit') {
            this.getReportSettingsTabs().setActiveTab(0);
        } else if (action == 'copy') {
            this.getReportSettingsTabs().setActiveTab(1);
        }

        modal.show(false, function () {
            // Close the modal when the user clicks outside of it.
            Ext.get(Ext.query('.x-mask:last')).on('click', function () {
                modal.close();
            });
        });
    },
    
    /**
     * Share
     */

    onReportModalShareSearchboxRender: function (cmp, eOpts) {
        cmp.store.getProxy().url = 'ReportAutocomplete!reportSharingAutocomplete.action?reportId=' + this.getReportId();
        cmp.store.load();
    },

    onReportModalShareSearchboxSelect: function (combo, records, eOpts) {
        var record = records[0];

        if (record) {
            var cmp = Ext.ComponentQuery.query('reportsettingsshare')[0];
            var account = {
                name: record.get('result_name'),
                at: record.get('result_at')
            };
            
            // Save the id of the selected record for use after share button is clicked.
            cmp.record_id = record.index;
            
            // Show the selection.
            cmp.update(account);
        }
    },

    onReportModalShareSearchboxSpecialKey: function (base, e, eOpts) {
        if (e.getKey() === e.ENTER) {
            var term = base.getValue();
            this.search(term);
        } else if (e.getKey() === e.BACKSPACE && base.getRawValue().length <= 1) {
            base.collapse();
        }
    },

    onReportModalShareClick: function (cmp, e, eOpts) {
        // Get the share component.
        var reportsettingsshare =  Ext.ComponentQuery.query('reportsettingsmodal reportsettingsshare')[0];

        // Get the record id.
        var record_id = reportsettingsshare.record_id;

        // Abort if no account has been selected.
        if (typeof record_id == 'undefined') {
            return;
        }
        
        // Get the record.
        var combo =  Ext.ComponentQuery.query('reportsettingsmodal reportsettingsshare sharesearchbox')[0],
            combo_store = combo.getStore(),
            record = combo_store.getAt(record_id);

        // Get the relevant record data.
        var account_id = record.raw.result_id,
            account_type = record.raw.search_type;

        // Get the editable value.
        var el = reportsettingsshare.getEl(),
            editable = el.down('.icon-edit.selected') ? true : false;

        // Get the report id.
        var report_id = this.getReportId();

        // Construct the URL and send the request.
        var that = this;
        Ext.Ajax.request({
            url: 'ReportSharing!share.action',
            params: {
                report: report_id,
                id: account_id,
                type: account_type,
                editable: editable
            },
            success: function (result) {
                var result = Ext.decode(result.responseText);

                if (result.error) {
                    Ext.Msg.alert('Status', result.error);
                } else {
                    var alert_message = Ext.getCmp('alert_message');
                    
                    if (alert_message) {
                        alert_message.destroy();
                    }

                    var alert_message = Ext.create('PICS.view.report.alert-message.AlertMessage', {
                        cls: 'alert alert-success',
                        html: 'with the selected account.',
                        title: 'Report Shared'
                    });

                    alert_message.show();
                    
                    that.getReportSettingsModal().close();
                }
            }
        });
    }
});