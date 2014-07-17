(function ($) {
    PICS.define('contractor.ContractorEdit', {
        methods: (function () {
            function init() {
                var $contractor_page = $('.ContractorEdit-page'),
                    $country_select = $('.country select');

                if ($contractor_page.length) {
                    $contractor_page.on('change', '#save_contractor_hasVatId', toggleVatField);

                    $country_select.on('change', updateCountryFields);

                    (hasVat()) ? showVat() : hideVat();
                    displayAppropriateFields($country_select.val());

                    $('.datepicker').datepicker();
                }
            }

            // Events
            function toggleVatField(event) {
                var $vat_checkbox = $(event.target),
                    $vat_wrapper = $('#vat_wrapper');

                if ($vat_checkbox.prop('checked')) {
                    $vat_wrapper.slideDown(400);
                } else {
                    $('#save_contractor_vatId').val('');
                    $vat_wrapper.slideUp(400);
                }
            }

            function updateCountryFields() {
                var $country = $('.country select'),
                    selected_country = $country.val() || '';

                displayAppropriateFields(selected_country);
                updateBillingCountry();
                updateCountrySubdivision();
                updateCountryBillingSubdivision();
                updateZipcode(selected_country);
            }

            // Other Methods
            function hasVat() {
                var vat_field = $('#save_contractor_vatId');

                return (vat_field.val() == '') ? false : true;
            }

            function showVat() {
                var vat = {
                    wrapper: $('#vat_wrapper'),
                    checkbox: $('#save_contractor_hasVatId')
                }

                vat.checkbox.attr('checked', 'checked')
                vat.wrapper.show();
            }

            function hideVat() {
                var vat = {
                    wrapper: $('#vat_wrapper'),
                    checkbox: $('#save_contractor_hasVatId')
                }

                vat.checkbox.removeAttr('checked');
                vat.wrapper.hide();
            }

            function displayAppropriateFields(selected_country) {
                if (selected_country == 'AE') {
                    $('#tax_li').hide();
                    $('#zip_li').hide();
                } else {
                    $('#tax_li').show();
                    $('#zip_li').show();

                    if (selected_country == 'US'){
                        $('.taxIdLabel').text(translate('JS.ContractorAccount.taxId.US')+':');
                        $('#taxIdLabelHelp').html(translate('JS.ContractorAccount.taxId.US.help'));
                    } else if (selected_country == 'CA') {
                        $('.taxIdLabel').text(translate('JS.ContractorAccount.taxId.CA')+':');
                        $('#taxIdLabelHelp').html(translate('JS.ContractorAccount.taxId.CA.help'));
                    } else {
                        $('.taxIdLabel').text(translate('JS.ContractorAccount.taxId.Other')+':');
                        $('#taxIdLabelHelp').html(translate('JS.ContractorAccount.taxId.Other.help'));
                    }
                }
            }

            function updateBillingCountry() {
                $('#billing_country').val($('.country select option:selected').text());
            }

            function updateCountrySubdivision() {
                var countrySubdivisionString = $('#countrySubdivision_li').attr('data'),
                    prefix = '',
                    element = '#countrySubdivision_li';

                updateCountrySubdivisionFor(countrySubdivisionString, prefix, element);
            }

            function updateCountryBillingSubdivision() {
                var countrySubdivisionString = $('#billing_countrySubdivision_li').attr('data'),
                    prefix = 'contractor.billingCountrySubdivision',
                    element = '#billing_countrySubdivision_li';

                updateCountrySubdivisionFor(countrySubdivisionString, prefix, element);
            }

            function updateCountrySubdivisionFor(countrySubdivisionString, prefix, element) {
                var $country = $('.country select'),
                    selected_country = $country.val() || '';

                if (selected_country) {
                    PICS.ajax({
                        url: 'CountrySubdivisionListAjax.action',
                        data: {
                            countryString: selected_country,
                            countrySubdivisionString: countrySubdivisionString,
                            needsSuffix: false,
                            prefix: prefix
                        },
                        success: function(data, textStatus, XMLHttpRequest) {
                            $(element).html(data);
                        }
                    });
                }
            }

            function updateZipcode(selected_country) {
                var Country = PICS.getClass('country.Country');

                Country.modifyZipcodeDisplay(selected_country);
            }

            return {
                init: init
            };
        }())
    });
})(jQuery);