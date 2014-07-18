PICS.define('employee-guard.Welcome', {
    methods: (function () {
        var welcome_page_el = $('.employee_guard_welcome-page'),
            selected_language,
            language_select_el,
            profile_fname_el,
            profile_fname,
            companyName_el,
            companyName;

        function init() {
            if (welcome_page_el.length > 0) {
                bindEvents();
            }
        }

        function bindEvents() {
            language_select_el = welcome_page_el.find('#supported_locales');

            language_select_el.on('change', onSupportedLanguageChange);
            language_select_el.val(selected_language);

            profile_fname_el = welcome_page_el.find('#profile_fname');
            companyName_el = welcome_page_el.find('#companyName');

            populateGreeting();
        }

        function populationGreeting() {
            if (profile_fname) {
                profile_fname_el.html(profile_fname);
            }

            if (companyName) {
                companyName_el.html(companyName);
            }
        }

        function onSupportedLanguageChange(event) {
            var language = language_select_el.val();

            selected_language = language;
            setActiveLanguage(language);
            profile_fname = welcome_page_el.find('#profile_fname').html();
            companyName = welcome_page_el.find('#companyName').html();
        }

        function setActiveLanguage(language) {
            PICS.ajax({
                url: 'login/switchLanguage',
                data: formatFormData(language),
                success: onUpdateLanguageRequestSuccess
            });
        }

        function formatFormData(language) {
            var hashCode = $('#employee_guard_login_hashCode').val();

            var data = {
                request_locale: language,
                hashCode: hashCode
            };

            return data;
        }

        function onUpdateLanguageRequestSuccess(new_login_form_html) {
            welcome_page_el.find('#login_row').replaceWith(new_login_form_html);
            init();
        }

        return {
            init: init
        };
    }())
});