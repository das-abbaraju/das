PICS.define('PICS.ProfileEdit', {
    methods: (function () {

        // Private
        function apiKeyChangeMessage() {
            var message = translate('JS.ProfileEdit.alert.ExistingApiKeyWarning');
            return confirm(message);
        }

        function checkForApiKey() {
            var $api_key = $('#UserApiKey__div').find('input.apikey');

            if ($api_key.val().length > 0) {
                if (apiKeyChangeMessage()) {
                    generateAPIKey();
                }
            } else {
                generateAPIKey();
            }
        }

        function getTimezonesForUserCountry() {
            var $timezone_input = $('.timezone_input'),
                selected_country = $timezone_input.attr('data-country') || '',
                Country = PICS.getClass('country.Country');

            Country.getTimezones(selected_country);
        }

        function loadDialect(event) {
            var language = $(this).val(),
                $dialect = $('#profile_dialect');

            PICS.ajax({
                url: 'ProfileEdit!dialect.action',
                data: {
                    language: language
                },
                success: function(data, textStatus, jqXHR) {
                    $dialect.html(data);
                    $dialect.find('select').select2();
                }
            })
        }

        //Public
        function init() {
            var $element = $('.ProfileEdit-page');

            if ($element.length) {
                var $user_api_div = $('#UserApiKey__div');

                if ($user_api_div.length) {
                    $('#UserApiKey__div').on('click', 'button.positive', checkForApiKey);
                }

                $element.on('change', '#profile_language', loadDialect);

                getTimezonesForUserCountry();
            }
        }

        function generateAPIKey() {
            PICS.ajax({
                url: 'ProfileEdit!generateApiKey.action',
                data: {
                    user:currentUserID
                },
                dataType: 'json',
                success: function (data, textStatus, XMLHttpRequest) {
                    var input_field = $('#UserApiKey__div').find('input.apikey');
                    if (input_field) {
                        input_field.val(data.ApiKey);
                    }
                    var check_link = $('#UserApiKey__div').find('.apikey');
                    if (check_link) {
                        check_link.attr('href', data.ApiCheck);
                    }
                }
            });
        }

        return {
            init: init,
            generateAPIKey: generateAPIKey
        };
    }())
});