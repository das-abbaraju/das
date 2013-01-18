/**
 * ReportDatas Class
 *
 * Dynamically generates associated Data Model Class
 */
Ext.define('PICS.store.report.ReportDatas', {
    extend : 'PICS.store.report.base.Store',
    model : 'PICS.model.report.ReportData',

    requires: [
        'Ext.window.MessageBox'
    ],

    proxy: {
        reader: {
            root: 'results.data',
            totalProperty: 'results.total',
            type: 'json'
        },
        type: 'memory'
    },

    setLimit: function (limit) {
        this.pageSize = limit;
    },
    
    updateProxyParameters: function (params) {
        this.proxy.extraParams = params;
    },
    
    updateReportDataModelFields: function (model_fields) {
        var report_data_model = Ext.ModelManager.getModel('PICS.model.report.ReportData');
        
        // update model fields
        report_data_model.setFields(model_fields);
    }
});