(function ($) {
    PICS.define('contact.ContactUsCtrl', {
        methods: (function() {
            function init() {
                if ($('#ContactUs__page').length) {
                    $('.message-form button').on('click', sendMessage);
                }
            }

            function sendMessage(event) {
                var $element = $(event.target),
                    $form = $element.closest('form'),
                    url = $form.attr('action'),
                    data = $form.serialize();

                event.preventDefault();

                PICS.ajax({
                    url: url,
                    data: data,
                    success: onSendMessageSuccess
                });
            }

            function onSendMessageSuccess(data) {
                var message = getConfirmationMessage();

                $('.message-container').html(message);
            }

            function getConfirmationMessage() {
                return [
                    '<div class="alert alert-success text-left">',
                        '<i class="icon-ok-sign"></i><h3>' + PICS.text('ContactUs.execute.MessageSent.Title') + '</h3>',
                        '<p>' + PICS.text('ContactUs.execute.MessageSent.Message') + '</p>',
                    '</div>'
                ].join('');
            }

            return {
                init: init
            };
        }())
    });
})(jQuery);