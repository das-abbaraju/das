Ext.define('PICS.store.report.Data', {
    extend: 'Ext.data.Store',

    fields: [],
    proxy: {
        reader: {
            messageProperty: 'message',
            root: 'data',
            type: 'json'
        },
        type: 'ajax'
    },
    getReportParameters: function () {
        var report_store = Ext.StoreManager.get('report.Reports');
        var report = report_store && report_store.first();

        // TODO: instance of report store
        if (!report) {
            Ext.Msg.alert('Error', 'Missing report definition');

            return false;
        }

        var column_store = report.columns();
        var filter_store = report.filters();
        var sort_store = report.sorts();

        function configureReportParameters() {
            function getFieldDataFromStore(store) {
                var data = [];

                store.each(function (record) {
                    var item = {};

                    record.fields.each(function (field) {
                        var field_name = field.name;

                        item[field_name] = record.get(field_name);
                    });

                    data.push(item);
                });

                return data;
            }

            var data = getFieldDataFromStore(report_store)[0];

            if (data instanceof Object) {
                data.columns = getFieldDataFromStore(column_store);
                data.filters = getFieldDataFromStore(filter_store);
                data.sorts = getFieldDataFromStore(sort_store);
            }

            delete data.id;
            delete data.modelType;
            delete data.summary;
            delete data.description;

            return Ext.encode(data);
        }

        return configureReportParameters();
    },
    populateGrid: function () {
        var report_store = Ext.StoreManager.get('report.Reports');
        var report = report_store && report_store.first();

        // TODO: instance of report store
        if (!report) {
            Ext.Msg.alert('Error', 'Missing report definition');
            return false;
        }

        var me = this;
        var column_store = report.columns();
        var filter_store = report.filters();
        var sort_store = report.sorts();

        function buildReportDataStoreUrl(report_parameters) {
            var url = 'ReportDynamic!data.action?';

            if (report && report.getId() > 0) {
                url += 'report=' + report.getId();
            } else {
                url += 'report.base=' + report.get('base');
            }

            url += '&report.parameters=' + report_parameters;

            return url;
        }

        function buildReportDataStore() {
            function generateReportRowFields() {
                var column_store = report_store.first().columns();
                var fields = [];

                column_store.each(function (record) {
                    var report_row_field = record.convertRecordToReportRowField();

                    fields.push(report_row_field);
                });

                return fields;
            }

            function generateReportRowModel(fields) {
                // TODO: typecheck fields + existence

                var model = Ext.define('PICS.model.report.ReportRow', {
                    extend: 'Ext.data.Model',

                    fields: fields
                });

                return model;
            }

            function configureReportDataStore(model) {
                // TODO: typecheck model + existence
                var data_store = Ext.StoreManager.get('report.Data');

                data_store.removeAll(true);
                data_store.proxy.reader.setModel(model);

                return data_store;
            }

            var fields = generateReportRowFields();
            var model = generateReportRowModel(fields);
            var data_store = configureReportDataStore(model);

            return data_store;
        }

        var report_parameters = this.getReportParameters();
        var report_data_store_url = buildReportDataStoreUrl(report_parameters);
        var report_data_store = buildReportDataStore();

        report_data_store.proxy.url = report_data_store_url;

        // Run the report
        report_data_store.load({
            callback: function(records, operation, success) {
                if (success) {
                    var data_grid = Ext.ComponentQuery.query('reportdatagrid')[0];
                    var columns = report_store.first().columns().data.items;
                    var new_columns = [{
                        xtype: 'rownumberer',
                        width: 27
                    }];
                    for(var i = 0; i < columns.length; i++) {
                        new_columns.push(columns[i].toGridColumn());
                    }
                    data_grid.reconfigure(null, new_columns);
                } else {
                    Ext.Msg.alert('Failed to read data from Server', 'Reason: ' + operation.error);
                }
            }
        });
    }    
});