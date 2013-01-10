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
        actionMethods: {
            create: 'POST',
            read: 'POST',
            update: 'POST',
            destroy: 'POST'
        },
        listeners: {
            exception: function (proxy, response, operation, eOpts) {
                if (operation.success == false) {
                    Ext.Msg.alert('Failed to read data from Server', 'Reason: ' + operation.error);
                }
            }
        },
        reader: {
            messageProperty: 'message',
            root: 'data',
            type: 'json'
        },
        timeout: 60000,
        type: 'ajax',
        url: '/ReportData!extjs.action'
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