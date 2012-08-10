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
                render: this.onReportModalEditRender
            },

            'reportsettingsmodal reportsettingsedit button[action=edit]':  {
                click: this.onReportModalEditClick
            },

            'reportsettingsmodal reportsettingscopy button[action=copy]':  {
                click: this.onReportModalCopyClick
            },

            'reportsettingsmodal #report_settings_tabbar tab': {
                click: this.onReportModalTabClick
            }
        });

        this.application.on({
            showsettingsmodal: this.showSettingsModal,
            scope: this
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
            report_description = this.getReportDescriptionEdit().getValue();

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

    onReportModalEditRender: function (cmp, eOpts) {
        var store = this.getReportReportsStore(),
            report = store.first(),
            report_id = report.get('id'),
            element = cmp.getEl(),
            favorite = element.down('.favorite');

        favorite.on('click', function (cmp, e, eOpts) {
            Ext.Ajax.request({
                url: 'ReportDynamic!favorite.action?report=' + report_id,
                success: function (result) {
                    // do something
                }
            });
        });
    },

    onReportModalTabClick: function (cmp, e, eOpts) {
        var modal = this.getReportSettingsModal(),
            title = cmp.card.modal_title;

        modal.setTitle(title);
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
    },

    updateReportSettingsEditForm: function () {
        var store = this.getReportReportsStore(),
            report_name_field = this.getReportNameEdit(),
            report_description_field = this.getReportDescriptionEdit();


    }
});