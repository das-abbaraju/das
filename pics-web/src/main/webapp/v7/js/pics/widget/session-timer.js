PICS.define('widget.SessionTimer', {
    methods: (function () {

        function init() {
            bindGlobalAjaxErrorHandler();
        }

        function bindGlobalAjaxErrorHandler() {
            $(document).on('ajaxError', function (event, jqXHR, ajaxSettings, thrownError) {
                if (jqXHR.status === 401) {
                    reloadPageToInvalidateSession();
                }
            });
        }

        /*This request triggers a java interceptor which
        handles the redirect to the login page*/
        function reloadPageToInvalidateSession() {
            document.location.reload();
        }

        return {
            init: init
        };
    }())
});