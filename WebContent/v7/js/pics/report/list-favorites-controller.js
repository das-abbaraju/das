PICS.define('report.ListFavoritesController', {
    methods: {
        init: function () {
            if ($('#ManageReports_favoritesList_page').length) {
                var that = this;
                
                $('.dropdown-toggle').dropdown();
                
                $('#report_favorites').delegate('.favorite', 'click', function (event) {
                    that.onFavoriteClick.apply(that, [event]);
                });
            }
        },
        
        onFavoriteClick: function (event) {
            var element = $(event.currentTarget),
                icon = element.find('.icon-star');
            
            if (icon.hasClass('selected')) {
                this.unfavorite(element, icon);
            }
            
            event.preventDefault();
        },
        
        unfavorite: function (element, icon) {
            var body = $('body'),
                report_id = element.attr('data-id');
            
            function success(data, textStatus, jqXHR) {
                element.closest('li').fadeOut(750, function () {
                    PICS.ajax({
                        url: 'ManageReports!favoritesList.action',
                        success: function (data, textStatus, jqXHR) {
                            $('#report_favorites').replaceWith(data);
                        }
                    });
                });
            }
            
            var params = [
                report_id,
                success
            ];
            
            body.trigger('report-unfavorite', params);
        }
    }
});