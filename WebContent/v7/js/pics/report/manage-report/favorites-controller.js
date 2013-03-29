PICS.define('report.manage-report.FavoritesController', {
    methods: {
        init: function () {
            if ($('#ManageReports_favorites_page').length > 0) {
                $('#favorite_reports_container')
                    .on('click', '.favorite-icon.favorite', $.proxy(this.unfavoriteReport, this))
                    .on('click', '.favorite-action.unfavorite', $.proxy(this.unfavoriteReport, this))
                    .on('click', '.move-down', $.proxy(this.moveReportDown, this))
                    .on('click', '.move-up', $.proxy(this.moveReportUp, this));
            }
        },
        
        moveReportUp: function (event) {
            var element = $(event.currentTarget);
            
            PICS.ajax({
                url: element.attr('href'),
                success: $.proxy(this.refreshFavorites, this)
            });
            
            event.preventDefault();
        },
        
        moveReportDown: function (event) {
            var element = $(event.currentTarget);
            
            PICS.ajax({
                url: element.attr('href'),
                success: $.proxy(this.refreshFavorites, this)
            });
            
            event.preventDefault();
        },
        
        unfavoriteReport: function (event) {
            var element = $(event.currentTarget),
                body = $('body'),
                report_id = element.attr('data-id');
            
            function success(data, textStatus, jqXHR) {
                element.closest('.report').fadeOut(750, this.refreshFavorites);
            }
            
            var params = [
                report_id,
                $.proxy(success, this)
            ];
            
            body.trigger('report-unfavorite', params);
            
            event.preventDefault();
        },
        
        refreshFavorites: function () {
            PICS.ajax({
                url: 'ManageReports!favorites.action',
                success: function (data, textStatus, jqXHR) {
                    $('#favorite_reports_container').html(data);
                }
            });
        }
    }
});