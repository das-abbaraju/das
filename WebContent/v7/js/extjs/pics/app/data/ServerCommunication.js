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
        
        function loadReportDataStore(json) {
            var report_store = Ext.StoreManager.get('report.Reports'),
                report = report_store.first(),
                report_data_store = Ext.StoreManager.get('report.ReportDatas'),
                model_fields = report.convertColumnsToModelFields();
            
            // update report data model
            report_data_store.updateReportDataModelFields(model_fields);
            
            // load report data with results
            report_data_store.loadRawData(json);
        }
            
        function updateReportDataView(report) {
            var report_data_view = Ext.ComponentQuery.query('reportdata')[0],
                new_grid_columns = report.convertColumnsToGridColumns();
            
            report_data_view.updateGridColumns(new_grid_columns);
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
                    page: page ? page : 1,
                    limit: limit ? limit : 50
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
                        
                        loadReportDataStore(json);

                        callback.apply(scope, arguments);
                    }
                });
            },
            
            loadReportAndData: function () {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    report_id = report.get('id'),
                    url = this.getLoadDataUrl();
                
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
                        loadReportDataStore(json);
                        
                        // refresh grid
                        updateReportDataView(report);
                    }
                });
            },
            
            loadData: function (page, limit) {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    report_id = report.get('id'),
                    url = this.getLoadDataUrl(page, limit);
                
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
                        loadReportDataStore(json);
                        
                        // refresh grid
                        updateReportDataView(report);
                    }
                });
            }
        };
    }())
});