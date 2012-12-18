(function ($) {
    PICS.define('request.RequestNewContractor', {
        methods: {
            init: function () {
                var element = $('.RequestNewContractorAccount-page');

                if (element.length) {
                    var that = this;
                    
                    element.delegate('#city', 'keyup', this.toggleAddressZip);
                    element.delegate('#country', 'change', this.loadCountrySubdivision);
                    element.delegate('#email_preview', 'click', this.previewEmail);
                    element.delegate('#operator_list', 'change', this.loadOperatorUsersAndTags);
                    element.delegate('#requesting_user', 'change', this.toggleOtherTextfield);
                    element.delegate('.check-matches', 'keyup', PICS.debounce(that.checkMatches, 250));
                    element.delegate('.popup-on-match', 'change', this.showMatchModal);
                    element.delegate('.contact-note-required', 'click', this.showContactNote);
                    
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
                    
                    if (!$('#country_subdivision').html().trim()) {
                        $('#country').trigger('change');
                    }
                    
                    if ($('#user_list').length && $('#user_list').html().trim()) {
                        $('#requesting_user').trigger('change');
                    }
                    
                    $('#city').trigger('keyup');
                    $('#request_status').trigger('change');
                }
            },
            
            checkMatches: function(event) {
                var classname = $(this).attr('data-class');
                
                var messageDiv = $('.match-found.' + classname);
                var listDiv = $('.match-list.' + classname);
                var type = $(this).attr('data-type');
                var term = $(this).val();
                
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
                var isocode = $(this).val();
                
                if (isocode.trim()) {
                    PICS.ajax({
                        url: 'CountrySubdivisionListAjax.action',
                        data: {
                            countryString: isocode,
                            countrySubdivisionString: isocode,
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
                var opID = $(this).val();
                
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
                            title: translate('JS.button.Preview'),
                            content: data
                        });
                        
                        modal.show();
                    }
                });
            },

            showContactNote: function (event) {
                var declined = $(this).hasClass('negative');
                var type = $(this).attr('data-type');
                var placeholder = $(this).attr('data-placeholder');

                if (type) {
                    $('#contact_type').val(type);
                } else {
                    $('#contact_type').val('');
                }

                if (declined) {
                    $('#declined_value').val('true');
                } else {
                    $('#declined_value').val('false');
                }

                $('#contact_note').find('textarea').attr('placeholder', placeholder);
                $('#contact_note').removeClass('hide');
            },
            
            showMatchModal: function (event) {
                var classname = $(this).attr('data-class');
                var listDiv = $('.match-list.' + classname);
                var type = $(this).attr('data-type');
                var term = $(this).val();
                
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
                var city = $(this).val();
                
                if (city) {
                    $('.address-zip').show();
                } else {
                    $('.address-zip').hide();
                }
            },
            
            toggleOtherTextfield: function(event) {
                if ($(this).val() > 0) {
                    $('#requesting_other').hide();
                } else {
                    $('#requesting_other').show();
                }
            }
        }
    });
})(jQuery);