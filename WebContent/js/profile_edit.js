(function ($) {
    PICS.define('PICS.ProfileEdit', {
        methods: {
            init: function () {
                var user_api_div = $('#UserApiKey__div'),
                    that = this;

                if (user_api_div.length) {
                    $('#UserApiKey__div').delegate('button.positive', 'click', function (event) {
                        that.checkForApiKey.apply(that, [event]);
                    });
                }
            },

            apiKeyChangeMessage: function () {
                var message = translate('JS.ProfileEdit.alert.ExistingApiKeyWarning');
                return confirm(message);
            },

            checkForApiKey: function () {
                var that = this,
                    api_key = $('#UserApiKey__div').find('input.apikey');

                if (api_key.val().length > 0) {
                    if (that.apiKeyChangeMessage()) {
                        that.generateAPIKey();
                    }
                } else {
                    that.generateAPIKey();
                }
            },

            generateAPIKey: function () {
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
                    }
                });
            }
        }
    });
})(jQuery);