Ext.define('PICS.controller.report.ReportHeaderController', {
    extend: 'Ext.app.Controller',
    
    refs: [{
        ref: 'reportSettings',
        selector: 'reportsettings'
    }, {
        ref: 'reportTitle',
        selector: 'reportheader #reportTitle'
    }, {
        ref: 'reportName',
        selector: 'reportsettings panel textfield[name=reportName]'
    }, {
        ref: 'reportDescription',
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
                    this.showReportSettingsWindow('Edit', 'edit');
                }
            },
            'reportheader button[action=copy]': {
                click: function (component, options) {
                    this.showReportSettingsWindow('Copy', 'copy');
                }
            },
            'reportsettings button[action=cancel]':  {
                click: function () {
                    this.getReportSettings().close();
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

                    this.getReportSettings().close();
                    
                    if (userStatus.get_has_permssion() || userStatus.get_is_developer()) {
                        this.saveReport(component.action);
                    }
                }
            },
            'reportsettings button[action=copy]':  {
                click: function (component, options) {
                    var userStatus = PICS.app.configuration;
                    
                    this.setReportName();
                    this.getReportSettings().close();

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
               if (result.error) {
                   Ext.Msg.alert('Status', result.error);        
               } else {
                   document.location = 'ReportDynamic.action?report=' + result.reportID;    
               }
               
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
               var result = Ext.decode(result.responseText);
               
               if (result.error) {
                   Ext.Msg.alert('Status', result.error);        
               } else {
                   Ext.Msg.alert('Status', 'Report Saved Successfully');
                   this.setReportName();
                   me.application.fireEvent('refreshreport');
               }
           }
        });
    },

    showReportSettingsWindow: function (name, action) {
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

    // TODO: fishy
    updateReportSettings: function () {
        var report = this.getReportReportsStore().first();

        this.getReportTitle().update('<h1>' + report.get('name') + '</h1><p>' + report.get('description') + '</p>');    
    },

    // save report setting form to report object
    setReportName: function () {
        var report = this.getReportReportsStore().first(),
            name = this.getReportName().getValue(),
            description = this.getReportDescription().getValue();

        report.set('name', name);
        report.set('description', description);
    }
});