(function ($) {
    PICS.define('registration.RegistrationMakePayment', {
        methods: (function() {
            function init() {
                if ($('.RegistrationMakePayment-page').length) {

                    addFieldHelp();

                    $('#accept_contractor_agreement').on('change', togglePaymentButtons);
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
                $('#submit_payment_button').removeAttr('disabled');
                $('#pro-forma-button').removeAttr('disabled');
            }

            function disableButtons() {
                $('#submit_payment_button').attr('disabled', 'disabled');
                $('#pro-forma-button').attr('disabled', 'disabled');
            }

            return {
                init: init
            };
        }())
    });
})(jQuery);