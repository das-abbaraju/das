(function ($) {
    PICS.define('user.PasswordReset', {
        //This is ONLY for appUser password reset.  Please kill off as soon as page is angular
        methods: (function () {
            var $form,
                $password,
                $confirm,
                $alert,
                $username;

            function init() {
                if ($('.password-reset-page').length) {
                    initElements();
                    $form.on('submit', submitPasswordForm);
                }
            }

            function initElements() {
                $form = $('.password-reset-page form'),
                $password = $form.find('input[name="password"]'),
                $confirm = $form.find('input[name="password2"]'),
                $alert = $form.find('.alert'),
                $username = $form.find('input[name="username"]');
            }

            function submitPasswordForm(event) {
                var password = $password.val(),
                    confirm = $confirm.val();

                var isValid = validateFields($password.val(), $confirm.val());

                if (!isValid) {
                    event.preventDefault();
                }
            }

            function validateFields(password, confirm) {
                var isValid;

                if ((password === '') || (confirm === '')) {
                    showErrorMessage('Your password field can not be empty.');
                    isValid = false;
                } else if (password === $username.val() || confirm === $username.val()) {
                    console.log('bad username');
                    showErrorMessage('Your password can not be your username.');
                    isValid = false;
                } else if (password !== confirm) {
                    console.log('not equal');
                    showErrorMessage('Password fields do not match.');
                    isValid = false;
                } else {
                    isValid = true;
                }

                return isValid;
            }

            function showErrorMessage(msg) {
                $alert.html(msg);
                $alert.show();
            }

            return {
                init: init
            };
        }())
    });
}(jQuery));