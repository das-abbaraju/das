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
            var $element = $(event.currentTarget);
            
            PICS.ajax({
                url: $element.attr('href'),
                success: this.refreshFavorites
            });
            
            event.preventDefault();
        },
        
        moveReportDown: function (event) {
            var $element = $(event.currentTarget);
            
            event.preventDefault();
            
            PICS.ajax({
                url: $element.attr('href'),
                success: this.refreshFavorites
            });
            
        },
        
        unfavoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $body = $('body'),
                report_id = $element.attr('data-id');
            
            $body.trigger('report-unfavorite', {
                report_id: report_id,
                success: $.proxy(function (data, textStatus, jqXHR) {
                    $element.closest('.report').slideUp(400, this.refreshFavorites);
                }, this)
            });
            
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