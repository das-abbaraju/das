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
    pageSize: 50,
    proxy: {
        reader: {
            messageProperty: 'message',
            root: 'data',
            type: 'json'
        },
        type: 'ajax'
    },

    buildDataSetGrid: function () {
        this.initReportPaging();
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
    },    
    
    /**
     * Configure Proxy Url
     *
     * Build the remote proxy url so the store data can be properly fetched by the server
     *
     * @param report
     */
    configureProxyUrl: function () {
        var url = 'ReportDynamic!data.action?',
            reports = Ext.StoreManager.get('report.Reports');

        var parameters = reports.getReportParameters();

        this.proxy.url = url + Ext.Object.toQueryString(parameters);
    },
    
    initReportPaging: function () {
        var report = Ext.StoreManager.get('report.Reports').first(),
            paging_combo = Ext.ComponentQuery.query('pagingtoolbar combo[name=visibleRows]')[0];

        paging_combo.setValue(this.pageSize);
        
        report.set('rowsPerPage', this.pageSize);
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

    updateReportPaging: function (value) {
        var report = Ext.StoreManager.get('report.Reports').first(),
            paging_toolbar = Ext.ComponentQuery.query('reportdatasetgrid pagingtoolbar')[0];

        report.set('rowsPerPage', value);

        this.pageSize = value;

        this.configureProxyUrl();
        
        paging_toolbar.moveFirst();
    }
});