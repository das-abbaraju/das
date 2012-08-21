PICS.define('report.ListFavoritesController', {
    methods: {
        init: function () {
            if ($('#ManageReports_favoritesList_page').length) {
                var that = this;
                
                $('.dropdown-toggle').dropdown();
                
                $('.favorite').on('click', function (event) {
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
                element.closest('li').remove();
            }
            
            var params = [
                report_id,
                success
            ];
            
            body.trigger('report-unfavorite', params);
        }
    }
});