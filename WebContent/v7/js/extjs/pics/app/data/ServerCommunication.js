Ext.define('PICS.data.ServerCommunication', {
    statics: (function () {
        function loadReportStore(json) {
            var report_store = Ext.StoreManager.get('report.Reports');
            
            report_store.loadRawData(json);
        }
        
        function loadColumnStore(json) {
            var column_store = Ext.StoreManager.get('report.Columns');
            
            column_store.loadRawData(json);
        }
        
        function loadFilterStore(json) {
            var filter_store = Ext.StoreManager.get('report.Filters');
            
            filter_store.loadRawData(json);
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
            getLoadAllUrl: function () {
                var params = Ext.Object.fromQueryString(window.location.search),
                    report_id = params.report,
                    path = 'ReportApi.action?';

                var params = {
                    reportId: report_id,
                    includeReport: true,
                    includeColumns: true,
                    includeFilters: true,
                    includeData: true
                };

                return path + Ext.Object.toQueryString(params);
            },
            
            getLoadReportAndDataUrl: function () {
                var params = Ext.Object.fromQueryString(window.location.search),
                    report_id = params.report,
                    path = 'ReportApi.action?';
                
                var params = {
                    reportId: report_id,
                    includeReport: true,
                    includeData: true
                };
    
                return path + Ext.Object.toQueryString(params);
            },
            
            getLoadDataUrl: function (page, limit) {
                var params = Ext.Object.fromQueryString(window.location.search),
                    report_id = params.report,
                    path = 'ReportApi.action?';
                
                var params = {
                    reportId: report_id,
                    includeData: true,
                    page: page,
                    limit: limit
                };
    
                return path + Ext.Object.toQueryString(params);
            },
            
            loadAll: function (options) {
                 var url = this.getLoadAllUrl(),
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
                    url = this.getLoadDataUrl();
                
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
                        
                        report_store.setProxyForRead();
                        
                        loadReportStore(json);
                        
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
                    url = this.getLoadDataUrl(page, limit);

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
            }
        };
    }())
});