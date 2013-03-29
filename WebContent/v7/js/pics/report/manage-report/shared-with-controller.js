PICS.define('report.manage-report.SharedWithController', {
    methods: {
        init: function () {
            if ($('#ManageReports_sharedWith_page').length > 0) {
                $('#shared_with_reports_container')
                    .on('click', '.favorite-icon.favorite', $.proxy(this.unfavoriteReport, this))
                    .on('click', '.favorite-icon.unfavorite', $.proxy(this.favoriteReport, this))
                    .on('click', '.favorite-action.favorite', $.proxy(this.favoriteReport, this))
                    .on('click', '.favorite-action.unfavorite', $.proxy(this.unfavoriteReport, this))
                    .on('click', '#manage_report_filter a', $.proxy(this.filterList, this));
            }
        },
        
        favoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $favorite_icon = $report.find('.favorite-icon'),
                $favorite_action = $report.find('.favorite-action'),
                $icon = $report.find('.icon-star'),
                $body = $('body'),
                report_id = $element.attr('data-id');
            
            $body.trigger('report-favorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $icon.addClass('selected');
                    $favorite_icon.toggleClass('favorite unfavorite');
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
        
        unfavoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $favorite_icon = $report.find('.favorite-icon'),
                $favorite_action = $report.find('.favorite-action'),
                $icon = $report.find('.icon-star'),
                $body = $('body'),
                report_id = $element.attr('data-id');
            
            $body.trigger('report-unfavorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $icon.removeClass('selected');
                    $favorite_icon.toggleClass('favorite unfavorite');
                    $favorite_action.toggleClass('favorite unfavorite');
                    $favorite_action.text('Favorite');
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
        }
    }
});