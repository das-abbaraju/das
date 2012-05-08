/**
 * Report Controller
 * 
 * Controls report refresh
 */
Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',
    alias: ['widget.reportdatacontroller'],

    refs: [{
        ref: 'dataSetGrid',
        selector: 'reportdatasetgrid'
    },{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }],
    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.DataSets',
        'report.Reports'
    ],

    init: function () {
        this.control({
            'reportsorttoolbar button[action=add-column]': {
                click: this.showColumnSelector
            },
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

    refreshFilters: function () {
        this.application.fireEvent('refreshfilters');
    },
    
    refreshReport: function () {
        this.getReportDataSetsStore().buildDataSetGrid();
    },
    
    refreshSorts: function () {
        this.application.fireEvent('refreshsorts');
    },
    
    showColumnSelector: function(component, e, options) {
        var window = this.getReportColumnSelector();

        if (!window) {
            var store = this.getReportAvailableFieldsByCategoryStore();
            store.clearFilter();

            window = Ext.create('PICS.view.report.ColumnSelector');

            window._column_type = 'column';
            window.show();
        }
    }
});
