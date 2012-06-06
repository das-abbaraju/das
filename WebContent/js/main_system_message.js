(function ($) {
    PICS.define('main.SystemMessage', {
        methods: {
            init: function () {
                if ($('#systemMessage').length > 0) {
                    var system_message_element = $('#systemMessage');
                    
                    system_message_element.delegate('.system-message-locale', 'click', this.showValue);
                }
            },
            
            showValue: function(event) {
                var selected_locale_message = $(this).closest('.system-message-container').find('.system-message-value');
                $('#systemMessage').html(selected_locale_message);
                selected_locale_message.show();
            }
        }
    });
})(jQuery);