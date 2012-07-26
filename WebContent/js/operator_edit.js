(function ($) {
    PICS.define('operator.Edit', {
        methods: {
            init: function () {
                if ($('#FacilitiesEdit-page').length) {
                    $('#FacilitiesEdit-page').delegate('#general_contractor_checkbox', 'click', this.toggleLinkedAccountField);
                    $('#FacilitiesEdit-page').delegate('#opCountry', 'change', this.updateCountry);
                    $('#FacilitiesEdit-page').delegate('#save_autoApproveRelationships', 'change', this.showAutoApproveRelationshipModal);

                    //autofill state list
                    if ($('#opCountry').length) {
                        this.updateCountry();
                    }
                }
            },

            updateCountry: function (event) {
                function updateState(country, state) {
                    PICS.ajax({
                        url: "StateListAjax.action",
                        data: {
                            countryString: country,
                            stateString: state
                        },
                        success: function(data, textStatus, XMLHttpRequest) {
                            $('#FacilitiesEdit-page #state_li').html(data);
                        }
                    });
                }

                var element = $('#opCountry') || $(this);
                var country = element.val();
                var state = $('#FacilitiesEdit-page #operatorState').val();

                if (country == 'AE') {
                    $('#zip_li').hide();
                } else {
                    $('#zip_li').show();
                }

                updateState(country, state);
            },

            // show modal to confirm auto approve relationship on facility edit
            showAutoApproveRelationshipModal: function (event) {
                /**
                 * Create Modal
                 *
                 * @data:
                 * @return:
                 */
                function createModal(data) {
                    var modal = PICS.modal({
                        title: 'Auto Approve Contractors',
                        modal_class: 'modal operator-approve-relationship-modal',
                        content: data,
                        buttons: [{
                            html: [
                                '<a href="javascript:;" class="btn cancel-relationship">Cancel</a>',
                                '<a href="javascript:;" class="btn success approve-relationship">Approve</a>'
                            ].join('')
                        }]
                    });

                    return modal;
                }

                var element = $(this);
                var operator_id = $('#save_operator').val();
                var number_pending_not_approved = $('#number_pending_not_approved').val();

                if (element.is(':checked') && number_pending_not_approved > 0) {
                    element.attr('disabled', true);

                    // fetch content for modal
                    PICS.ajax({
                        url: 'FacilitiesEdit!ajaxAutoApproveRelationshipModal.action',
                        data: {
                            operator: operator_id
                        },
                        success: function (data, textStatus, XMLHttpRequest) {
                            element.attr('disabled', false);

                            var modal = createModal(data);
                            modal.show();

                            // bind approve save event
                            $('.operator-approve-relationship-modal').delegate('.approve-relationship', 'click', function (event) {
                                var modal = PICS.getClass('modal.Modal');
                                modal.hide();
                            });

                            // bind cancel event
                            $('.operator-approve-relationship-modal').delegate('.cancel-relationship', 'click', function (event) {
                                element.attr('checked', false);

                                var modal = PICS.getClass('modal.Modal');
                                modal.hide();
                            });
                        }
                    });
                }
            },

            toggleLinkedAccountField: function(event) {
                $('#FacilitiesEdit-page #linked_clients').toggle();
            }
        }
    });
})(jQuery);