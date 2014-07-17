PICS.define('employee-guard.Welcome', {
    methods: (function () {
        var welcome_page_el = $('.employee_guard_welcome-page'),
            signup_page_el = $('.employee_guard_sign_up-page'),
            login_page_el,
            switchLanguageURL,
            selected_language;

        function init() {
            if (welcome_page_el.length > 0) {
                login_page_el = welcome_page_el;
                switchLanguageURL = 'login/switchLanguage';
            } else if (signup_page_el.length > 0) {
                login_page_el = signup_page_el;
                switchLanguageURL = 'sign-up/switchLanguage';
            }

            bindEvents();
            login_page_el.find('#supported_locales').val(selected_language);
        }

        function bindEvents() {
            $('#supported_locales').on('change', onSupportedLanguageChange);
        }

        function onSupportedLanguageChange(event) {
               var language_select_el = $(event.target),
                    language = language_select_el.val();

                updateLanguage(language);

            function updateLanguage(language) {
                selected_language = language;
                PICS.ajax({
                    url: switchLanguageURL,
                    data: {
                        request_locale: language
                    },
                    success: onUpdateLanguageRequestSuccess
                });
            }

            function onUpdateLanguageRequestSuccess(new_login_form_html) {
                login_page_el.find('#login_row').replaceWith(new_login_form_html);
                init();
            }
        }

        return {
            init: init
        };
    }())
});