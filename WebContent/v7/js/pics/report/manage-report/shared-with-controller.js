PICS.define('report.manage-report.SharedWithController', {
    methods: {
        init: function () {
            if ($('#ManageReports_sharedWith_page').length > 0) {
                $('#shared_with_reports_container')
                    .on('click', '.favorite-icon.favorite', $.proxy(this.unfavoriteReport, this))
                    .on('click', '.favorite-icon.unfavorite', $.proxy(this.favoriteReport, this))
                    .on('click', '.favorite-action.favorite', $.proxy(this.favoriteReport, this))
                    .on('click', '.favorite-action.unfavorite', $.proxy(this.unfavoriteReport, this));
            }
        },
        
        favoriteReport: function (event) {
            var element = $(event.currentTarget),
                report = element.closest('.report'),
                favorite_icon = report.find('.favorite-icon'),
                favorite_action = report.find('.favorite-action'),
                icon = report.find('.icon-star'),
                body = $('body'),
                report_id = element.attr('data-id');
            
            function success(data, textStatus, jqXHR) {
                favorite_icon.toggleClass('favorite unfavorite');
                favorite_action.toggleClass('favorite unfavorite');
                
                icon.addClass('selected');
                
                favorite_action.text('Unfavorite');
            }
            
            var params = [
                report_id,
                success
            ];
            
            body.trigger('report-favorite', params);
            
            event.preventDefault();
        },
        
        unfavoriteReport: function (event) {
            var element = $(event.currentTarget),
                report = element.closest('.report'),
                favorite_icon = report.find('.favorite-icon'),
                favorite_action = report.find('.favorite-action'),
                icon = report.find('.icon-star'),
                body = $('body'),
                report_id = element.attr('data-id');
            
            function success(data, textStatus, jqXHR) {
                favorite_icon.toggleClass('favorite unfavorite');
                favorite_action.toggleClass('favorite unfavorite');
                
                icon.removeClass('selected');
                
                favorite_action.text('Favorite');
            }
            
            var params = [
                report_id,
                success
            ];
            
            body.trigger('report-unfavorite', params);
            
            event.preventDefault();
        }
    }
});