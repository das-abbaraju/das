(function ($) {
    PICS.define('registration.RegistrationMakePayment', {
        methods: (function() {
            function init() {
                if ($('.RegistrationMakePayment-page').length) {

                    addFieldHelp();

                    $('.accept_contractor_agreement').on('change', togglePaymentButtons);

                    $('input[name=payment_method]').on('change', changePaymentMethod);
                }
            }

            // Events
            function togglePaymentButtons(event) {
                var $accept_agreement_checkbox = $(event.target);
                checkbox_checked = $accept_agreement_checkbox.attr('checked') == 'checked' ? true : false;

                if (checkbox_checked) {
                    enableButtons();
                } else {
                    disableButtons();
                }
            }

            function changePaymentMethod (event) {
                var selected_method = $(event.target).attr('id');

                if (selected_method == 'credit_card') {
                    showCreditCardForm();
                } else if (selected_method == 'pro_forma') {
                    showProFormaForm();
                }
            }

            // Other Methods
            function addFieldHelp() {
                var element = $('.help-text');

                element.each(function (key, value) {
                    var html = $(this).html();
                    var label = $(this).siblings('label');
                    var input = $(this).siblings('input[type=text], input[type=password], select');

                    label.attr('title', label.html().replace(':', ''));
                    label.attr('data-content', html.replace('"', "'"));

                    label.popover({
                        placement: 'top',
                        trigger: 'manual'
                    });

                    input.bind('focus', function (event) {
                        label.popover('show');
                    });

                    input.bind('blur', function (event) {
                        label.popover('hide');
                    });
                });
            }

            function enableButtons() {
                $('.accept_contractor_agreement').attr('checked', true);
                $('#submit_payment_button').removeAttr('disabled');
                $('#pro_forma_button').removeAttr('disabled');
            }

            function disableButtons() {
                $('.accept_contractor_agreement').attr('checked', false);
                $('#submit_payment_button').attr('disabled', 'disabled');
                $('#pro_forma_button').attr('disabled', 'disabled');
            }

            function showCreditCardForm() {
                $('#credit_card_form').show();
                $('#pro_forma_form').hide();
            }

            function showProFormaForm() {
                $('#pro_forma_form').show();
                $('#credit_card_form').hide();
            }

            return {
                init: init
            };
        }())
    });
})(jQuery);