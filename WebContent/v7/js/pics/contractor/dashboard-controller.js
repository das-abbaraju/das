(function ($) {
    PICS.define('contractor.DashboardController', {
        methods: {
            init: function () {
                if ($('#ContractorView__page').length > 0) {
                    var that = this;

                    $('#contractor_dashboard').delegate('.contractor-status-buttons button', 'click', function (event) {
                        that.saveContractorStatus.apply(that, [event]);
                    });
                }
            },

            saveContractorStatus: function (event) {
                var that = this,
                    element = $(event.currentTarget),
                    contractor_id = element.attr('data-conid'),
                    contractor_status = element.attr('data-constatus');

                PICS.ajax({
                    url: 'ContractorApprovalAjax!save.action',
                    data: {
                        conids: contractor_id,
                        workStatus: contractor_status
                    },
                    success: function (data, textStatus, jqXHR) {
                        var message = element.closest('.alert'),
                            operator_id = element.attr('data-opid');

                        that.updateContractorStatusDisplay(message, contractor_status, contractor_id, operator_id);
                    }
                });
            },

            updateContractorStatusDisplay: function (message, contractor_status, contractor_id, operator_id) {

                function getContractorNotApprovedMessage() {
                    PICS.ajax({
                        url: 'ContractorView!getContractorAndOperatorNames.action',
                        data: {
                            id: contractor_id,
                            opId: operator_id
                        },
                        dataType: 'json',
                        success: function (data, textStatus, jqXHR) {
                            if (data.result === "success") {
                                var not_approved_message =
                                    "<p>" +
                                        data.contractorName +
                                        " " + translate("JS.ContractorView.ContractorDashboard.NotApproved") + " " +
                                        data.operatorName + "." +
                                    "</p>";

                                message.html(not_approved_message);
                            }
                        }
                    });
                }

                if (contractor_status === 'Y') {
                    message.hide('blind', 1000);
                } else {
                    getContractorNotApprovedMessage();
                }
            }
        }
    });

})(jQuery);