PICS.define('report.manage-report.FavoritesController', {
    methods: {
        init: function () {
            if ($('#ManageReports_favorites_page').length > 0) {
                $('#favorite_reports_container')
                    .on('click', '.report > .favorite', $.proxy(this.unfavoriteReport, this))
                    .on('click', '.report-options .unfavorite', $.proxy(this.unfavoriteReport, this))
                    .on('click', '.report-options .pin', $.proxy(this.pinReport, this))
                    .on('click', '.report-options .unpin', $.proxy(this.unpinReport, this))
                    .on('click', '.report-options .move-down', $.proxy(this.moveReportDown, this))
                    .on('click', '.report-options .move-up', $.proxy(this.moveReportUp, this));
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
            
            event.preventDefault();
        },
        
        pinReport: function (event) {
            var $element = $(event.currentTarget),
                report_id = $element.data('report-id'),
                pinned_index = $element.data('pinned-index');
            
            PICS.ajax({
                url: 'ManageReports!pinFavorite.action',
                data: {
                    reportId: report_id,
                    pinnedIndex: pinned_index
                },
                success: this.refreshFavorites
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
        
        unfavoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $body = $('body'),
                report_id = $element.data('report-id');
            
            $body.trigger('report-unfavorite', {
                report_id: report_id,
                success: $.proxy(function (data, textStatus, jqXHR) {
                    $element.closest('.report').slideUp(400, this.refreshFavorites);
                }, this)
            });
            
            event.preventDefault();
        },
        
        unpinReport: function (event) {
            var $element = $(event.currentTarget),
                report_id = $element.data('report-id'),
                pinned_index = $element.data('pinned-index');
            
            PICS.ajax({
                url: 'ManageReports!unpinFavorite.action',
                data: {
                    reportId: report_id,
                    pinnedIndex: pinned_index
                },
                success: this.refreshFavorites
            });
            
            event.preventDefault();
        }
    }
});