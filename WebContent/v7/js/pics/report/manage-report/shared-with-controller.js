PICS.define('report.manage-report.SharedWithController', {
    methods: {
        init: function () {
            if ($('#ManageReports_sharedWith_page').length > 0) {
                $('#shared_with_reports_container')
                    .on('click', '.report > .favorite', this.unfavoriteReport)
                    .on('click', '.report > .unfavorite', this.favoriteReport)
                    .on('click', '.report-options .favorite', this.favoriteReport)
                    .on('click', '.report-options .unfavorite', this.unfavoriteReport)
                    .on('click', '.report-options .remove', this.removeReport)
                    .on('click', '#manage_report_filter a', this.filterList);
            }
        },
        
        favoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $report_options = $report.find('.report-options'),
                $favorite_star = $report.children('.favorite, .unfavorite'),
                $favorite_icon = $favorite_star.find('.icon-star'),
                $favorite_action = $report_options.find('.favorite, .unfavorite'),
                $body = $('body'),
                report_id = $element.data('report-id');
            
            $body.trigger('report-favorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $favorite_star.toggleClass('favorite unfavorite');
                    $favorite_icon.addClass('selected');
                    $favorite_action.toggleClass('favorite unfavorite');
                    $favorite_action.text('Unfavorite');
                }
            });
            
            event.preventDefault();
        },
        
        filterList: function (event) {
            var $filter = $(event.currentTarget);
            
            $('body').trigger('report-filter', {
                filter: $filter,
                success: function (data, textStatus, jqXHR) {
                    $('#shared_with_reports_container').html(data);
                }
            });
            
            event.preventDefault();
        },
        
        refreshSharedWith: function () {
            PICS.ajax({
                url: 'ManageReports!sharedWith.action',
                success: function (data, textStatus, jqXHR) {
                    $('#shared_with_reports_container').html(data);
                }
            });
        },
        
        removeReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                report_id = $element.data('report-id');
            
            PICS.ajax({
                url: 'ManageReports!removeReport.action',
                data: {
                    reportId: report_id,
                }
            });
            
            $report.slideUp(400, function () {
                $(this).remove();
            });
            
            event.preventDefault();
        },
        
        unfavoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $report_options = $report.find('.report-options'),
                $favorite_star = $report.children('.favorite, .unfavorite'),
                $favorite_icon = $favorite_star.find('.icon-star'),
                $favorite_action = $report_options.find('.favorite, .unfavorite'),
                $body = $('body'),
                report_id = $element.data('report-id');
            
            $body.trigger('report-unfavorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $favorite_star.toggleClass('favorite unfavorite');
                    $favorite_icon.removeClass('selected');
                    $favorite_action.toggleClass('favorite unfavorite');
                    $favorite_action.text('Favorite');
                }
            });
            
            event.preventDefault();
        }
    }
});