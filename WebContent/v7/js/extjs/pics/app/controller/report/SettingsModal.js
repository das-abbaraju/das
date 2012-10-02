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
                select: this.onReportModalSeachboxSelect,
                specialkey: this.onReportModalSearchboxSpecialKey
            }
        });

        this.application.on({
            showsettingsmodal: this.showSettingsModal,
            scope: this,
        });
    },

   showMoreResults: function (e, t, eOpts) {
       var cmp = Ext.ComponentQuery.query('searchbox')[0];
       var term = cmp.inputEl.getValue();

       cmp.search(term);
   },
   
   onReportModalSeachboxSelect: function (combo, records, eOpts) {
       var post = records[0];

       if (post) {
           var account = {
                           name: post.get('result_name'),
                           at: post.get('result_at')
                         };

           var cmp = Ext.ComponentQuery.query('reportsettingsshare')[0];
           cmp.update(account);
       }
   },
   
   onReportModalSearchboxSpecialKey: function (base, e, eOpts) {
       console.log('specialkey');
       if (e.getKey() === e.ENTER) {
           var term = base.getValue();
           this.search(term);
       } else if (e.getKey() === e.BACKSPACE && base.getRawValue().length <= 1) {
           base.collapse();
       }
   },

   onReportFavorite: function () {
        var store = this.getReportReportsStore(),
            report = store.first(),
            report_id = report.get('id');

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
        window.open('ReportDownload.action?report=' + report.get('id'));
    },

    onReportModalPrintClick: function (cmp, e, eOpts) {
        var store = this.getReportReportsStore(),
            report = store.first();

        //TODO: Change this to a post and include parameters.
        window.open('ReportPrint.action?report=' + report.get('id'));
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

        modal.show();
    }
});