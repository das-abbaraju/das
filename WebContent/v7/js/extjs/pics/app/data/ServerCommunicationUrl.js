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
        
        getCopyReportUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ReportApi!copy.action?';
            
            var params = {
                reportId: report_id
            };
            
            return path + Ext.Object.toQueryString(params);
        },
        
        getFavoriteReportUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ReportApi!favorite.action?';
            
            var params = {
                reportId: report_id
            };
            
            return path + Ext.Object.toQueryString(params);
        },
        
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
        
        getMultiSelectUrl: function (field_id) {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'Autocompleter.action?';
            
            var params = {
                fieldType: field_id
            };
            
            return path + Ext.Object.toQueryString(params);
        },
        
        getSaveReportUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ReportApi!save.action?';
            
            var params = {
                reportId: report_id
            };
            
            return path + Ext.Object.toQueryString(params);
        },
        
        getUnfavoriteReportUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ReportApi!unfavorite.action?';
            
            var params = {
                reportId: report_id
            };
            
            return path + Ext.Object.toQueryString(params);
        },
    }
});