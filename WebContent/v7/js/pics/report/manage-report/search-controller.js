PICS.define('report.manage-report.SearchController', {
    methods: {
        init: function () {
            if ($('#ManageReports_search_page').length > 0) {
                $('#report_search_form input[type=text]')
                    .on('keyup', PICS.debounce(this.search, 250));
                
                $('#search_reports_container')
                    .on('click', '.favorite-icon.favorite', this.favoriteReport)
                    .on('click', '.favorite-icon.unfavorite', this.unfavoriteReport);
            }
        },
        
        favoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $favorite_icon = $report.find('.favorite-icon'),
                $icon = $report.find('.icon-star'),
                $body = $('body'),
                report_id = $element.attr('data-id');
            
            $body.trigger('report-favorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $icon.addClass('selected');
                    $favorite_icon.toggleClass('favorite unfavorite');
                }
            });
            
            event.preventDefault();
        },
        
        search: function (event) {
            var element = $(this);
            
            PICS.ajax({
                url: 'ManageReports!search.action',
                data: {
                    searchTerm: element.val()
                },
                success: function (data, textStatus, jqXHR) {
                    $('#search_reports_container').html(data);
                }
            });
        },
        
        unfavoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $favorite_icon = $report.find('.favorite-icon'),
                $icon = $report.find('.icon-star'),
                $body = $('body'),
                report_id = $element.attr('data-id');
            
            $body.trigger('report-unfavorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $icon.removeClass('selected');
                    $favorite_icon.toggleClass('favorite unfavorite');
                }
            });
            
            event.preventDefault();
        },
    }
});