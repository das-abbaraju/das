(function ($) {
    PICS.define('login.LoginController', {
        methods: {
            init: function () {
                if ($('.Login-page').length) {

                    $('#username').focus();

                    $('.login-container').delegate('select', 'change', this.setLanguage);
                }
            },

            setLanguage: function () {
                var supported_locale_element = $(this),
                    locale = supported_locale_element.val();

                PICS.ajax({
                    url: "Login!loginform.action",
                    data: {
                        request_locale: supported_locale_element.val()
                    },
                    success: function(data, textStatus, jqXHR) {
                        var login_container_element = supported_locale_element.closest('.login-container'),
                            language = login_container_element.find('#current_locale').val();
                        
                        login_container_element.html(data);
                        supported_locale_element.val(locale);
                    }
                });
            }
        }
    });
}(jQuery));