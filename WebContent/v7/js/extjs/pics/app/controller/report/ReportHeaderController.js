Ext.define('PICS.controller.report.ReportHeaderController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportHeader',
        selector: 'reportheader'
    }, {
        ref: 'reportSettingsModal',
        selector: 'reportsettingsmodal'
    }, {
        ref: 'reportSettingsEdit',
        selector: 'reportsettingsedit'
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
        'report.Reports',
        'report.DataSets'
    ],

    init: function () {
        this.control({
            'reportheader': {
                render: function (cmp, eOpts) {
                    this.updateReportSummary();
                }
            },

            'reportheader button[action=save]': {
                click: function (cmp, e, eOpts) {
                    var config = PICS.app.configuration;

                    if (config.isEditable()) {
                        this.saveReport();
                    } else {
                        this.showSettingsModal();
                        this.getReportSettingsModal().query('tabpanel')[0].setActiveTab(1);
                    }
                }
            },

            'reportheader button[action=edit]': {
                click: function (cmp, e, eOpts) {
                    this.showSettingsModal();
                }
            },

            'reportsettingsmodal tabpanel': {
                render: function (cmp, eOpts) {
                    var title = cmp.getActiveTab().modal_title;

                    this.updateReportSettingsModalTitle(title);
                }
            },

            'reportsettingsmodal button[action=cancel]':  {
                click: function (cmp, e, eOpts) {
                    this.getReportSettingsModal().close();
                }
            },

            'reportsettingsmodal reportsettingsedit': {
                render: function (cmp, eOpts) {
                    this.updateReportSettingsEditForm();
                }
            },

            'reportsettingsmodal reportsettingsedit button[action=edit]':  {
                click: function (cmp, e, eOpts) {
                    var report_name = this.getReportNameEdit().getValue();
                    var report_description = this.getReportDescriptionEdit().getValue();

                    this.setReportNameAndDescription(report_name, report_description);
                    this.updateReportSummary();
                    this.saveReport();

                    this.getReportSettingsModal().close();
                }
            },

            'reportsettingsmodal reportsettingscopy button[action=copy]':  {
                click: function (cmp, e, eOpts) {
                    var report_name = this.getReportNameCopy().getValue();
                    var report_description = this.getReportDescriptionCopy().getValue();

                    this.setReportNameAndDescription(report_name, report_description);
                    this.createReport();

                    // form reset
                    cmp.up('form').getForm().reset();
                }
            },

            'reportsettingsmodal #report_settings_tabbar tab': {
                click: function (cmp, e, eOpts) {
                    var title = cmp.card.modal_title;

                    this.updateReportSettingsModalTitle(title);
                }
            }
        });
    },

    createReport: function () {
        var store = this.getReportReportsStore(),
        proxy_url = 'ReportDynamic!create.action?' + store.getReportQueryString();

        Ext.Ajax.request({
           url: proxy_url,
           success: function (result) {
               var result = Ext.decode(result.responseText);

               if (result.error) {
                   Ext.Msg.alert('Status', result.error);
               } else {
                   document.location = 'ReportDynamic.action?report=' + result.reportID;
               }
           }
        });
    },

    // TODO: why is this here - shouldn't this be in the general report controller??
    saveReport: function () {
        var store = this.getReportReportsStore(),
            proxy_url = 'ReportDynamic!edit.action?' + store.getReportQueryString(),
            me = this;

        Ext.Ajax.request({
           url: proxy_url,
           success: function (result) {
               var result = Ext.decode(result.responseText);

               if (result.error) {
                   Ext.Msg.alert('Status', result.error);
               } else {
                   var alert_message = Ext.create('PICS.view.report.alert-message.AlertMessage', {
                       cls: 'alert alert-success',
                       html: 'to My Reports in Manage Reports.',
                       title: 'Report Saved',
                   });

                   alert_message.show();

                   me.application.fireEvent('refreshreport');
               }
           }
        });
    },

    setReportNameAndDescription: function (name, description) {
        var store = this.getReportReportsStore();
        var report = store.first();

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
        var me = this;

        function updateSettingsEditFormFromStore(store) {
            var report = store.first();
            var report_name = report.get('name');
            var report_description = report.get('description');

            updateSettingsEditForm(report_name, report_description);
        }

        function updateSettingsEditForm(name, description) {
            var report_settings_edit_element = me.getReportSettingsEdit().getEl();
            var report_name_field = report_settings_edit_element.query('[name=report_name]')[0];
            var report_description_field = report_settings_edit_element.query('[name=report_description]')[0];

            if (report_name_field) {
                report_name_field.value = name;
            }

            if (report_description_field) {
                report_description_field.value = description;
            }
        }

        // TODO: need better loading check
        if (store.isLoading()) {
            store.addListener('load', function (store) {
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
            var report = store.first();
            var report_name = report.get('name');
            var report_description = report.get('description');

            updateSummary(report_name, report_description);
        }

        function updateSummary(name, description) {
            var report_header_element = me.getReportHeader().getEl();
            var report_name = report_header_element.query('.name')[0];
            var report_description = report_header_element.query('.description')[0];

            report_name.innerHTML = name;
            report_description.innerHTML = description;
        }

        // TODO: need better loading check
        if (store.isLoading()) {
            store.addListener('load', function (store) {
                updateSummaryFromStore(store);
            });
        } else {
            updateSummaryFromStore(store);
        }
    }
});