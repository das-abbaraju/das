PICS.define('report.Report', {
    methods: {
        init: function () {
            if ($('#ManageReports_myReportsList_page').length || $('#ManageReports_favoritesList_page').length) {
                var body = $('body');
                
                body.on('report-favorite', this.favorite);
                body.on('report-unfavorite', this.unfavorite);
            }
        },
        
        favorite: function (event, report_id, success, error) {
            if (!report_id) {
                throw 'Missing report id';
            }
            
            if (typeof success != 'function') {
                success = function (data, textStatus, jqXHR) {};
            }
            
            if (typeof error != 'function') {
                error = function (jqXHR, textStatus, errorThrown) {};
            }
            
            PICS.ajax({
                url: 'ManageReports!favorite.action',
                data: {
                    reportId: report_id
                },
                success: success,
                error: error
            });
        },
        
        unfavorite: function (event, report_id, success, error) {
            if (!report_id) {
                throw 'Missing report id';
            }
            
            if (typeof success != 'function') {
                success = function (data, textStatus, jqXHR) {};
            }
            
            if (typeof error != 'function') {
                error = function (jqXHR, textStatus, errorThrown) {};
            }
            
            PICS.ajax({
                url: 'ManageReports!unfavorite.action',
                data: {
                    reportId: report_id
                },
                success: success,
                error: error
            });
        }
    }
});