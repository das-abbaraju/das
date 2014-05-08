(function ($) {
    PICS.define('login.LoginController', {
        methods: (function () {
            var $login_container_el,
                $login_form_el,
                $username_input_el,
                $password_input_el,
                $body = $('body'),

                user_credentials,
                user_eula;

           function init() {
                if ($('.Login-page').length) {
                    initEls();

                    $username_input_el.focus();

                    $login_container_el.delegate('select', 'change', onLanguageSelect);
                }
            }

            function initEls() {
                $login_container_el = $('.login-container');
                $login_form_el = $login_container_el.find('form');
                $username_input_el = $login_form_el.find('[name=username]');
                $password_input_el = $login_form_el.find('[name=password]');
            }

            // Language

            function onLanguageSelect(event) {
                var language_select_el = $(event.target),
                    language = language_select_el.val();

                updateLanguage(language);
            }

            function updateLanguage(language) {
                PICS.ajax({
                    url: "Login!loginform.action",
                    data: {
                        request_locale: language
                    },
                    success: onUpdateLanguageRequestSuccess
                });
            }

            function onUpdateLanguageRequestSuccess(data) {
                var new_login_form_html = data;

                $login_container_el.html(new_login_form_html);

                initEls();
            }

            return {
                init: init
            };
        }())
    });
}(jQuery));