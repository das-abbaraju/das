(function ($) {
    PICS.define('report.manage-report.GetStartedController', {
        methods: (function() {
            function init() {
                if ($('#ManageReports_getStarted_page').length) {
                    $('#user_type').on('change', toggleGoToFavoritesButton);
                }
            }

            // Events
            function toggleGoToFavoritesButton(event) {
                var $user_type = $(event.target);

                if ($user_type.val() != '') {
                    enableButton();
                } else {
                    disableButton();
                }
            }

            // Other Methods
            function enableButton() {
                $('#go_to_favorites').removeAttr('disabled');
            }

            function disableButton() {
                $('#go_to_favorites').attr('disabled', 'disabled');
            }

            return {
                init: init
            };
        }())
    });
})(jQuery);