PICS.define('widget.SessionTimer', {
    methods: (function () {

        var reload_disabled = false;

        function disableReload() {
            reload_disabled = true;
        };

        function init() {
            bindGlobalAjaxErrorHandler();
        }

        function bindGlobalAjaxErrorHandler() {
            $(document).on('ajaxError', function (event, jqXHR, ajaxSettings, thrownError) {
                if (jqXHR.status === 401 && !reload_disabled) {
                    reloadPageToInvalidateSession();                        
                }
            });
        }

        /*This request triggers a java interceptor which
        handles the redirect to the login page*/
        function reloadPageToInvalidateSession() {
            window.location.reload();
        }

        return {
            init: init,
            disableReload: disableReload
        };
    }())
});