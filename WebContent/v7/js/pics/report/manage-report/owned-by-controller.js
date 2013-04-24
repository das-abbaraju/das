PICS.define('report.manage-report.OwnedByController', {
    methods: {
        init: function () {
            if ($('#ManageReports_ownedBy_page').length > 0) {
                $('#owned_by_reports_container')
                    .on('click', '.report > .favorite', this.unfavoriteReport)
                    .on('click', '.report > .unfavorite', this.favoriteReport)
                    .on('click', '.report-options .favorite', this.favoriteReport)
                    .on('click', '.report-options .unfavorite', this.unfavoriteReport)
                    .on('click', '.report-options .private', this.privateReport)
                    .on('click', '.report-options .public', this.publicReport)
                    .on('click', '.report-options .delete', this.deleteReport)
                    .on('click', '.confirm-options .delete', $.proxy(this.confirmDeleteReport, this))
                    .on('click', '.confirm-options .cancel', $.proxy(this.cancelChanges, this))
                    .on('click', '#manage_report_filter a', this.filterList);
            }
        },
        
        cancelChanges: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report');
            
            this.resetReport($report);
            
            event.preventDefault();
        },
        
        confirmDeleteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                report_id = $element.data('report-id');
            
            PICS.ajax({
                url: 'ManageReports!deleteReport.action',
                data: {
                    reportId: report_id,
                }
            });

            $report.slideUp(400, function () {
                $(this).remove();
            });
            
            event.preventDefault();
        },
        
        deleteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $report_options = $report.find('.report-options'),
                $report_icons = $report.find('.icons'),
                report_id = $element.data('report-id');
            
            $report.addClass('delete');
            
            $report_options.hide();
            
            $report_icons.hide();
                    
            $report.append($([
                '<div class="confirm-options btn-group pull-right">',
                    '<button class="btn cancel">Cancel</button>',
                    '<button class="btn btn-danger delete" data-report-id="' + report_id + '">Delete</button>',
                '</div>'
            ].join('')));
            
            event.preventDefault();
        },
        
        favoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $report_options = $report.find('.report-options'),
                $favorite_star = $report.children('.favorite, .unfavorite'),
                $favorite_icon = $favorite_star.find('.icon-star'),
                $favorite_action = $report_options.find('.favorite, .unfavorite'),
                $body = $('body'),
                report_id = $element.data('report-id');
            
            $body.trigger('report-favorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $favorite_star.toggleClass('favorite unfavorite');
                    $favorite_icon.addClass('selected');
                    $favorite_action.toggleClass('favorite unfavorite');
                    $favorite_action.text('Unfavorite');
                }
            });
            
            event.preventDefault();
        },
        
        filterList: function (event) {
            var $filter = $(event.currentTarget);
            
            $('body').trigger('report-filter', {
                filter: $filter,
                success: function (data, textStatus, jqXHR) {
                    $('#owned_by_reports_container').html(data);
                }
            });
            
            event.preventDefault();
        },
        
        privateReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $report_options = $report.find('.report-options'),
                $public_icon = $report.find('.icon-search'),
                $public_action = $report_options.find('.private, .public'),
                report_id = $element.data('report-id');
            
            PICS.ajax({
                url: 'ManageReports!unpublicize.action',
                data: {
                    reportId: report_id
                },
                success: function (data, textStatus, jqXHR) {
                    $public_icon.remove();
                    $public_action.toggleClass('public private');
                    $public_action.text('Show in Search');
                }
            });
            
            event.preventDefault();
        },
        
        publicReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $report_options = $report.find('.report-options'),
                $icons = $report.find('.icons'),
                $public_action = $report_options.find('.private, .public'),
                report_id = $element.data('report-id');
            
            PICS.ajax({
                url: 'ManageReports!publicize.action',
                data: {
                    reportId: report_id
                },
                success: function (data, textStatus, jqXHR) {
                    $icons.append($('<i class="icon-search"></i>'));
                    $public_action.toggleClass('public private');
                    $public_action.text('Hide from Search');
                }
            });
            
            event.preventDefault();
        },
        
        refreshOwnedBy: function () {
            PICS.ajax({
                url: 'ManageReports!ownedBy.action',
                success: function (data, textStatus, jqXHR) {
                    $('#owned_by_reports_container').html(data);
                }
            });
        },
        
        resetReport: function ($report) {
            var $confirm_options = $report.find('.confirm-options'),
                $report_options = $report.find('.report-options'),
                $report_icons = $report.find('.icons');
            
            $report.removeClass('delete');
            
            $confirm_options.remove();
            
            $report_options.show();
            
            $report_icons.show();
        },
        
        unfavoriteReport: function (event) {
            var $element = $(event.currentTarget),
                $report = $element.closest('.report'),
                $report_options = $report.find('.report-options'),
                $favorite_star = $report.children('.favorite, .unfavorite'),
                $favorite_icon = $favorite_star.find('.icon-star'),
                $favorite_action = $report_options.find('.favorite, .unfavorite'),
                $body = $('body'),
                report_id = $element.data('report-id');
            
            $body.trigger('report-unfavorite', {
                report_id: report_id,
                success: function (data, textStatus, jqXHR) {
                    $favorite_star.toggleClass('favorite unfavorite');
                    $favorite_icon.removeClass('selected');
                    $favorite_action.toggleClass('favorite unfavorite');
                    $favorite_action.text('Favorite');
                }
            });
            
            event.preventDefault();
        }
    }
});