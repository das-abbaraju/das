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

    getReportDescription: function () {
        return {
            name: this.getReportSave().child('panel textfield[name=reportName]').getValue(),
            description: this.getReportSave().child('panel textarea[name=reportDescription]').getValue()
        }
    },

    saveReport: function () {
        var save_url = 'ReportDynamic!edit.action?',
            me = this,
            report = this.getReportReportsStore().first();
            reportDescription = this.getReportDescription();

        report.set('name', reportDescription.name);
        report.set('description', reportDescription.description);

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

    createNewReport: function () {
        var create_url = 'ReportDynamic!create.action?',
            me = this,
            report = this.getReportReportsStore().first();
            reportDescription = this.getReportDescription();

        report.set('name', reportDescription.name);
        report.set('description', reportDescription.description);

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

        return Ext.Object.toQueryString(parameters);
    },

    configureSaveWindow: function (type) {
        if (type === 'save') {
            this.openSaveWindow();
        } else if (type === 'copy') {
            this.openCopyWindow();
        } else if (type === 'create') {
            this.openCreateWindow();
        }
    },

    openCopyWindow: function () {
        var window = Ext.create('PICS.view.report.ReportSave', {
            title: "Copy Report",
            buttons: [{
                action: 'copy',
                name: 'copy',
                text: 'Copy'
            }, {
                action: 'cancel',
                name: 'cancel',
                text: 'Cancel'
            }]
        });

        this.setFormValues(window);

        window.show();
    },

    openCreateWindow: function () {
        var window = Ext.create('PICS.view.report.ReportSave', {
            title: "Create Report",
            buttons: [{
                action: 'create',
                name: 'create',
                text: 'Create'
            }, {
                action: 'cancel',
                name: 'cancel',
                text: 'Cancel'
            }]
        });

        this.setFormValues(window);

        window.show();
    },

    openSaveWindow: function () {
        var window = Ext.create('PICS.view.report.ReportSave', {
            title: "Save Report",
            buttons: [{
                action: 'save',
                name: 'save',
                text: 'Save'
            }, {
                action: 'cancel',
                name: 'cancel',
                text: 'Cancel'
            }]
        });

        this.setFormValues(window);

        window.show();
    },

    setFormValues: function (window) {
        var report = this.getReportReportsStore().first();

        window.child('panel textfield[name=reportName]').setValue(report.get('name'));
        window.child('panel textfield[name=reportDescription]').setValue(report.get('description'));
    },

    showReportSaveWindow: function (type) {
        var me = this,
            reportShow = this.getReportSave();

        if (!reportShow) {

            reportShow = Ext.create('PICS.view.report.ReportSave', {saveWindowType: type});

            reportShow.show();
        }
    }
});