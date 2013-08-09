(function ($) {
    PICS.define('insureguard.ReportInsuranceApprovalController', {
        methods: {
            init: function () {
                if ($('#ReportInsuranceApproval-page').length) {
                    var that = this;

                    var form = $('#approveInsuranceForm');

                    form.delegate('.insurance-approval-report .actions .policy-approve', 'click', function (event) {
                        var element = $(this);
                        var row = element.closest('tr');
                        var cao_id = row.attr('data-cao-id');

                        element.trigger('approve', [cao_id, function () {
                            // disable policy row
                            row.addClass('disable');

                            // toggle action column
                            that.toggleInsuranceApprovalActions(row);

                        }]);
                    });

                    form.delegate('.insurance-approval-report .actions .policy-na', 'click', function (event) {
                        var element = $(this);
                        var row = element.closest('tr');
                        var cao_id = row.attr('data-cao-id');

                        element.trigger('na', [cao_id, function () {
                            // disable policy row
                            row.addClass('disable');

                            // toggle action column
                            that.toggleInsuranceApprovalActions(row);
                        }]);
                    });

                    form.delegate('.insurance-approval-report .actions .policy-reject', 'click', function (event) {
                        var element = $(this);
                        var row = element.closest('tr');
                        var cao_id = row.attr('data-cao-id');

                        element.trigger('reject', [cao_id, function () {
                            // close modal window
                            var modal = PICS.getClass('widget.Modal');
                            modal.hide();

                            // disable policy row
                            row.addClass('disable');

                            // toggle action column
                            that.toggleInsuranceApprovalActions(row);
                        }]);
                    });

                    form.delegate('.insurance-approval-report .actions .policy-revert:not(.disabled)', 'click', function (event) {
                        var element = $(this);
                        var row = element.closest('tr');
                        var cao_id = row.attr('data-cao-id');

                        element.trigger('revert', [cao_id, function () {
                            // enable policy row
                            row.removeClass('disable');

                            // toggle action column
                            that.toggleInsuranceApprovalActions(row);
                        }]);
                    });
                }
            },

            // toggle action column between approve + reject + na and undo
            toggleInsuranceApprovalActions: function (row) {
                var change_policy = $('.actions .change-policy', row);
                var revert_policy = $('.actions .revert-policy', row);
                var revert_policy_link = revert_policy.find('a');

                if (change_policy.is(':visible')) {
                    change_policy.hide();
                    revert_policy.show();
                    revert_policy_link.addClass('disabled');

                    setTimeout(function () {
                        revert_policy_link.removeClass('disabled');
                    }, 1000);
                } else {
                    change_policy.show();
                    revert_policy.hide();
                    revert_policy_link.addClass('disabled');
                }
            }
        }
    });
})(jQuery);