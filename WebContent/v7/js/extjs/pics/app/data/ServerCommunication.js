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

        /* For our purposes, a "response object" is one that contains either a status or responseText property or both.
         * 
         * Ext.Ajax.request(), on success, returns a status property (of value 2xx) and a reponseText property value: { status: 2xx, responseText: { ... } }
         * If the backend caught an error, this responseText property will contain a JSON string representing exception data used by PICS.data.Exception.
         * On failure, Ext.Ajax.request() returns a non-2xx status value but no responseText: { status: non-2xx }
         * Both of these qualify as "response objects".
         * 
         * A store's sync method, however, returns a different set of data. If we need a response object following a call to sync,
         * then we must create the response object ourselves from the data that sync provides. This method primarily serves that purpose.
         */
        function createResponse(operation, jsonData) {
            var response = {};

            // If the server returned a non-2xx status code, then we can get our response object's "status" value from operation.error.status.
            if (operation.error && operation.error.status) {
                response.status = operation.error.status;

            // If the store's reader contains JSON data, then we can create our response object's "responseText" value by converting that data to a string.
            // Otherwise, we first need to create the JSON data to convert.
            } else {
                
                // Since the response had no error, it was successful but it contained useless data.
                if (!jsonData) {
                    var unknown_error = PICS.data.Exception.getUnknownError();

                    jsonData = {
                        title: unknown_error.title,
                        message: unknown_error.message,
                        success: false
                    };
                }
                
                response.responseText = Ext.encode(jsonData);
            }
            
            return response;
        }

        return {
            copyReport: function () {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    report_copy = report_store.add(report)[0],
                    url = PICS.data.ServerCommunicationUrl.getCopyReportUrl();

                // Remove the original report, so that sync sends only the copy.
                report_store.remove(report);

                // Flag the store as dirty so that sync will execute.
                report_copy.setDirty();

                // set load data proxy
                report_store.setProxyForWrite(url);
                
                report_store.sync({
                    callback: function (batch, eOpts) {
                        var operation = batch.operations[batch.current],
                            response = operation.response,
                            jsonData = this.getReader().jsonData;

                        // sync does not return response when the server sends "success: false"
                        if (!response) {
                            response = createResponse(operation, jsonData);
                        }

                        if (PICS.data.Exception.hasException(response)) {
                            PICS.data.Exception.handleException({
                                response: response
                            });
                        } else {
                            var report_id = jsonData.id;

                            window.location.href = 'Report.action?report=' + report_id;
                        }
                    }
                });
            },
            
            exportReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getExportReportUrl();

                this.sendReportViaForm(url, '_self');
            },

            favoriteReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getFavoriteReportUrl();

                Ext.Ajax.request({
                    url: url,
                    callback: function (options, success, response) {                        
                        if (PICS.data.Exception.hasException(response)) {
                            PICS.data.Exception.handleException({
                                response: response
                            });
                        }
                    }
                });

            },

            loadAll: function (options) {
                var url = PICS.data.ServerCommunicationUrl.getLoadAllUrl(),
                    success_callback = typeof options.success_callback == 'function' ? options.success_callback : function () {},
                    scope = options.scope ? options.scope : this;

                Ext.Ajax.request({
                    url: url,
                    callback: function (options, success, response) {                        
                        if (PICS.data.Exception.hasException(response)) {
                            PICS.data.Exception.handleException({
                                response: response
                            });
                        } else {
                            var data = response.responseText,
                                json = Ext.JSON.decode(data);
    
                            loadReportStore(json);
    
                            loadColumnStore(json);
    
                            loadFilterStore(json);
    
                            loadDataTableStore(json);
    
                            success_callback.apply(scope, arguments);
                        }
                    }
                });
            },

            loadReportAndData: function () {
                var report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    report_id = report.get('id'),
                    has_unsaved_changes = report.getHasUnsavedChanges(),
                    url = PICS.data.ServerCommunicationUrl.getLoadReportAndDataUrl();

                // add data table loading mask
                startDataTableLoading();

                // flag store as dirty so it will sync data to server
                report.setDirty();

                // set load data proxy
                report_store.setProxyForWrite(url);
                
                // sync
                report_store.sync({
                    callback: function (batch, eOpts) {
                        var operation = batch.operations[batch.current],
                            response = operation.response,
                            jsonData = this.getReader().jsonData;

                        if (!response) {
                            response = createResponse(operation, jsonData);
                        }
    
                        if (PICS.data.Exception.hasException(response)) {
                            PICS.data.Exception.handleException({
                                response: response
                            });
                        } else {
                            // load the report store
                            var json = this.getReader().jsonData,
                                report_store = loadReportStore(json),
                                report = report_store.first();

                            // Persist the unsaved changes flag.
                            report.setHasUnsavedChanges(has_unsaved_changes);

                            // load new results
                            loadDataTableStore(json);

                            // remove data table loading mask
                            stopDataTableLoading();

                            // refresh grid
                            updateDataTableView(report);
                        }
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
                
                // sync
                report_store.sync({
                    callback: function (batch, eOpts) {
                        var operation = batch.operations[batch.current],
                            response = operation.response,
                            jsonData = this.getReader().jsonData;
    
                        if (!response) {
                            response = createResponse(operation, jsonData);
                        }
    
                        if (PICS.data.Exception.hasException(response)) {
                            PICS.data.Exception.handleException({
                                response: response
                            });
                        } else {
                            var json = this.getReader().jsonData,
                                report_store = Ext.StoreManager.get('report.Reports'),
                                report = report_store.first();

                            // load new results
                            loadDataTableStore(json);

                            // remove data table loading mask
                            stopDataTableLoading();

                            // refresh grid
                            updateDataTableView(report);
                        }
                    }
                });
            },

            printReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getPrintReportUrl();

                this.sendReportViaForm(url, '_blank');
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
                
                report_store.sync({
                    callback: function (batch, eOpts) {
                        var operation = batch.operations[batch.current],
                            response = operation.response,
                            jsonData = this.getReader().jsonData;

                        if (!response) {    
                            response = createResponse(operation, jsonData);
                        }
    
                        if (PICS.data.Exception.hasException(response)) {
                            PICS.data.Exception.handleException({
                                response: response
                            });
                    } else {
                            report.setHasUnsavedChanges(false);

                            success_callback();
                        }
                    }
                });
            },

            sendReportViaForm: function (url, target) {
                var form = document.createElement('form'),
                    ext_form = new Ext.Element(form),
                    input = document.createElement('input'),
                    ext_input = new Ext.Element(input),
                    report_store = Ext.StoreManager.get('report.Reports'),
                    report = report_store.first(),
                    json = report.getRecordDataAsJson(report);

                ext_form.set({
                    action: url,
                    method: 'post',
                    target: target
                });

                ext_form.addCls('send-report-form');

                ext_input.set({
                    name: 'reportJson',
                    value: json,
                    type: 'hidden'
                });

                ext_form.appendChild(input);
                Ext.getBody().appendChild(ext_form);

                ext_form.dom.submit();
            },

            shareReport: function (options) {
                var id = options.id,
                    access_type = options.access_type,
                    is_editable = options.is_editable,
                    success_callback = typeof options.success_callback == 'function' ? options.success_callback : function () {},
                    failure_callback = typeof options.failure_callback == 'function' ? options.failure_callback : function () {},
                    url = '';

                if (!(id && access_type && typeof is_editable != 'undefined')) {
                    Ext.Error.raise('Error');
                }

                if (is_editable) {
                    switch (access_type) {
                        case 'account':
                            url = PICS.data.ServerCommunicationUrl.getShareWithAccountEditPermissionUrl();
                            break;
                        case 'group':
                            url = PICS.data.ServerCommunicationUrl.getShareWithGroupEditPermissionUrl();
                            break;
                        case 'user':
                        default:
                            url = PICS.data.ServerCommunicationUrl.getShareWithUserEditPermissionUrl();
                            break;
                    }
                } else {
                    switch (access_type) {
                        case 'account':
                            url = PICS.data.ServerCommunicationUrl.getShareWithAccountViewPermissionUrl();
                            break;
                        case 'group':
                            url = PICS.data.ServerCommunicationUrl.getShareWithGroupViewPermissionUrl();
                            break;
                        case 'user':
                        default:
                            url = PICS.data.ServerCommunicationUrl.getShareWithUserViewPermissionUrl();
                            break;
                    }
                }

                Ext.Ajax.request({
                    url: url,
                    params: {
                        shareId: id
                    },
                    callback: function (options, success, response) {
                        if (PICS.data.Exception.hasException(response)) {
                            PICS.data.Exception.handleException({
                                response: response
                            });
                        } else {
                            if (success) {
                                success_callback(response);
                            } else {
                                failure_callback(response);
                            }
                        }
                    }
                });
            },

            unfavoriteReport: function () {
                var url = PICS.data.ServerCommunicationUrl.getUnfavoriteReportUrl();

                Ext.Ajax.request({
                    url: url,
                    callback: function (options, success, response) {                        
                        if (PICS.data.Exception.hasException(response)) {
                            PICS.data.Exception.handleException({
                                response: response
                            });
                        }
                    }
                });
            }
        };
    }())
});