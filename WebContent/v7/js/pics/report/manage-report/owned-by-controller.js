PICS.define('report.manage-report.OwnedByController', {
    methods: {
        init: function () {
            if ($('#ManageReports_ownedBy_page').length > 0) {
                $('#owned_by_reports_container')
                    .on('click', '.favorite-icon.favorite', this.unfavoriteReport)
                    .on('click', '.favorite-icon.unfavorite', this.favoriteReport)
                    .on('click', '.favorite-action.favorite', this.favoriteReport)
                    .on('click', '.favorite-action.unfavorite', this.unfavoriteReport)
                    .on('click', '#manage_report_filter a', this.filterList);
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
                    $('#owned_by_reports_container').html(data);
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
        
        refreshOwnedBy: function () {
            PICS.ajax({
                url: 'ManageReports!ownedBy.action',
                success: function (data, textStatus, jqXHR) {
                    $('#owned_by_reports_container').html(data);
                }
            });
        }
    }
});