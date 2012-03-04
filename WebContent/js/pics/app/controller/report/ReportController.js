Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',

 	stores: [
        'report.Reports'
    ],
	
    init: function() {
        this.control({
            "reportoptions button[action=save]": {
                click: this.saveReport
            },
            "reportcolumnselector button[action=add]":  {
            	click: this.addColumn
            }
        });
    },
    addColumn: function(button, e, options) {
    	alert("TODO : adding columns");
    },
    saveReport: function(button, e, options) {
        var reportStore = this.getReportReportsStore();
        reportStore.first().setDirty();
        reportStore.sync();
    }
});
