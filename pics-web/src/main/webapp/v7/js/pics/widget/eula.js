(function ($) {
    PICS.define('widget.Eula', {
        methods: (function () {
            var $body = $('body'),
                allow_default = false,
                $form, eula_id, submit_event, user_credentials, user_eula;

            function init() {
                PICS.getClass('widget.SessionTimer').disableReload();
                $body.delegate('form.eula-required', 'submit', onEulaRequiredFormSubmit);                
            }

            function onEulaRequiredFormSubmit(event) {
                if (allow_default) return;

                $form = $(this);
                eula_id = $form.data('eula-id');
                submit_event = event;

                if (eula_id == 'login') {
                    user_credentials = getUserCredentialsFromLoginForm();                    
                }

                validateForm(onFormValid, onFormInvalid);
            }

            function validateForm(onFormValid, onFormInvalid) {
                // TODO: Perform ajax validation, rather than hard-coding validations for login form
                if (user_credentials.username && user_credentials.password) {
                    onFormValid();
                }
            }

            function onFormValid() {
                submit_event.preventDefault();

                fetchUserEula();                    
            }

            function onFormInvalid() {
                // Submit the form to force validation error message to appear
                submitForm();
            }

            function getUserCredentialsFromLoginForm() {
                var $login_container_el = $('.login-container');
                    $login_form_el = $login_container_el.find('form');
                    $username_input_el = $login_form_el.find('[name=username]');
                    $password_input_el = $login_form_el.find('[name=password]');

                return {
                    username: $username_input_el.val(),
                    password: $password_input_el.val()
                };
            }

            function fetchUserEula() {
                PICS.ajax({
                    type: 'GET',
                    url: getUserEulaUrl(),
                    dataType: 'json',
                    success: onUserEulaRequestSuccess,
                    statusCode: {
                        401: submitForm
                    }
                });
            }

            function getUserEulaUrl() {
                if (user_credentials.username && user_credentials.password) {
                    return 'eulas/' + eula_id + '/users/' + user_credentials.username + '.action?password=' + user_credentials.password;
                }
            }

            function onUserEulaRequestSuccess(data) {
                user_eula = data;

                switch (user_eula.status) {
                    case 'accepted':
                        submitForm();
                        break;
                    case 'not accepted':
                    case 'rejected':
                    default:
                        fetchEula(user_eula.eulaUrl, showEula);
                }
            }

            function fetchEula(eula_url, callback) {
                PICS.ajax({
                    type: 'GET',
                    url: eula_url,
                    dataType: 'html',
                    statusCode: {
                        401: submitForm
                    },
                    success: function (data) {
                        var eula_html = $.trim(data);

                        showEula(eula_html);
                    }
                });
            }

            function showEula(eula_html) {
                var $eula_html = $(eula_html),
                    $eula_container_el,
                    $eula_accept_button_el,
                    $eula_reject_button_el,
                    $body = $('body');

                $body.children().hide();
                $body.append(eula_html);

                setEulaHeight();

                $(window).on('resize', setEulaHeight);

                bindEulaEvents();
            }

            function setEulaHeight () {
                var window_height = $(window).height(),
                    $eula_container = $('.eula-container'),
                    $logo = $eula_container.find('.logo'),
                    $panel_body = $eula_container.find('.panel-body'),
                    $button_container = $eula_container.find('.button-container');

                $eula_container.find('.container').css('height', window_height);
                $logo.css('height', window_height * .05);
                $logo.css('margin-top', window_height * .05);
                $logo.css('margin-bottom', window_height * .03);
                $eula_container.find('.row').css('height', window_height * .87);

                var row_height = $eula_container.find('.row').height(),
                    panel_heading_height = $eula_container.find('.panel-heading').height(),

                    panel_heading_height_percent = panel_heading_height / row_height,
                    remaining_height_percent = 1 - panel_heading_height_percent,

                    panel_body_height = (remaining_height_percent / 2) * row_height,
                    button_container_height = (remaining_height_percent / 2) * row_height;

                if ($panel_body.css('overflow-y') == 'scroll') {
                    $panel_body.css('height', Math.floor(panel_body_height));
                    $button_container.css('height', Math.floor(button_container_height));
                } else {
                    $panel_body.css('height', 'auto');
                    $button_container.css('height', 'auto');
                }

                $eula_container.css('height', $(document).height());
            }

            function bindEulaEvents() {
                $eula_container_el = $('.eula-container');
                $eula_accept_button_el = $eula_container_el.find('.btn-agree');
                $eula_exit_button_el = $eula_container_el.find('.btn-exit');
                $eula_print_button_el = $eula_container_el.find('.icon-print');

                $eula_accept_button_el.on('click', onEulaAcceptButtonClick);
                $eula_exit_button_el.on('click', onEulaExitButtonClick);
                $eula_print_button_el.on('click', onEulaPrintButtonClick)
            }

            function onEulaAcceptButtonClick() {
                acceptEula(submitForm);
            }

            function onEulaExitButtonClick() {
                $body.children().show();

                resetForm();

                $eula_container_el.remove();
            }

            function onEulaPrintButtonClick() {
                window.print();
            }

            function resetForm() {
                var inputs = $form.find('input');

                inputs.val('');
                inputs.filter('[tabindex="2"]').focus();

                $('div.alert').remove();
            }

            function acceptEula(callback) {
                user_eula.status = 'accepted';

                PICS.ajax({
                    type: 'PUT',
                    url: getUserEulaUrl(),
                    data: JSON.stringify(user_eula),
                    contentType: 'application/json',
                    success: callback
                });
            }

            function submitForm() {
                allow_default = true;
                $login_form_el.submit();
            }

            return {
                init: init
            };
        }())
    });
}(jQuery));