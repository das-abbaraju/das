PICS.define('report.ListMyReportsController', {
    methods: {
        init: function () {
            if ($('#ManageReports_myReportsList_page').length) {
                var that = this,
                    my_reports_filter_element = $('#my_reports_filter'),
                    report_my_reports_element = $('#report_my_reports');
                
                $('.dropdown-toggle').dropdown();
                
                my_reports_filter_element.delegate('a', 'click', function (event) {
                    that.onMyReportsFilterClick.apply(that, [event]);
                });
                
                report_my_reports_element.delegate('.delete', 'click', function (event) {
                    that.onDeleteClick.apply(that, [event]);
                });
                
                report_my_reports_element.delegate('.favorite', 'click', function (event) {
                    that.onFavoriteClick.apply(that, [event]);
                });
                
                report_my_reports_element.delegate('.remove', 'click', function (event) {
                    that.onRemoveClick.apply(that, [event]);
                });
            }
        },
        
        onDeleteClick: function (event) {
            var element = $(event.currentTarget)
                that = this;
            
            element.closest('.report').fadeOut(750, function () {
                PICS.ajax({
                    url: element.attr('href'),
                    success: function (data, textStatus, jqXHR) {
                        that.refreshMyReportsList();
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
            } else {
                this.favorite(element, icon);
            }
            
            event.preventDefault();
        },
        
        onMyReportsFilterClick: function (event) {
            var element = $(event.currentTarget),
                dom_element = element[0],
                path_name = dom_element.pathname,
                query_string = PICS.getRequestParameters(dom_element.search),
                siblings = element.siblings(),
                that = this;
            
            PICS.ajax({
                url: element.attr('href'),
                success: function (data, textStatus, jqXHR) {
                    $('#report_my_reports').html(data);
                }
            });
            
            siblings.removeClass('active');
            
            element.addClass('active');
            element.attr('href', [
                path_name,
                '?',
                $.param({
                    sort: query_string.sort,
                    direction: query_string.direction == 'desc' ? 'asc' : 'desc'
                })
            ].join(''));
            
            event.preventDefault();
        },
        
        onRemoveClick: function (event) {
            var element = $(event.currentTarget),
                that = this;
            
            element.closest('.report').fadeOut(750, function () {
                PICS.ajax({
                    url: element.attr('href'),
                    success: function (data, textStatus, jqXHR) {
                        that.refreshMyReportsList();
                    }
                });
            });
            
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
        
        refreshMyReportsList: function () {
            PICS.ajax({
                url: 'ManageReports!myReportsList.action',
                success: function (data, textStatus, jqXHR) {
                    $('#report_my_reports').html(data);
                }
            });
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