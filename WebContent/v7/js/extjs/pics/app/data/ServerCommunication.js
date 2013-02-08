Ext.define('PICS.data.ServerCommunication', {
    statics: (function () {
        function loadReportStore(json) {
            var report_store = Ext.StoreManager.get('report.Reports');

            report_store.setProxyForRead();

            report_store.loadRawData(json);
            
            return report_store;
        }

        function loadColumnStore(json) {
            var column_store = Ext.StoreManager.get('report.Columns');

            column_store.loadRawData(json);
            
            return column_store;
        }

        function loadFilterStore(json) {
            var filter_store = Ext.StoreManager.get('report.Filters');

            filter_store.loadRawData(json);
            
            return filter_store;
        }

        function loadDataTableStore(json) {
            var report_store = Ext.StoreManager.get('report.Reports'),
                report = report_store.first(),
                data_table_store = Ext.StoreManager.get('report.DataTables'),
                model_fields = report.convertColumnsToModelFields();

            // update data table model
            data_table_store.updateDataTableModelFields(model_fields);

            // load data table with results
            data_table_store.loadRawData(json);
            
            return data_table_store;
        }

        function startDataTableLoading() {
            var data_table_view = Ext.ComponentQuery.query('reportdatatable')[0];

            data_table_view.setLoading(true);
        }

        function stopDataTableLoading() {
            var data_table_view = Ext.ComponentQuery.query('reportdatatable')[0];

            data_table_view.setLoading(false);
        }

        function updateDataTableView(report) {
            var data_table_view = Ext.ComponentQuery.query('reportdatatable')[0],
                new_grid_columns = report.convertColumnsToGridColumns();

            data_table_view.updateGridColumns(new_grid_columns);
        }

        return {
            copyReport: function () {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    url = PICS.data.ServerCommunicationUrl.getCopyReportUrl();

                // flag store as dirty so it will sync data to server
                report.setDirty();

                // set load data proxy
                report_store.setProxyForWrite(url);

                report_store.sync({
                    success: function (batch, eOpts) {
                        var response = batch.operations[0].response,
                            data = response.responseText,
                            json = Ext.JSON.decode(data),
                            report_id = json.id;

                        window.location.href = 'Report.action?report=' + report_id;
                    },
                    failure: function (batch, eOpts) {
                        // TODO: error message - revert?
                    }
                });
            },
            
            exportReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getExportReportUrl();
                
                window.open(url);
            },

            favoriteReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getFavoriteReportUrl();

                Ext.Ajax.request({
                    url: url,
                    failure: function () {
                        // TODO: error message - revert?
                    }
                });
            },

            loadAll: function (options) {
                var url = PICS.data.ServerCommunicationUrl.getLoadAllUrl(),
                    callback = typeof options.callback == 'function' ? options.callback : function () {},
                    scope = options.scope ? options.scope : this;

                Ext.Ajax.request({
                    url: url,
                    success: function (response) {
                        var data = response.responseText,
                            json = Ext.JSON.decode(data);

                        loadReportStore(json);

                        loadColumnStore(json);

                        loadFilterStore(json);

                        loadDataTableStore(json);

                        callback.apply(scope, arguments);
                    }
                });
            },

            loadReportAndData: function () {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    report_id = report.get('id'),
                    url = PICS.data.ServerCommunicationUrl.getLoadReportAndDataUrl();

                // add data table loading mask
                startDataTableLoading();

                // flag store as dirty so it will sync data to server
                report.setDirty();

                // set load data proxy
                report_store.setProxyForWrite(url);

                // sync
                report_store.sync({
                    success: function (batch, eOpts) {
                        // TODO: sketchy
                        var response = batch.operations[0].response,
                            data = response.responseText,
                            json = Ext.JSON.decode(data);

                        // load the report store
                        var report_store = loadReportStore(json),
                            report = report_store.first();
                        

                        // load new results
                        loadDataTableStore(json);

                        // remove data table loading mask
                        stopDataTableLoading();

                        // refresh grid
                        updateDataTableView(report);

                    }
                });
            },

            loadData: function (page, limit) {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    report_id = report.get('id'),
                    data_table_store = Ext.StoreManager.get('report.DataTables'),
                    page = page ? page : 1,
                    limit = limit ? limit : data_table_store.pageSize,
                    url = PICS.data.ServerCommunicationUrl.getLoadDataUrl(page, limit);

                // updates the stores limit tracker
                data_table_store.setLimit(limit);

                // updates the stores page tracker
                data_table_store.setPage(page);

                // add data table loading mask
                startDataTableLoading();

                // flag store as dirty so it will sync data to server
                report.setDirty();

                // set load data proxy
                report_store.setProxyForWrite(url);

                // sync
                report_store.sync({
                    success: function (batch, eOpts) {
                        // TODO: sketchy
                        var response = batch.operations[0].response,
                            data = response.responseText,
                            json = Ext.JSON.decode(data);

                        // load new results
                        loadDataTableStore(json);

                        // remove data table loading mask
                        stopDataTableLoading();

                        // refresh grid
                        updateDataTableView(report);
                    }
                });
            },
            
            printReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getPrintReportUrl();
                
                window.open(url);
            },

            saveReport: function () {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    url = PICS.data.ServerCommunicationUrl.getSaveReportUrl();

                // flag store as dirty so it will sync data to server
                report.setDirty();

                // set load data proxy
                report_store.setProxyForWrite(url);

                report_store.sync({
                    success: function (batch, eOpts) {
                        // TODO: alert message
                    },
                    failure: function (batch, eOpts) {
                        // TODO: error message - revert?
                    }
                });
            },
            
            shareReport: function (options) {
                var account_id = options.account_id,
                    account_type = options.account_type,
                    is_editable = options.is_editable,
                    success_callback = options.success_callback ? options.success_callback : function () {},
                    failure_callback = options.failure_callback ? options.failure_callback : function () {},
                    url = PICS.data.ServerCommunicationUrl.getShareReportUrl();
                    
                if (!(account_id && account_type && is_editable)) {
                    Ext.Error.raise('Error');
                }
                
                Ext.Ajax.request({
                    url: url,
                    params: {
                        id: account_id,
                        type: account_type,
                        editable: is_editable
                    },
                    success: success_callback,
                    failure: failure_callback
                });
            },

            unfavoriteReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getUnfavoriteReportUrl();

                Ext.Ajax.request({
                    url: url,
                    failure: function () {
                        // TODO: error message - revert?
                    }
                });
            }
        };
    }())
});