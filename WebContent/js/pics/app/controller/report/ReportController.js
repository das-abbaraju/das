/**
 * Report Controller
 *
 * Controls report refresh
 */
Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'rowsPerPage',
        selector: 'pagingtoolbar combo[name=visibleRows]'
    }],
    
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

    // TODO: fishy
    refreshReport: function () {
        this.getController('report.ReportHeaderController').updateReportSettings();
        this.getReportDataSetsStore().buildDataSetGrid();
    },

    refreshSorts: function () {
        this.application.fireEvent('refreshsorts');
    }
});
