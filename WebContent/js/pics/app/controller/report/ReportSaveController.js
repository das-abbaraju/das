Ext.define('PICS.controller.report.ReportSaveController', {
    extend: 'Ext.app.Controller',
    refs: [{
        ref: 'reportSave',
        selector: 'reportsave'
    }],

    stores: [
        'report.Reports',
        'report.DataSets'
    ],

    init: function () {
        var me = this;

        this.control({
            'reportsave button[action=cancel]':  {
                click: function () {
                    this.getReportSave().close();
                }
            },
            'reportsave button[action=create]':  {
                click: function () {
                    var userStatus = PICS.app.constants.userStatus;

                    if (userStatus.get_has_permssion() || userStatus.get_is_developer()) {
                        this.createNewReport();
                    }
                }
            },
            'reportsave button[action=copy]':  {
                click: function () {
                    var userStatus = PICS.app.constants.userStatus;

                    if (userStatus.get_has_permssion() || userStatus.get_is_developer()) {
                        this.createNewReport();
                    }
                }
            },
            'reportsave button[action=save]':  {
                click: function () {
                    var userStatus = PICS.app.constants.userStatus;

                    if (userStatus.get_has_permssion() || userStatus.get_is_developer()) {
                        this.saveReport();
                    }
                }
            }
        });

        this.application.on({
            showsavewindow: this.configureSaveWindow,
            scope: this
        });
    },

    configureSaveWindow: function (type) {
        if (type === 'save') {
            this.createReportSaveWindow('Save', 'save');
        } else if (type === 'copy') {
            this.createReportSaveWindow('Copy', 'copy');
        } else if (type === 'create') {
            this.createReportSaveWindow('Create', 'create');
        }
    },

    createNewReport: function () {
        var create_url = 'ReportDynamic!create.action?',
            me = this;

        this.setReportName();

        create_url = create_url + this.getReportParameters();

        this.getReportSave().close();

        Ext.Ajax.request({
           url: create_url,
           success: function (result) {
               var result = Ext.decode(result.responseText);
               document.location = 'ReportDynamic.action?report=' + result.reportID;
           }
        });
    },

    createReportSaveWindow: function (name, action) {
         var window = Ext.create('PICS.view.report.ReportSave', {
            title: name + " Report",
            buttons: [{
                action: action,
                text: name
            }, {
                action: 'cancel',
                name: 'cancel',
                text: 'Cancel'
            }]
        });

        this.setReportWindowFormValues(window);

        window.show();
    },

    getReportParameters: function () {
        var report_store = Ext.StoreManager.get('report.Reports');
        var report = report_store && report_store.first();

        if (!report) {
            throw 'Data.getReportJSON missing report';
        }

        var report_json = this.getReportDataSetsStore().getReportJSON();
        var parameters = {};

        if (report && report.getId() > 0) {
            parameters['report'] = report.getId();
        } else {
            parameters['report.base'] = report.get('base');
        }

        parameters['report.parameters'] = report_json;

        parameters['report.name'] = report.get('name');

        parameters['report.description'] = report.get('description');

        return Ext.Object.toQueryString(parameters);
    },

    saveReport: function () {
        var save_url = 'ReportDynamic!edit.action?',
            me = this;

        this.setReportName();

        save_url = save_url + this.getReportParameters();

        this.getReportSave().close();

        Ext.Ajax.request({
           url: save_url,
           success: function (result) {
               var result = Ext.decode(result.responseText);
               me.application.fireEvent('refreshreport');
           }
        });
    },

    setReportWindowFormValues: function (window) {
        var report = this.getReportReportsStore().first();

        window.child('panel textfield[name=reportName]').setValue(report.get('name'));
        window.child('panel textfield[name=reportDescription]').setValue(report.get('description'));
    },

    setReportName: function () {
        var report = this.getReportReportsStore().first(),
            name = this.getReportSave().child('panel textfield[name=reportName]').getValue(),
            description = this.getReportSave().child('panel textarea[name=reportDescription]').getValue();

        report.set('name', name);
        report.set('description', description);
    }
});