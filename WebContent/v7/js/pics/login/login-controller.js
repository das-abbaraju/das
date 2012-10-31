(function ($) {
    PICS.define('login.LoginController', {
        methods: {
            init: function () {
                if ($('.Login-page').length) {

                    $('#username').focus();

                    $('#login_wrapper').delegate('select', 'change', this.setLanguage);

                    //TODO load login modal
                    /*this.isLoaded = 0;
                    $(document).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
                        if (jqXHR.status == 401) {
                            if (!that.isLoaded) {
                                that.showLoginModal(ajaxSettings);
                                that.isLoaded++;
                            }
                        }
                    });*/


                }
            },

            setLanguage: function () {
                var language = this.value;

                PICS.ajax({
                    url: "Login!loginform.action",
                    data: {
                        request_locale: language
                    },
                    success: function(data, textStatus, jqXHR) {
                        $('#login_wrapper').html(data);
                        $('#supported_locales').val(language);

                        //TODO update login modal content as well
                        /*$('section.login-form, .modal-login-form .modal-body').html(response);
                        $('#supported_locales, .modal-login-form #supported_locales').val(language);

                        var title = $(response).find('#login_modal_title');

                        $('.modal-login-form .modal-header h3').html(title.val());
                        */
                    }
                });
            }

            /*showLoginModal: function (originalSettings) {
                var me = this;

                PICS.ajax({
                    url: "Login!overlay.action",
                    success: function(response, status, loginXhr) {
                        var language = $(response).find("#current_locale").val(),
                            title = $(response).find('#login_modal_title');

                        var login_modal_new = PICS.modal({
                            modal_class: 'modal modal-login-form',
                            backdrop: true,
                            content: response,
                            keyboard: true,
                            title: title.val(),
                            width: 'auto'
                        });

                        login_modal_new.show();


                        $('.modal-login-form').delegate('#supported_locales', 'change', me.setLanguage)


                        var form = login_modal.find('form#Login');

                        form.ajaxForm({
                            url: 'Login!ajax.action',
                            dataType: 'json',
                            success: function(response, status, formXhr, $form) {
                                console.log(response);

                                if (response.loggedIn) {
                                    login_modal.modal('hide');
                                    if (originalSettings.url.indexOf('?') != -1) {
                                        console.log(originalSettings.url);
                                        originalSettings.url = originalSettings.url.replace(/\?.*$/,'');
                                        console.log(originalSettings.url);
                                    } else {
                                        console.log('no ? in url')
                                    }
                                    $.ajax(originalSettings);
                                } else {
                                    $('#loginMessages').msg('error', response.actionError, true);2
                                    $('#username').focus();
                                }
                            }
                        });
                    }
                });
            }*/
        }
    });
})(jQuery);