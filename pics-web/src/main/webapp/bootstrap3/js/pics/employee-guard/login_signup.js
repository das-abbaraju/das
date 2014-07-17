PICS.define('employee-guard.SignUp', {
    methods: (function () {
        var sign_up_page_el = $('.employee_guard_sign_up-page'),
            selected_language,
            language_select_el,
            formValues;

        function init() {
            if (sign_up_page_el.length > 0) {
                bindEvents();
            }
        }

        function bindEvents() {
            language_select_el = sign_up_page_el.find('#supported_locales');

            language_select_el.on('change', onSupportedLanguageChange);
            language_select_el.val(selected_language);

            if (formValues) {
                prefillFormData(formValues);
            }

        }

        function prefillFormData (formValues) {
            var form = sign_up_page_el.find('form');

            form.find('#employee_guard_create_account_hashCode').val(formValues.hashCode);
            form.find('#employee_guard_create_account_firstName').val(formValues.firstName);
            form.find('#employee_guard_create_account_lastName').val(formValues.lastName);
            form.find('#employee_guard_create_account_email').val(formValues.email);
        }

        function onSupportedLanguageChange(event) {
            var language = language_select_el.val();
            selected_language = language;
            setActiveLanguage(language);
            setFormValues(language);
        }

        function setActiveLanguage(language) {
            PICS.ajax({
                url: 'sign-up/switchLanguage',
                request_locale: language,
                success: onUpdateLanguageRequestSuccess
            });
        }

        function setFormValues(language) {
            var form = sign_up_page_el.find('form');

            formValues = {};
            formValues.hashCode = form.find('#employee_guard_create_account_hashCode').val();
            formValues.firstName = form.find('#employee_guard_create_account_firstName').val();
            formValues.lastName = form.find('#employee_guard_create_account_lastName').val();
            formValues.email = form.find('#employee_guard_create_account_email').val();
        }

        function onUpdateLanguageRequestSuccess(new_login_form_html) {
            sign_up_page_el.find('#login_row').replaceWith(new_login_form_html);
            init();
        }

        return {
            init: init
        };
    }())
});