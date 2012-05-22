/**
 * Report Controller
 *
 * Controls report refresh
 */
Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',
    alias: ['widget.reportdatacontroller'],

    refs: [{
        ref: 'reportheader',
        selector: 'reportheader'
    }],
    
    stores: [
        'report.AvailableFields',
        'report.DataSets',
        'report.Reports'
    ],

    init: function () {
        this.control({
            'reportheader button': {
                click: function (component, options) {
                    this.application.fireEvent('showsavewindow', component.action);
                }
            }
        });

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
    generateReportDescription: function () {
        var report = Ext.StoreManager.get('report.Reports').first();

        this.getReportheader().child("#reportTitle").update('<h1>' + report.get('name') + '</h1><p>' + report.get('description') + '</p>');
    },
    
    refreshFilters: function () {
        this.application.fireEvent('refreshfilters');
    },

    refreshReport: function () {
        this.getReportDataSetsStore().buildDataSetGrid();
        this.generateReportDescription();
    },

    refreshSorts: function () {
        this.application.fireEvent('refreshsorts');
    }
});
