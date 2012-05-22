/**
 * Data Sets Class
 *
 * Dynamically generates associated Data Model Class
 */
Ext.define('PICS.store.report.DataSets', {
    extend: 'Ext.data.Store',

    // there is no preset Model - we must place empty fields [] as a default
    // we dynamically create / attach Model which has the actual fields
    fields: [],
    proxy: {
        reader: {
            messageProperty: 'message',
            root: 'data',
            type: 'json'
        },
        type: 'ajax'
    },

    /**
     * Get Report JSON
     *
     * Builds a jsonified version of the report to be sent to the server
     */
    getReportJSON: function () {
        var report_store = Ext.StoreManager.get('report.Reports');
        var report = report_store && report_store.first();
        var report_data;

        if (!report) {
            throw 'Data.getReportJSON missing report';
        }

        function convertStoreToDataObject(store) {
            var data = [];

            store.each(function (record) {
                var item = {};

                record.fields.each(function (field) {
                    item[field.name] = record.get(field.name);
                });

                data.push(item);
            });

            return data;
        }

        report_data = report.data;
        report_data.columns = convertStoreToDataObject(report.columns());
        report_data.filters = convertStoreToDataObject(report.filters());
        report_data.sorts = convertStoreToDataObject(report.sorts());

        return Ext.encode(report_data);
    },

    /**
     * Configure Proxy Url
     *
     * Build the remote proxy url so the store data can be properly fetched by the server
     *
     * @param report
     */
    configureProxyUrl: function () {
        var report_store = Ext.StoreManager.get('report.Reports');
        var report = report_store && report_store.first();

        if (!report) {
            throw 'Data.getReportJSON missing report';
        }

        var report_json = this.getReportJSON();
        var url = 'ReportDynamic!data.action?';
        var parameters = {};

        if (report && report.getId() > 0) {
            parameters['report'] = report.getId();
        } else {
            parameters['report.base'] = report.get('base');
        }

        parameters['report.parameters'] = report_json;

        this.proxy.url = url + Ext.Object.toQueryString(parameters);
    },

    /**
     * Reload Report Data Set
     *
     * Refresh the report grid
     */
    reloadReportDataSet: function () {
        var report_store = Ext.StoreManager.get('report.Reports');
        var report = report_store && report_store.first();

        if (!report) {
            throw 'Data.getReportJSON missing report';
        }

        var data_set_grid = Ext.ComponentQuery.query('reportdatasetgrid')[0];
        var column_store = report.columns();
        var data_set_columns = [{
            xtype: 'rownumberer',
            width: 27
        }];

        // See http://docs.sencha.com/ext-js/4-0/#!/api/Ext.grid.column.Column
        // generate data grid columns
        column_store.each(function (record) {
            data_set_columns.push(record.toDataSetGridColumn());
        });

        data_set_grid.reconfigure(null, data_set_columns);
    },

    /**
     * Reload Store Data
     *
     * Fetch new data from the server by making a request to the configured proxy url
     *
     * @param callback
     */
    reloadStoreData: function (callback) {
        function generateDataSetModelFieldsFromColumnStore() {
            var report_store = Ext.StoreManager.get('report.Reports');
            var report = report_store && report_store.first();

            if (!report) {
                throw 'Data.getReportJSON missing report';
            }

            var column_store = report.columns();
            var model_fields = [];

            // generate model fields
            column_store.each(function (record) {
                model_fields.push(record.toDataSetModelField());
            });

            return model_fields;
        }

        function generateDataSetModel(model_fields) {
            var model = Ext.define('PICS.model.report.DataSet', {
                extend: 'Ext.data.Model',

                fields: model_fields
            });

            return model;
        }

        // dynamically create model for DataSets Store
        var model_fields = generateDataSetModelFieldsFromColumnStore();
        var model = generateDataSetModel(model_fields);

        // remove all data from DataSets Store
        this.removeAll(true);
        this.proxy.reader.setModel(model);

        // reload store data
        this.load(callback);
    },

    buildDataSetGrid: function () {
        this.configureProxyUrl();

        this.reloadStoreData({
            callback: function(records, operation, success) {
                if (success) {
                    this.reloadReportDataSet();
                } else {
                    Ext.Msg.alert('Failed to read data from Server', 'Reason: ' + operation.error);
                }
            }
        });
    }
});