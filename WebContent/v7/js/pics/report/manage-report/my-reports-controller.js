PICS.define('report.manage-report.MyReportsController', {
    methods: {
        init: function () {
            if ($('#ManageReports_myReports_page').length > 0) {
                var that = this,
                    filters = $('#manage_report_filter'),
                    my_reports_container = $('#my_reports_container');
                
                $('.dropdown-toggle').dropdown();
                
                filters.on('click', 'a', function (event) {
                    that.onMyReportsFilterClick.apply(that, [event]);
                });
                
                my_reports_container.on('click', '.delete', function (event) {
                    that.onDeleteClick.apply(that, [event]);
                });
                
                my_reports_container.on('click', '.favorite', function (event) {
                    that.onFavoriteClick.apply(that, [event]);
                });
                
                my_reports_container.on('click', '.remove', function (event) {
                    that.onRemoveClick.apply(that, [event]);
                });
            }
        },
        
        favoriteReport: function (element, icon) {
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
        
        onDeleteClick: function (event) {
            var element = $(event.currentTarget),
                that = this;
            
            element.closest('.report').fadeOut(750, function () {
                PICS.ajax({
                    url: element.attr('href'),
                    success: that.refreshReports
                });
            });
            
            event.preventDefault();
        },
        
        onFavoriteClick: function (event) {
            var element = $(event.currentTarget),
                icon = element.find('.icon-star');
            
            if (icon.hasClass('selected')) {
                this.unfavoriteReport(element, icon);
            } else {
                this.favoriteReport(element, icon);
            }
            
            event.preventDefault();
        },
        
        onMyReportsFilterClick: function (event) {
            var filter = $(event.currentTarget);
            
            PICS.ajax({
                url: filter.attr('href'),
                success: function (data, textStatus, jqXHR) {
                    $('#my_reports_container').html(data);
                }
            });
            
            this.resetFilters(filter);
            
            this.selectFilter(filter); 
            
            event.preventDefault();
        },
        
        onRemoveClick: function (event) {
            var element = $(event.currentTarget),
                that = this;
            
            element.closest('.report').fadeOut(750, function () {
                PICS.ajax({
                    url: element.attr('href'),
                    success: that.refreshReports
                });
            });
            
            event.preventDefault();
        },
        
        refreshReports: function () {
            PICS.ajax({
                url: 'ManageReports!myReports.action',
                success: function (data, textStatus, jqXHR) {
                    $('#my_reports_container').html(data);
                }
            });
        },
        
        resetFilters: function (filter) {
            var filters = filter.siblings();
            
            filters.each(function (index) {
                var filter = $(this),
                    params = PICS.getRequestParameters(filter[0].search);
                
                params.direction = 'ASC';
                
                filter[0].search = $.param(params);
            });
            
            filters.removeClass('active');
        },
        
        selectFilter: function (filter) {
            var params = PICS.getRequestParameters(filter[0].search);
            
            params.direction = params.direction == 'DESC' ? 'ASC' : 'DESC'
            
            filter[0].search = $.param(params);
            
            filter.addClass('active');
        },
        
        unfavoriteReport: function (element, icon) {
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