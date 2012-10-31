/**
 * Report Controller
 *
 * Controls report refresh
 */
Ext.define('PICS.controller.report.Report', {
    extend: 'Ext.app.Controller',

    stores: [
        'report.AvailableFields',
        'report.ReportDatas',
        'report.Reports'
    ],

    views: [
        'PICS.view.report.alert-message.AlertMessage'
    ],

    init: function () {
    	this.control({
    		'reportdata': {
    			beforerender: this.onReportDataBeforeRender
    		}
    	});

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

    onReportDataBeforeRender: function (cmp, eOpts) {
        var store = this.getReportReportsStore();

        if (!store.isLoaded()) {
            store.on('load', function (store, records, successful, eOpts) {
                this.application.fireEvent('refreshreport');
            }, this);
        } else {
            this.application.fireEvent('refreshreport');
        }
    },

    createReport: function () {
        var store = this.getReportReportsStore(),
            report = store.first(),
            url = 'ReportDynamic!copy.action';

        Ext.Ajax.request({
            url: url,
            params: report.toRequestParams(),
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
            report_data_store = this.getReportReportDatasStore(),
            report_data_view = Ext.getCmp('report_data').view;

        // Remove the 'no results' message.
        report_data_view.emptyText = '';
        report_data_view.refresh();
        
        this.setPageTitle(report_name);
        
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
                        title: 'Report Saved',
                    });

                    alert_message.show();
                }
            }
        });
    },

    setPageTitle: function(title) {
        document.title = 'PICS - ' + title;
    }
});