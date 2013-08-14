Ext.define('PICS.data.ServerCommunicationUrl', {
    statics: {
        getAutocompleteUrl: function (field_id, search_key) {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'Autocompleter.action?';

            var params = {
                reportId: report_id,
                fieldId: field_id,
                searchKey: search_key
            };

            return path + Ext.Object.toQueryString(params);
        },

        getBaseApiParams: function () {
            var query_string_params = Ext.Object.fromQueryString(window.location.search),
                base_api_params = {};

            if (query_string_params.report) {
                base_api_params.reportId = query_string_params.report;
            }

            if (query_string_params.dynamicParameters) {
                base_api_params.dynamicParameters = query_string_params.dynamicParameters
            }

            if (query_string_params.removeAggregates) {
                base_api_params.removeAggregates = query_string_params.removeAggregates
            }

            return base_api_params;
        },

        getCopyReportUrl: function () {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi!copy.action?';

            var params = {};

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getColumnFunctionUrl: function (report_type, field_id) {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi!buildSqlFunctions.action?';

            var params = {
                type: report_type,
                fieldId: field_id
            };

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getGetReportInfoUrl: function (report_id) {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi!info.action?';

            var params = {};

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getExportReportUrl: function () {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi!download.action?';

            var params = {};

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getFavoriteReportUrl: function () {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi!favorite.action?';

            var params = {};

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getRequestSubscriptionUrl: function () {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi!subscribe.action?';

            var params = {};

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getLoadAllUrl: function () {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi.action?';

            var params = {
                includeReport: true,
                includeColumns: true,
                includeFilters: true,
                includeData: true
            };

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getLoadReportAndDataUrl: function () {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi.action?';

            var params = {
                includeReport: true,
                includeData: true
            };

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getLoadDataUrl: function (page, limit) {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi.action?';

            var params = {
                includeData: true,
                page: page,
                limit: limit
            };

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getMultiSelectUrl: function (field_id) {
            var base_api_params = this.getBaseApiParams(),
                path = 'Autocompleter.action?';

            var params = {
                fieldId: field_id
            };

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getPrintReportUrl: function () {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi!print.action?';

            var params = {};

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },
        
        getReportAccessUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ManageReports!access.action?';
            
            var params = {
                reportId: report_id
            };

            return path + Ext.Object.toQueryString(params);
        },

        getSaveReportUrl: function () {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi!save.action?';

            var params = {};

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        },

        getShareWithAccountEditPermissionUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ManageReports!shareWithAccountEditPermission.action?';

            var params = {
                reportId: report_id
            };

            return path + Ext.Object.toQueryString(params);
        },

        getShareWithGroupEditPermissionUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ManageReports!shareWithGroupEditPermission.action?';

            var params = {
                reportId: report_id
            };

            return path + Ext.Object.toQueryString(params);
        },

        getShareWithUserEditPermissionUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ManageReports!shareWithUserEditPermission.action?';

            var params = {
                reportId: report_id
            };

            return path + Ext.Object.toQueryString(params);
        },

        getShareWithAccountViewPermissionUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ManageReports!shareWithAccountViewPermission.action?';

            var params = {
                reportId: report_id
            };

            return path + Ext.Object.toQueryString(params);
        },

        getShareWithGroupViewPermissionUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ManageReports!shareWithGroupViewPermission.action?';

            var params = {
                reportId: report_id
            };

            return path + Ext.Object.toQueryString(params);
        },

        getShareWithUserViewPermissionUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ManageReports!shareWithUserViewPermission.action?';

            var params = {
                reportId: report_id
            };

            return path + Ext.Object.toQueryString(params);
        },

        getUnfavoriteReportUrl: function () {
            var base_api_params = this.getBaseApiParams(),
                path = 'ReportApi!unfavorite.action?';

            var params = {};

            Ext.apply(params, base_api_params);

            return path + Ext.Object.toQueryString(params);
        }
    }
});