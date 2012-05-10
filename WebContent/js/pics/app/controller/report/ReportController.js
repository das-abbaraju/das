/**
 * Report Controller
 * 
 * Controls report refresh
 */
Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',
    alias: ['widget.reportdatacontroller'],

    stores: [
        'report.AvailableFields',
        'report.DataSets',
        'report.Reports'
    ],

    init: function () {
        this.application.on({
            refreshreport: this.refreshReport,
            scope: this
        });
    },
    
    onLaunch: function () {
        this.getReportAvailableFieldsStore().load();
        
        this.getReportReportsStore().load({
            scope: this,
            callback: function(records, operation, success) {
                this.refreshReport();
                this.refreshFilters();
                this.refreshSorts();
            }
        });
    },

    refreshFilters: function () {
        this.application.fireEvent('refreshfilters');
    },
    
    refreshReport: function () {
        this.getReportDataSetsStore().buildDataSetGrid();
    },
    
    refreshSorts: function () {
        this.application.fireEvent('refreshsorts');
    }
});
