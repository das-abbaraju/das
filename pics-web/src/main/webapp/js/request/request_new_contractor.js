(function ($) {
    PICS.define('request.RequestNewContractor', {
        methods: {
            init: function () {
                var $element = $('.RequestNewContractorAccount-page');

                if ($element.length) {
                    $element.on('keyup', '#city', this.toggleAddressZip);
                    $element.on('change', '#country', this.loadCountrySubdivision);
                    $element.on('click', '#email_preview', this.previewEmail);
                    $element.on('change', '#operator_list', this.loadOperatorUsersAndTags);
                    $element.on('change', '#requesting_user', this.toggleOtherTextfield);
                    $element.on('keyup', '.check-matches', PICS.debounce(this.checkMatches, 250));
                    $element.on('change', '.popup-on-match', this.showMatchModal);
                    $element.on('click', '.contact-note-required', this.showContactNote);
                    $element.on('click', '.duplicated-show', this.showResolvedDuplicate);
                    $element.on('click', '.duplicated-hide', this.hideResolvedDuplicate);
                    
                    $('.datepicker').datepicker({
                        changeMonth : true,
                        changeYear : true,
                        yearRange : '1940:2039',
                        showOn : 'button',
                        buttonImage : 'images/icon_calendar.gif',
                        buttonImageOnly : true,
                        buttonText : translate('JS.ChooseADate'),
                        constrainInput : true,
                        showAnim : 'fadeIn',
                        minDate: 1
                    });
                }
            },
            
            checkMatches: function(event) {
                var classname = $(event.target).attr('data-class'),
                    messageDiv = $('.match-found.' + classname),
                    listDiv = $('.match-list.' + classname),
                    type = $(event.target).attr('data-type'),
                    term = $(event.target).val();
                
                messageDiv.html('<img src="images/ajax_process.gif" alt="' + translate('JS.Loading') + '" /> '
                        + translate('JS.RequestNewContractor.message.CheckingForMatches'));
                
                PICS.ajax({
                    url: 'RequestNewContractorSearch.action',
                    data: {
                        term: term,
                        type: type
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        if (data.indexOf("No matches") > -1) {
                            messageDiv.empty();
                            listDiv.empty();
                        } else {
                            listDiv.html(data);
                            
                            messageDiv.html('<a href="javascript:;">' 
                                    + translate('JS.RequestNewContractor.message.PossibleMatches') 
                                    + '</a>');
                            
                            messageDiv.delegate('a', 'click', function() {
                                var modal = PICS.modal({
                                    title: translate('JS.RequestNewContractor.message.PotentialMatches'),
                                    content: listDiv.html()
                                });
                                
                                modal.show();
                            });
                        }
                    }
                });
            },
            
            loadCountrySubdivision: function(event) {
                var isocode = $(event.target).val(),
                    selected_isocode = $('#country_subdivision').attr('data');

                if ($.trim(isocode)) {
                    PICS.ajax({
                        url: 'CountrySubdivisionListAjax.action',
                        data: {
                            countryString: isocode,
                            countrySubdivisionString: selected_isocode,
                            needsSuffix: false,
                            required: true,
                            prefix: 'contractor.'
                        },
                        success: function(data, textStatus, XMLHttpRequest) {
                            $('#country_subdivision').html(data);
                        }
                    });
                }
            },
            
            loadOperatorUsersAndTags: function(event) {
                var opID = $(event.target).val();
                
                PICS.ajax({
                   url: 'RequestingOperatorUserList.action',
                   data: {
                       'requestRelationship.operatorAccount': opID
                   },
                   success: function(data, textStatus, XMLHttpRequest) {
                       $('#user_list').html(data);
                   }
                });
                
                PICS.ajax({
                    url: 'RequestingOperatorTagList.action',
                    data: {
                        'requestRelationship.operatorAccount': opID
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        $('#tag_list').html(data);
                    }
                });
            },
            
            previewEmail: function(event) {
                var formData = $('#request_form').serialize();
                
                PICS.ajax({
                    url: 'RequestNewContractorAccount!emailPreview.action',
                    data: formData,
                    success: function(data, textStatus, XMLHttpRequest) {
                        var modal = PICS.modal({
                            width: 800,
                            height: 500,
                            title: translate('JS.button.Preview'),
                            content: data
                        });
                        
                        modal.show();
                    }
                });
            },

            showContactNote: function (event) {
                var declined = $(event.target).hasClass('negative'),
                    type = $(event.target).attr('data-type'),
                    placeholder = $(event.target).attr('data-placeholder');

                if (type) {
                    $('#contact_type').val(type);
                } else {
                    $('#contact_type').val('');
                }

                $('#contact_note').find('textarea').attr('placeholder', placeholder);
                $('#contact_note').removeClass('hide');

                $('#duplicated_contractor_id').addClass('hide');
                $('.duplicated_contractor_button').hide();
            },

            showResolvedDuplicate: function (event) {
                $('#duplicated_contractor_id').removeClass('hide');
                $('.duplicated_contractor_button').show();

                $('#contact_note').addClass('hide');
            },

            hideResolvedDuplicate: function (event) {
                $('#duplicated_contractor_id').addClass('hide');
                $('.duplicated_contractor_button').hide();
            },

            showMatchModal: function (event) {
                var classname = $(event.target).attr('data-class'),
                    listDiv = $('.match-list.' + classname),
                    type = $(event.target).attr('data-type'),
                    term = $(event.target).val();

                PICS.ajax({
                    url: 'RequestNewContractorSearch.action',
                    data: {
                        term: term,
                        type: type
                    },
                    success: function(data, textStatus, XMLHttpRequest) {
                        if (data.indexOf("No matches") > -1) {
                            messageDiv.empty();
                            listDiv.empty();
                        } else {
                            listDiv.html(data);
                            
                            var modal = PICS.modal({
                                title: translate('JS.RequestNewContractor.message.PotentialMatches'),
                                content: listDiv.html()
                            });
                            
                            $('input').trigger('blur');
                            modal.show();
                        }
                    }
                });
            },
            
            toggleAddressZip: function(event) {
                var city = $(event.target).val();
                
                if (city) {
                    $('.address-zip').show();
                } else {
                    $('.address-zip').hide();
                }
            },
            
            toggleOtherTextfield: function(event) {
                if ($(event.target).val() > 0) {
                    $('#requesting_other').hide();
                } else {
                    $('#requesting_other').show();
                }
            }
        }
    });
})(jQuery);