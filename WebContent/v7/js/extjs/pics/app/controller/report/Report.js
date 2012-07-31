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
    	var that = this;

    	this.control({
    		'reportdata': {
    			render: function () {
    				if (this.getReportReportsStore().isLoading()) {
			        	this.getReportReportsStore().addListener({
				    		load: function (store, records, successful, eOpts) {
				    			that.application.fireEvent('refreshreport');
				    		}
				    	});
			        } else {
			        	this.application.fireEvent('refreshreport');
			        }
    			}
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

    createReport: function () {
        var store = this.getReportReportsStore(),
            report = store.first(),
            url = 'ReportDynamic!create.action?' + report.toQueryString();

        Ext.Ajax.request({
            url: url,
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

    refreshReport: function () {
        this.getReportReportDatasStore().reload();
    },

    saveReport: function () {
        var store = this.getReportReportsStore(),
            report = store.first(),
            url = 'ReportDynamic!edit.action?' + report.toQueryString(),
            me = this;

        Ext.Ajax.request({
            url: url,
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
    }
});