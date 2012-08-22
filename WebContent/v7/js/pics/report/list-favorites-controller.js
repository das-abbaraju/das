PICS.define('report.ListFavoritesController', {
    methods: {
        init: function () {
            if ($('#ManageReports_favoritesList_page').length) {
                var that = this,
                    report_favorites_element = $('#report_favorites');
                
                $('.dropdown-toggle').dropdown();
                
                report_favorites_element.delegate('.delete', 'click', function (event) {
                    that.onDeleteClick.apply(that, [event]);
                });
                
                report_favorites_element.delegate('.favorite', 'click', function (event) {
                    that.onFavoriteClick.apply(that, [event]);
                });
                
                report_favorites_element.delegate('.move-down', 'click', function (event) {
                    that.onMoveDownClick.apply(that, [event]);
                });
                
                report_favorites_element.delegate('.move-up', 'click', function (event) {
                    that.onMoveUpClick.apply(that, [event]);
                });
                
                report_favorites_element.delegate('.remove', 'click', function (event) {
                    that.onRemoveClick.apply(that, [event]);
                });
            }
        },
        
        onDeleteClick: function (event) {
            var element = $(event.currentTarget),
                that = this;
            
            element.closest('.report').fadeOut(750, function () {
                PICS.ajax({
                    url: element.attr('href'),
                    success: function (data, textStatus, jqXHR) {
                        that.refreshFavoritesList();
                    }
                });
            });
            
            event.preventDefault();
        },
        
        onFavoriteClick: function (event) {
            var element = $(event.currentTarget),
                icon = element.find('.icon-star');
            
            if (icon.hasClass('selected')) {
                this.unfavorite(element, icon);
            }
            
            event.preventDefault();
        },
        
        onMoveDownClick: function (event) {
            var element = $(event.currentTarget),
                that = this;
            
            PICS.ajax({
                url: element.attr('href'),
                success: function (data, textStatus, jqXHR) {
                    that.refreshFavoritesList();
                }
            });
            
            event.preventDefault();
        },
        
        onMoveUpClick: function (event) {
            var element = $(event.currentTarget)
                that = this;
            
            PICS.ajax({
                url: element.attr('href'),
                success: function (data, textStatus, jqXHR) {
                    that.refreshFavoritesList();
                }
            });
            
            event.preventDefault();
        },
        
        onRemoveClick: function (event) {
            var element = $(event.currentTarget),
                that = this;
        
            element.closest('.report').fadeOut(750, function () {
                PICS.ajax({
                    url: element.attr('href'),
                    success: function (data, textStatus, jqXHR) {
                        that.refreshFavoritesList();
                    }
                });
            });
            
            event.preventDefault();
        },
        
        refreshFavoritesList: function () {
            PICS.ajax({
                url: 'ManageReports!favoritesList.action',
                success: function (data, textStatus, jqXHR) {
                    $('#report_favorites').html(data);
                }
            });
        },
        
        unfavorite: function (element, icon) {
            var body = $('body'),
                report_id = element.attr('data-id'),
                that = this;
            
            function success(data, textStatus, jqXHR) {
                element.closest('.report').fadeOut(750, function () {
                    that.refreshFavoritesList();
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