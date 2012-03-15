(function ($) {
    PICS.define('operator.Edit', {
        methods: {
            init: function () {
                $('#FacilitiesEdit-page').bind('#opCountry', this.countryChanged);
                $('#FacilitiesEdit-page').delegate('#general_contractor_checkbox', 'click', this.toggleLinkedAccountField);
                $('#FacilitiesEdit-page').delegate('#opCountry', 'change', this.updateCountry);
                $('#FacilitiesEdit-page').delegate('#save_autoApproveRelationships', 'change', this.showAutoApproveRelationshipModal);
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
            
                var element = $(this);
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
                
                /**
                 * Save Operator
                 * 
                 * @operator_id
                 */
                function saveOperator(operator_id) {
                    PICS.ajax({
                        url: 'FacilitiesEdit!save.action',
                        data: {
                            operator: operator_id
                        },
                        success: function (data, textStatus, XMLHttpRequest) {
                            var modal = PICS.getClass('modal.Modal');
                            modal.hide();
                        }
                    });
                }
                
                var element = $(this);
                var operator_id = $('#save_operator').val();
                
                if (element.is(':checked')) {
                    // fetch content for modal
                    PICS.ajax({
                        url: 'FacilitiesEdit!ajaxAutoApproveRelationshipModal.action',
                        data: {
                            operator: operator_id
                        },
                        success: function (data, textStatus, XMLHttpRequest) {
                            var modal = createModal(data);
                            modal.show();
                            
                            // bind approve save event
                            $('.operator-approve-relationship-modal').delegate('.approve-relationship', 'click', function (event) {
                                saveOperator(operator_id);
                            });
                            
                            // bind cancel event
                            $('.operator-approve-relationship-modal').delegate('.cancel-relationship', 'click', function (event) {
                                var modal = PICS.getClass('modal.Modal');
                                modal.hide();
                            });
                        }
                    });
                }
            },
            
            toggleLinkedAccountField: function(event) {
                $('#FacilitiesEdit-page #linked_contractor').toggle();
            }
        }
    });
})(jQuery);