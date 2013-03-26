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
                
                function callback(conn, response, options, eOpts) {
                    if (PICS.data.Exception.hasException(response)) {
                        PICS.data.Exception.handleException({
                            response: response
                        });
                    } else {
                        var data = response.responseText,
                            json = Ext.JSON.decode(data),
                            report_id = json.id;

                        window.location.href = 'Report.action?report=' + report_id;
                    }
                } 
                
                Ext.Ajax.on({
                    requestcomplete: {
                        fn: callback,
                        scope: this,
                        single: true
                    },
                    requestexception: {
                        fn: callback,
                        scope: this,
                        single: true
                    }
                });

                report_store.sync();
            },
            
            exportReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getExportReportUrl();
                
                window.open(url);
            },

            favoriteReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getFavoriteReportUrl();

                PICS.Ajax.request({
                    url: url
                });
            },

            loadAll: function (options) {
                var url = PICS.data.ServerCommunicationUrl.getLoadAllUrl(),
                    success_callback = typeof options.success_callback == 'function' ? options.success_callback : function () {},
                    scope = options.scope ? options.scope : this;

                PICS.Ajax.request({
                    url: url,
                    success: function (response) {
                        var data = response.responseText,
                            json = Ext.JSON.decode(data);

                        loadReportStore(json);

                        loadColumnStore(json);

                        loadFilterStore(json);

                        loadDataTableStore(json);

                        success_callback.apply(scope, arguments);
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
                
                function callback(conn, response, options, eOpts) {
                    if (PICS.data.Exception.hasException(response)) {
                        var data = response.responseText,
                            json = Ext.JSON.decode(data),
                            report_store = Ext.StoreManager.get('report.Reports'),
                            report = report_store.first();
                        
                        PICS.data.Exception.handleException({
                            response: response,
                            callback: function () {
                                report.rejectAllChanges();
                                
                                PICS.data.ServerCommunication.loadData();
                            }
                        });
                    } else {
                        var data = response.responseText,
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
                } 
                
                Ext.Ajax.on({
                    requestcomplete: {
                        fn: callback,
                        scope: this,
                        single: true
                    },
                    requestexception: {
                        fn: callback,
                        scope: this,
                        single: true
                    }
                });

                // sync
                report_store.sync();
            },

            loadData: function (page, limit) {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    report_id = report.get('id'),
                    data_table_store = Ext.StoreManager.get('report.DataTables'),
                    page = page ? page : 1,
                    limit = limit ? limit : data_table_store.pageSize,
                    url = PICS.data.ServerCommunicationUrl.getLoadDataUrl(page, limit),
                    that = this;

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
                
                function callback(conn, response, options, eOpts) {
                    if (PICS.data.Exception.hasException(response)) {
                        var data = response.responseText,
                            json = Ext.JSON.decode(data),
                            report_store = Ext.StoreManager.get('report.Reports'),
                            report = report_store.first();
                        
                        PICS.data.Exception.handleException({
                            response: response,
                            callback: function () {
                                report.rejectAllChanges();
                                
                                PICS.data.ServerCommunication.loadData();
                            }
                        });
                    } else {
                        var data = response.responseText,
                            json = Ext.JSON.decode(data);
                        
                        var report_store = Ext.StoreManager.get('report.Reports'),
                            report = report_store.first();

                        // load new results
                        loadDataTableStore(json);

                        // remove data table loading mask
                        stopDataTableLoading();

                        // refresh grid
                        updateDataTableView(report);
                    }
                }
                
                Ext.Ajax.on({
                    requestcomplete: {
                        fn: callback,
                        scope: this,
                        single: true
                    },
                    requestexception: {
                        fn: callback,
                        scope: this,
                        single: true
                    }
                });

                // sync
                report_store.sync();
            },
            
            printReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getPrintReportUrl();
                
                window.open(url);
            },

            saveReport: function (options) {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    success_callback = typeof options.success_callback == 'function' ? options.success_callback : function () {},
                    url = PICS.data.ServerCommunicationUrl.getSaveReportUrl();

                // flag store as dirty so it will sync data to server
                report.setDirty();

                // set load data proxy
                report_store.setProxyForWrite(url);
                
                function callback(conn, response, options, eOpts) {
                    if (PICS.data.Exception.hasException(response)) {
                        PICS.data.Exception.handleException({
                            response: response
                        });
                    } else {
                        success_callback();
                    }
                }
                
                Ext.Ajax.on({
                    requestcomplete: {
                        fn: callback,
                        scope: this,
                        single: true
                    },
                    requestexception: {
                        fn: callback,
                        scope: this,
                        single: true
                    }
                });

                report_store.sync();
            },
            
            shareReport: function (options) {
                var account_id = options.account_id,
                    account_type = options.account_type,
                    is_editable = options.is_editable,
                    success_callback = typeof options.success_callback == 'function' ? options.success_callback : function () {},
                    failure_callback = typeof options.failure_callback == 'function' ? options.failure_callback : function () {},
                    url = '';

                if (!(account_id && account_type && typeof is_editable != 'undefined')) {
                    Ext.Error.raise('Error');
                }

                switch (account_type) {
                    case 'account':
                        url = PICS.data.ServerCommunicationUrl.getShareReportWithAccountUrl();
                        break;
                    case 'user':
                        url = PICS.data.ServerCommunicationUrl.getShareReportWithUserUrl(is_editable);
                        break;
                    case 'group':
                    default:
                        url = PICS.data.ServerCommunicationUrl.getShareReportWithGroupUrl(is_editable);
                        break;
                }

                Ext.Ajax.request({
                    url: url,
                    params: {
                        shareId: account_id
                    },
                    success: success_callback,
                    failure: failure_callback
                });
            },

            unfavoriteReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getUnfavoriteReportUrl();

                PICS.Ajax.request({
                    url: url
                });
            }
        };
    }())
});