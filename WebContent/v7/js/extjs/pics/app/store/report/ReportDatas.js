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

    pageSize: 50,
    proxy: {
        reader: {
            root: 'results.data',
            totalProperty: 'results.total',
            type: 'json'
        },
        type: 'memory'
    },
    
    // overriding to include start for buffered store
    // see Ext.data.Store.loadRecords
    loadRawData : function(data, append) {
        var me      = this,
            result  = me.proxy.reader.read(data),
            records = result.records,
            options = append ? me.addRecordsOptions : {};

        options.start = (me.currentPage - 1) * me.pageSize;

        if (result.success) {
            me.totalCount = result.total;
            
            me.loadRecords(records, options);
            
            me.fireEvent('load', me, records, true);
        }
    },

    setLimit: function (limit) {
        this.pageSize = limit;
    },
    
    setPage: function (page) {
        this.currentPage = page;
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