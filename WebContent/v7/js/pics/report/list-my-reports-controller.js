PICS.define('report.ListMyReportsController', {
    methods: {
        init: function () {
            if ($('#ManageReports_myReportsList_page').length) {
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
            } else {
                this.favorite(element, icon);
            }
            
            event.preventDefault();
        },
        
        favorite: function (element, icon) {
            var body = $('body'),
                report_id = element.attr('data-id');
            
            function success(data, textStatus, jqXHR) {
                icon.addClass('selected');
            }
            
            var params = [
                report_id,
                success
            ];
            
            body.trigger('report-favorite', params);
        },
        
        unfavorite: function (element, icon) {
            var body = $('body'),
                report_id = element.attr('data-id');
            
            function success(data, textStatus, jqXHR) {
                icon.removeClass('selected');
            }
            
            var params = [
                report_id,
                success
            ];
            
            body.trigger('report-unfavorite', params);
        }
    }
});