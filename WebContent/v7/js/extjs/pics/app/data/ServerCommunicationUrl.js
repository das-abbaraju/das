Ext.define('PICS.data.ServerCommunicationUrl', {
    statics: {
        getCopyReportUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ReportDynamic!copy.action?';
            
            var params = {
                reportId: report_id
            };
            
            return path + Ext.Object.toQueryString(params);
        },
        
        getFavoriteReportUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ManageReports!favorite.action?';
            
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
        
        getSaveReportUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ReportDynamic!save.action?';
            
            var params = {
                reportId: report_id
            };
            
            return path + Ext.Object.toQueryString(params);
        },
        
        getUnfavoriteReportUrl: function () {
            var params = Ext.Object.fromQueryString(window.location.search),
                report_id = params.report,
                path = 'ManageReports!unfavorite.action?';
            
            var params = {
                reportId: report_id
            };
            
            return path + Ext.Object.toQueryString(params);
        },
    }
});