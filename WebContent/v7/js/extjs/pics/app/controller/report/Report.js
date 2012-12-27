/**
 * Report Controller
 *
 * Controls report refresh
 */
Ext.define('PICS.controller.report.Report', {
    extend: 'Ext.app.Controller',

    stores: [
        'report.ReportDatas',
        'report.Reports'
    ],

    init: function () {
        this.application.on({
            createreport: this.createReport,
            scope: this
        });

        this.application.on({
            refreshreport: this.refreshReport,
            scope: this
        });

        this.application.on({
            savereport: this.saveReport,
            scope: this
        });
    },

    createReport: function () {
        var store = this.getReportReportsStore(),
            report = store.first(),
            config = PICS.app.configuration,
            request_params = report.toRequestParams();

        request_params.favorite = config.isFavorite();

        Ext.Ajax.request({
            url: 'ReportDynamic!copy.action',
            params: request_params,
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

    refreshReport: function () {
        var report_store = this.getReportReportsStore(),
            report = report_store.first(),
            report_name = report.get('name'),
            report_data_store = this.getReportReportDatasStore();
        
        this.updatePageTitle(report_name);
        
        report_data_store.reload();
    },

    saveReport: function () {
        var store = this.getReportReportsStore(),
            report = store.first(),
            url = 'ReportDynamic!save.action';

        Ext.Ajax.request({
            url: url,
            params: report.toRequestParams(),
            success: function (result) {
                var result = Ext.decode(result.responseText);

                if (result.error) {
                    Ext.Msg.alert('Status', result.error);
                } else {
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

    updatePageTitle: function(title) {
        document.title = 'PICS - ' + title;
    }
});