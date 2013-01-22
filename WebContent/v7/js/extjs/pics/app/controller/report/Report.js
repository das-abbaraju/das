/**
 * Report Controller
 *
 * Controls report refresh
 */
Ext.define('PICS.controller.report.Report', {
    extend: 'Ext.app.Controller',
    
    refs: [{
        ref: 'reportAlertMessage',
        selector: 'reportalertmessage'
    }, {
        ref: 'reportData',
        selector: 'reportdata'
    }, {
        ref: 'reportSettingsModal',
        selector: 'reportsettingsmodal'
    }],

    // TODO: Try to move these to app.js.
    stores: [
        'report.ReportDatas',
        'report.Reports',
        'report.Columns',
        'report.Filters'
    ],

    init: function () {
        this.application.on({
            createreport: this.createReport,
            scope: this
        });
        
        this.application.on({
            downloadreport: this.downloadReport,
            scope: this
        });
        
        this.application.on({
            favoritereport: this.favoriteReport,
            scope: this
        });
        
        this.application.on({
            printreport: this.printReport,
            scope: this
        });

/*        this.application.on({
            refreshreport: this.refreshReport,
            scope: this
        });
*/
        this.application.on({
            savereport: this.saveReport,
            scope: this
        });
        
        this.application.on({
            sharereport: this.shareReport,
            scope: this
        });
        
        this.application.on({
            unfavoritereport: this.unfavoriteReport,
            scope: this
        });
    },

    createReport: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            params = report.toRequestParams();
        
        Ext.Ajax.request({
            url: 'ReportDynamic!copy.action',
            params: params,
            success: function (result) {
                var result = Ext.decode(result.responseText);

                if (result.error) {
                    Ext.Msg.alert('Status', result.error);
                } else {
                    document.location = 'Report.action?report=' + result.reportID;
                }
            }
        });
    },
    
    downloadReport: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_id = report.get('id');
    
        //TODO: Change this to a post and include parameters.
        window.open('ReportData!download.action?report=' + report_id);
    },
    
    favoriteReport: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_id = report.get('id');
        
        Ext.Ajax.request({
            url: 'ManageReports!favorite.action?reportId=' + report_id
        });
    },
    
    printReport: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_id = report.get('id');
    
        //TODO: Change this to a post and include parameters.
        window.open('ReportData!print.action?report=' + report_id);
    },
    
/*    refreshReport: function () {
        // TODO: not what we want anymore
        // need to hook into new load
        return false;
        
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_data_store = this.getReportReportDatasStore(),
            report_data = this.getReportData();
            
        var report_name = report.get('name'),
            // TODO: this should be removed
            limit = report.get('rowsPerPage'),
            params = report.toRequestParams(),
            model_fields = report.convertColumnsToModelFields(),
            grid_columns = report.convertColumnsToGridColumns();
            
        this.updatePageTitle(report_name);

        // TODO: this should be removed
        report_data_store.setLimit(limit);
        
        // update data store proxy
        report_data_store.updateProxyParameters(params);
        
        // update data store model
        report_data_store.updateReportDataModelFields(model_fields);
        
        // update report data grid columns
        report_data.updateGridColumns(grid_columns);
    },

*/    saveReport: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            params = report.toRequestParams(),
            that = this;
        
        Ext.Ajax.request({
            url: 'ReportDynamic!save.action',
            params: params,
            success: function (result) {
                var result = Ext.decode(result.responseText);

                if (result.error) {
                    Ext.Msg.alert('Status', result.error);
                } else {
                    var alert_message = that.getReportAlertMessage();
                    
                    if (alert_message) {
                        alert_message.destroy();
                    }
                    
                    var alert_message = Ext.create('PICS.view.report.alert-message.AlertMessage', {
                        cls: 'alert alert-success',
                        html: 'to My Reports in Manage Reports.',
                        title: 'Report Saved'
                    });

                    alert_message.show();
                }
            }
        });
    },
    
    shareReport: function (options) {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_settings_modal = this.getReportSettingsModal(),
            that = this;
        
        Ext.Ajax.request({
            url: 'ReportSharing!share.action',
            params: {
                report: options.report_id,
                id: options.account_id,
                type: options.account_type,
                editable: options.is_editable
            },
            success: function (result) {
                var result = Ext.decode(result.responseText);
        
                if (result.error) {
                    Ext.Msg.alert('Status', result.error);
                } else {
                    var alert_message = that.getReportAlertMessage();
                    
                    if (alert_message) {
                        alert_message.destroy();
                    }
        
                    var alert_message = Ext.create('PICS.view.report.alert-message.AlertMessage', {
                        cls: 'alert alert-success',
                        title: result.title,
                        html: result.html
                    });
        
                    alert_message.show();
                    
                    report_settings_modal.close();
                }
            }
        });
    },
    
    unfavoriteReport: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_id = report.get('id');
        
        Ext.Ajax.request({
            url: 'ManageReports!unfavorite.action?reportId=' + report_id
        });
    },

    updatePageTitle: function(title) {
        document.title = 'PICS - ' + title;
    }
});