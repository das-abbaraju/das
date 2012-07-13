/**
 * Report Controller
 *
 * Controls report refresh
 */
Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',

    stores: [
        'report.AvailableFields',
        'report.DataSets',
        'report.Reports'
    ],

    init: function () {
    	var that = this;

    	this.control({
    		'reportdatasetgrid': {
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
            refreshreport: this.refreshReport,
            scope: this
        });
    },

    refreshReport: function () {
        this.getReportDataSetsStore().buildDataSetGrid();
    }
});
