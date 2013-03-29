PICS.define('report.manage-report.ManageReport', {
    methods: {
        init: function () {
            if ($('.ManageReports-page').length > 0) {
                
                $('body')
                    .on('report-favorite', this.favorite)
                    .on('report-unfavorite', this.unfavorite)
                    .on('report-filter', this.filter);
            }
        },
        
        favorite: function (event, options) {
            var report_id = options.report_id,
                success = typeof options.success == 'function' ? options.success : function () {},
                error = typeof options.error == 'function' ? options.error : function () {};
                
            if (!report_id) {
                throw 'Missing report id';
            }
            
            PICS.ajax({
                url: 'ReportApi!favorite.action',
                data: {
                    reportId: report_id
                },
                success: success,
                error: error
            });
        },
        
        filter: function (event, options) {
            var $filter = options.filter,
                success = typeof options.success == 'function' ? options.success : function () {},
                error = typeof options.error == 'function' ? options.error : function () {};
                
            if (!$filter) {
                throw 'Missing filter';
            }
                    
            PICS.ajax({
                url: $filter.attr('href'),
                success: success,
                error: error
            });
            
            this.resetFilters($filter);
            this.selectFilter($filter);
        },
        
        resetFilters: function ($filter) {
            var $filters = $filter.siblings();
            
            $filters.each(function (index) {
                var $filter = $(this),
                    params = PICS.getRequestParameters($filter[0].search);
                
                params.direction = 'ASC';
                
                $filter[0].search = $.param(params);
            });
            
            $filters.removeClass('active');
        },
        
        selectFilter: function ($filter) {
            var params = PICS.getRequestParameters($filter[0].search);
            
            params.direction = params.direction == 'DESC' ? 'ASC' : 'DESC'
            
            $filter[0].search = $.param(params);
            
            $filter.addClass('active');
        },
        
        unfavorite: function (event, options) {
            var report_id = options.report_id,
                success = typeof options.success == 'function' ? options.success : function () {},
                error = typeof options.error == 'function' ? options.error : function () {};
                
            if (!report_id) {
                throw 'Missing report id';
            }
            
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