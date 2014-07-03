(function ($) {
    PICS.define('contractor.ContractorEdit', {
        methods: (function () {
            function init() {
                var $contractor_page = $('.ContractorEdit-page'),
                    $country_select = $('.country select'),
                    $billing_country_select = $('.billing-country select'),
                    selected_country_iso = $country_select.val();

                if ($contractor_page.length) {
                    $contractor_page.on('change', '#save_contractor_hasVatId', toggleVatField);

                    $country_select.on('change', updateCountryFields);

                    $billing_country_select.on('change', updateBillingCountryFields);

                    (hasVat()) ? showVat() : hideVat();
                    displayAppropriateGeneralFields(selected_country_iso);
                    displayAppropriateBillingFields(selected_country_iso);

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

                displayAppropriateGeneralFields(selected_country);
                updateCountrySubdivision(selected_country);
            }

            function updateBillingCountryFields() {
                var $billing_country = $('.billing-country select'),
                    selected_country = $billing_country.val() || '';

                displayAppropriateBillingFields(selected_country);
                updateBillingCountrySubdivision(selected_country);
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

            function displayAppropriateGeneralFields(selected_country) {
                if (selected_country == 'AE') {
                    $('#tax_li').hide();
                    $('#taxIdItem').hide();
                    $('#zip_li').hide();
                } else {
                    $('#tax_li').show();
                    $('#taxIdItem').show();
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

            function displayAppropriateBillingFields(selected_country) {
                if (selected_country == 'AE') {
                    $('#billing_zip_li').hide();
                } else {
                    $('#billing_zip_li').show();
                }
            }

            function updateCountrySubdivision(countryIsoCode) {
                var countrySubdivisionString = $('#countrySubdivision_li').attr('data'),
                    prefix = '',
                    element = '#countrySubdivision_li';

                updateCountrySubdivisionFor(countryIsoCode, countrySubdivisionString, prefix, element);
            }

            function updateBillingCountrySubdivision(countryIsoCode) {
                var countrySubdivisionString = $('#billing_countrySubdivision_li').attr('data'),
                    prefix = 'contractor.billingCountrySubdivision',
                    element = '#billing_countrySubdivision_li';

                updateCountrySubdivisionFor(countryIsoCode, countrySubdivisionString, prefix, element);
            }

            function updateCountrySubdivisionFor(countryIsoCode, countrySubdivisionString, prefix, element) {
                if (countryIsoCode) {
                    PICS.ajax({
                        url: 'CountrySubdivisionListAjax.action',
                        data: {
                            countryString: countryIsoCode,
                            countrySubdivisionString: countrySubdivisionString,
                            needsSuffix: false,
                            prefix: prefix
                        },
                        success: function(data, textStatus, XMLHttpRequest) {
                            $(element).html(data);
                            $(element).find('select').select2({
                                width: 'resolve'
                            });
                        }
                    });
                }
            }

            return {
                init: init
            };
        }())
    });
})(jQuery);