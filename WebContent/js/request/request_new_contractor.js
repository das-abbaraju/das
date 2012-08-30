(function ($) {
    PICS.define('request.RequestNewContractor', {
        methods: {
            init: function () {
                var element = $('.RequestNewContractorAccount-page');

                if (element.length) {
                    element.delegate('#country', 'change', this.loadCountrySubdivision);
                    element.delegate('#operatorsList', 'change', this.loadOperatorUsers);
                    element.delegate('#requestedUser', 'change', this.toggleOtherTextfield);
                    
                    $('.datepicker').datepicker({
                        changeMonth : true,
                        changeYear : true,
                        yearRange : '1940:2039',
                        showOn : 'button',
                        buttonImage : 'images/icon_calendar.gif',
                        buttonImageOnly : true,
                        buttonText : translate('JS.ChooseADate'),
                        constrainInput : true,
                        showAnim : 'fadeIn'
                    });
                    
                    if (!$('#countrySubdivision_li').html().trim()) {
                        $('#country').trigger('change');
                    }
                }
            },
            
            loadCountrySubdivision: function (event) {
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
                            $('#countrySubdivision_li').html(data);
                        }
                    });
                }
            },
            
            loadOperatorUsers: function (event) {
                var opID = $(this).val();
                
                PICS.ajax({
                   url: 'OperatorUserList.action',
                   data: {
                       'requestRelationship.operatorAccount': opID
                   },
                   success: function(data, textStatus, XMLHttpRequest) {
                       $('#user_list').html(data);
                   }
                });
            },
            
            toggleOtherTextfield: function (event) {
                if ($(this).val() != 0) {
                    $('#requestedOther').hide();
                } else {
                    $('#requestedOther').show();
                }
            }
        }
    });
})(jQuery);