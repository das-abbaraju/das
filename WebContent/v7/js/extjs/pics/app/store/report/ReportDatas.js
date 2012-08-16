/**
 * ReportDatas Class
 *
 * Dynamically generates associated Data Model Class
 */
Ext.define('PICS.store.report.ReportDatas', {
    extend : 'PICS.store.report.base.Store',
    
    requires: [
        'Ext.window.MessageBox'
    ],

    // there is no preset Model - we must place empty fields [] as a default
    // we dynamically create / attach Model which has the actual fields
    fields: [],
    pageSize: 50,
    proxy: {
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
        timeout: 3000,
        type: 'ajax'
    },

    reload: function () {
        var store = Ext.StoreManager.get('report.Reports'),
            report = store.first();

        // initialize store page size
        report.set('rowsPerPage', this.pageSize);

        this.configureProxyUrl(report);

        this.configureReportDataModel(report);

        this.reconfigureReportData(report);
    },

    /**
     * Configure Proxy Url
     *
     * Build the remote proxy url so the store data can be properly fetched by the server
     *
     * @param report
     */
    configureProxyUrl: function (report) {
        if (!report || report.modelName != 'PICS.model.report.Report') {
            throw 'Invalid report record';
        }

        this.proxy.url = 'ReportDynamic!data.action?' + report.toQueryString();
    },

    configureReportDataModel: function (report) {
        if (!(report && report.$className == 'PICS.model.report.Report')) {
            throw '';
        }

        function generateReportDataModelFieldsFromColumnStore() {
            var column_store = report.columns(),
                model_fields = [];

            // generate model fields
            column_store.each(function (record) {
                model_fields.push(record.toModelField());
            });

            return model_fields;
        }

        function generateReportDataModel(model_fields) {
            var model = Ext.define('PICS.model.report.ReportData', {
                extend: 'Ext.data.Model',

                fields: model_fields
            });

            return model;
        }

        // dynamically create model for ReportDatas Store
        var model_fields = generateReportDataModelFieldsFromColumnStore();
        var model = generateReportDataModel(model_fields);

        // remove all data from ReportDatas Store
        this.removeAll(true);
        this.proxy.reader.setModel(model);
    },

    reconfigureReportData: function (report) {
        if (!report || report.modelName != 'PICS.model.report.Report') {
            throw 'Invalid report record';
        }

        var report_data = Ext.ComponentQuery.query('reportdata')[0],
            column_store = report.columns();

        var report_data_columns = [{
            xtype: 'rownumberer',
            height: 28,
            width: 50
        }];

        // See http://docs.sencha.com/ext-js/4-0/#!/api/Ext.grid.column.Column
        // generate data grid columns
        column_store.each(function (record) {
            report_data_columns.push(record.toGridColumn());
        });

        report_data.reconfigure(null, report_data_columns);
    }
});