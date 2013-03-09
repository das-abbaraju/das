PICS.define('report.manage-report.FavoritesController', {
    methods: {
        init: function () {
            if ($('#ManageReports_favorites_page').length > 0) {
                var that = this,
                    favorite_reports = $('#favorite_reports_container');
                
                $('.dropdown-toggle').dropdown();
                
                favorite_reports.on('click', '.delete', function (event) {
                    that.onDeleteClick.apply(that, [event]);
                });
                
                favorite_reports.on('click', '.favorite', function (event) {
                    that.onFavoriteClick.apply(that, [event]);
                });
                
                favorite_reports.on('click', '.move-down', function (event) {
                    that.onMoveDownClick.apply(that, [event]);
                });
                
                favorite_reports.on('click', '.move-up', function (event) {
                    that.onMoveUpClick.apply(that, [event]);
                });
                
                favorite_reports.on('click', '.remove', function (event) {
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
                    success: that.refreshFavorites
                });
            });
            
            event.preventDefault();
        },
        
        onFavoriteClick: function (event) {
            var element = $(event.currentTarget),
                icon = element.find('.icon-star');
            
            if (icon.hasClass('selected')) {
                this.unfavoriteReport(element, icon);
            }
            
            event.preventDefault();
        },
        
        onMoveDownClick: function (event) {
            var element = $(event.currentTarget),
                that = this;
            
            PICS.ajax({
                url: element.attr('href'),
                success: that.refreshFavorites
            });
            
            event.preventDefault();
        },
        
        onMoveUpClick: function (event) {
            var element = $(event.currentTarget),
                that = this;
            
            PICS.ajax({
                url: element.attr('href'),
                success: that.refreshFavorites
            });
            
            event.preventDefault();
        },
        
        onRemoveClick: function (event) {
            var element = $(event.currentTarget),
                that = this;
        
            element.closest('.report').fadeOut(750, function () {
                PICS.ajax({
                    url: element.attr('href'),
                    success: that.refreshFavorites
                });
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
        },
        
        unfavoriteReport: function (element, icon) {
            var body = $('body'),
                report_id = element.attr('data-id'),
                that = this;
            
            function success(data, textStatus, jqXHR) {
                element.closest('.report').fadeOut(750, that.refreshFavorites);
            }
            
            var params = [
                report_id,
                success
            ];
            
            body.trigger('report-unfavorite', params);
        }
    }
});