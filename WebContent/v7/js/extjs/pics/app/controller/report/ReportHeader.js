Ext.define('PICS.controller.report.ReportHeader', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportHeader',
        selector: 'reportheader'
    }, {
        ref: 'reportSettingsEdit',
        selector: 'reportsettingsedit'
    }, {
        ref: 'reportSettingsModal',
        selector: 'reportsettingsmodal'
    }, {
        ref: 'reportSettingsTabs',
        selector: 'reportsettingstabs'
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
            'reportheader': {
                render: this.onReportHeaderRender
            },

            'reportheader button[action=save]': {
                click: this.onReportSaveClick
            },

            'reportheader button[action=edit]': {
                click: this.onReportEditClick
            },

            'reportsettingsmodal tabpanel': {
                render: this.onReportSettingsTabsRender
            },

            'reportsettingsmodal button[action=cancel]':  {
                click: this.onReportModalCancelClick
            },

            'reportsettingsmodal reportsettingsedit': {
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
    },

    onReportEditClick: function (cmp, e, eOpts) {
        this.showSettingsModal();
    },

    onReportHeaderRender: function (cmp, eOpts) {
        this.updateReportSummary();
    },

    onReportModalCancelClick: function (cmp, e, eOpts) {
        this.getReportSettingsModal().close();
    },

    onReportModalCopyClick: function (cmp, e, eOpts) {
        var report_name = this.getReportNameCopy().getValue(),
            report_description = this.getReportDescriptionCopy().getValue();

        this.setReportNameAndDescription(report_name, report_description);
        this.application.fireEvent('createreport');

        // form reset
        cmp.up('form').getForm().reset();
    },

    onReportModalEditClick: function (cmp, e, eOpts) {
        var report_name = this.getReportNameEdit().getValue(),
            report_description = this.getReportDescriptionEdit().getValue();

        this.setReportNameAndDescription(report_name, report_description);
        this.updateReportSummary();
        this.application.fireEvent('savereport');

        this.getReportSettingsModal().close();
    },

    onReportModalEditRender: function (cmp, eOpts) {
        this.updateReportSettingsEditForm();
    },

    onReportModalTabClick: function (cmp, e, eOpts) {
        var title = cmp.card.modal_title;

        this.updateReportSettingsModalTitle(title);
    },

    onReportSaveClick: function (cmp, e, eOpts) {
        var config = PICS.app.configuration;

        if (config.isEditable()) {
            this.application.fireEvent('savereport');
        } else {
            this.showSettingsModal();
            this.getReportSettingsTabs().setActiveTab(1);
        }
    },

    onReportSettingsTabsRender: function (cmp, eOpts) {
        var title = cmp.getActiveTab().modal_title;

        this.updateReportSettingsModalTitle(title);
    },

    setReportNameAndDescription: function (name, description) {
        var store = this.getReportReportsStore(),
            report = store.first();

        report.set('name', name);
        report.set('description', description);
    },

    showSettingsModal: function () {
        Ext.create('PICS.view.report.settings.SettingsModal').show();
    },

    updateReportSettingsModalTitle: function (title) {
        this.getReportSettingsModal().setTitle(title);
    },

    updateReportSettingsEditForm: function () {
        var store = this.getReportReportsStore();

        function updateSettingsEditFormFromStore(store) {
            var report = store.first(),
                report_name = report.get('name'),
                report_description = report.get('description');

            updateSettingsEditForm(report_name, report_description);
        }

        function updateSettingsEditForm(name, description) {
            var report_name = this.getReportNameEdit(),
                report_description = this.getReportDescriptionEdit();

            if (report_name) {
                report_name.value = name;
            }

            if (report_description) {
                report_description.value = description;
            }
        }

        // TODO: need better loading check
        if (store.isLoading()) {
            store.on('load', function (store) {
                updateSettingsEditFormFromStore(store);
            });
        } else {
            updateSettingsEditFormFromStore(store);
        }
    },

    updateReportSummary: function () {
        var store = this.getReportReportsStore();
        var me = this;

        function updateSummaryFromStore(store) {
            var report = store.first(),
                report_name = report.get('name'),
                report_description = report.get('description');

            updateSummary(report_name, report_description);
        }

        function updateSummary(name, description) {
            var report_header_element = me.getReportHeader().getEl(),
                report_name = report_header_element.query('.name')[0],
                report_description = report_header_element.query('.description')[0];

            report_name.innerHTML = name;
            report_description.innerHTML = description;
        }

        // TODO: need better loading check
        if (store.isLoading()) {
            store.on('load', function (store) {
                updateSummaryFromStore(store);
            });
        } else {
            updateSummaryFromStore(store);
        }
    }
});