Ext.define('PICS.store.report.DataTables', {
    extend : 'PICS.store.report.base.Store',
    model : 'PICS.model.report.DataTable',

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
    
    updateDataTableModelFields: function (model_fields) {
        var data_table_model = Ext.ModelManager.getModel('PICS.model.report.DataTable');
        
        // update model fields
        data_table_model.setFields(model_fields);
    }
});