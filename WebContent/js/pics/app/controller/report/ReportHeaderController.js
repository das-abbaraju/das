Ext.define('PICS.controller.report.ReportHeaderController', {
    extend: 'Ext.app.Controller',
    refs: [{
        ref: 'reportsettings',
        selector: 'reportsettings'
    }, {
        ref: 'reporttitle',
        selector: 'reportheader #reportTitle'
    }, {
        ref: 'reportname',
        selector: 'reportsettings panel textfield[name=reportName]'
    }, {
        ref: 'reportdescription',
        selector: 'reportsettings panel textfield[name=reportDescription]'
    }],

    stores: [
        'report.Reports',
        'report.DataSets'
    ],

    init: function () {
        var me = this;

        this.control({
            'reportheader button[action=save]': {
                click: function (component, options) {
                    this.saveReport('save');
                }
            },
            'reportheader button[action=edit]': {
                click: function (component, options) {
                    this.showReportSettings('Edit', 'edit');
                }
            },
            'reportheader button[action=copy]': {
                click: function (component, options) {
                    this.showReportSettings('Copy', 'copy');
                }
            },
            'reportsettings button[action=cancel]':  {
                click: function () {
                    this.getReportsettings().close();
                }
            },
            'reportsettings button[action=save]':  {
                click: function (component, options) {
                    var userStatus = PICS.app.configuration;

                    if (userStatus.get_has_permssion() || userStatus.get_is_developer()) {
                        this.saveReport(component.action);
                    }
                }
            },
            'reportsettings button[action=edit]':  {
                click: function (component, options) {
                    var userStatus = PICS.app.configuration;

                    this.setReportName();
                    this.getReportsettings().close();
                    
                    if (userStatus.get_has_permssion() || userStatus.get_is_developer()) {
                        this.saveReport();
                    }
                }
            },
            'reportsettings button[action=copy]':  {
                click: function (component, options) {
                    var userStatus = PICS.app.configuration;
                    
                    this.setReportName();
                    this.getReportsettings().close();

                    if (userStatus.get_has_permssion() || userStatus.get_is_developer()) {
                        this.createNewReport();
                    }
                }
            }
        });
    },

    createNewReport: function () {
        var reports = this.getReportReportsStore(),
        proxy_url = 'ReportDynamic!create.action?' + reports.getReportQueryString();
        
        Ext.Ajax.request({
           url: proxy_url,
           success: function (result) {
                   var result = Ext.decode(result.responseText);
                   document.location = 'ReportDynamic.action?report=' + result.reportID;
           }
        });        
    },

    saveReport: function () {
        var reports = this.getReportReportsStore(),
            proxy_url = 'ReportDynamic!edit.action?' + reports.getReportQueryString(),
            me = this;

        Ext.Ajax.request({
           url: proxy_url,
           success: function (result) {
               me.application.fireEvent('refreshreport');
           }
        });
    },

    showReportSettings: function (name, action) {
        var window = Ext.create('PICS.view.report.ReportSettings', {
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
      
         window.show();
    },

    updateReportSettings: function (local) {
        var report = this.getReportReportsStore().first();

        this.getReporttitle().update('<h1>' + report.get('name') + '</h1><p>' + report.get('description') + '</p>');    
    },

    setReportName: function () {
        var report = this.getReportReportsStore().first(),
            name = this.getReportname().getValue(),
            description = this.getReportdescription().getValue();

        report.set('name', name);
        report.set('description', description);
    }
});