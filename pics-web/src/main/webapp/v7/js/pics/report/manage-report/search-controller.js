PICS.define('report.manage-report.SearchController', {
    methods: {
        init: function () {
            if ($('#ManageReports_search_page').length > 0) {
                $('#report_search_form input[type=text]')
                    .on('keyup', PICS.debounce(this.search, 250))
                    .focus();
                
                $('#search_reports_container')
                    .on('click', '.report > .favorite', this.unfavoriteReport)
                    .on('click', '.report > .unfavorite', this.favoriteReport);
            }
        },
        
        favoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $favorite_star = $report.children('.favorite, .unfavorite'),
                $favorite_icon = $favorite_star.find('.icon-star'),
                $body = $('body'),
                report_id = $element.data('report-id');
            
            $body.trigger('report-favorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $favorite_star.toggleClass('favorite unfavorite');
                    $favorite_icon.addClass('selected');
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
                $favorite_star = $report.children('.favorite, .unfavorite'),
                $favorite_icon = $favorite_star.find('.icon-star'),
                $body = $('body'),
                report_id = $element.data('report-id');
            
            $body.trigger('report-unfavorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $favorite_star.toggleClass('favorite unfavorite');
                    $favorite_icon.removeClass('selected');
                }
            });
            
            event.preventDefault();
        },
    }
});