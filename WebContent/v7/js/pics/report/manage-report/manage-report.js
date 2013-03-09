PICS.define('report.manage-report.ManageReport', {
    methods: {
        init: function () {
            if ($('#ManageReports_myReports_page').length || $('#ManageReports_favorites_page').length) {
                var body = $('body');
                
                body.on('report-favorite', this.favorite);
                body.on('report-unfavorite', this.unfavorite);
            }
        },
        
        favorite: function (event, report_id, success, error) {
            if (!report_id) {
                throw 'Missing report id';
            }
            
            var success = typeof success == 'function' ? success : function () {},
                error = typeof error == 'function' ? error : function () {};
            
            PICS.ajax({
                url: 'ReportApi!favorite.action',
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
            
            var success = typeof success == 'function' ? success : function () {},
                error = typeof error == 'function' ? error : function () {};
            
            PICS.ajax({
                url: 'ReportApi!unfavorite.action',
                data: {
                    reportId: report_id
                },
                success: success,
                error: error
            });
        }
    }
});