(function ($) {
    PICS.define('request.RequestNewContractor', {
        methods: {
            init: function () {
                var element = $('.RequestNewContractorAccount-page');

                if (element.length) {
                    element.delegate('#city', 'keyup', this.toggleAddressZip);
                    element.delegate('#country', 'change', this.loadCountrySubdivision);
                    element.delegate('#email_preview', 'click', this.previewEmail);
                    element.delegate('#operator_list', 'change', this.loadOperatorUsersAndTags);
                    element.delegate('#requesting_user', 'change', this.toggleOtherTextfield);
                    element.delegate('#request_status', 'change', this.toggleStatusFields);
                    
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
                    
                    if ($('#user_list').html().trim()) {
                        $('#requesting_user').trigger('change');
                    }
                    
                    $('#city').trigger('keyup');
                    $('#request_status').trigger('change');
                }
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
                            prefix: 'requestedContractor.'
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
            },
            
            toggleStatusFields: function(event) {
                var status = $(this).val();
                
                $('#hold_date').hide();
                $('#reason_declined').hide();
                
                if (status.indexOf('Unsuccessful') > -1) {
                    $('#reason_declined').show();
                } else if (status.indexOf('Hold') > -1) {
                    $('#hold_date').show();
                }
            }
        }
    });
})(jQuery);