var temp;

PICS.define('widget.SessionTimer', {
    methods: (function () {

        //CONSTANTS
        var LOGOUT_URL = 'Login.action?button=logout',
            NOTIFICATION_DURATION = 60,
            AJAX_URL_BLACKLIST = [
                'SessionAjax!getSessionTimeRemaining.action',
                'SessionAjax!resetTimeout.action',
                'SessionTimeout.action'
            ];

        //Default session times in seconds
        var session = {
            duration: 3600,
            sessionActive: false,
            notification_time_remaining: NOTIFICATION_DURATION
        };

        var $timeout_notification;


        function init() {
            // any ajax made from the site that have an expired session will be forced to the redirection target
            $(document).on('ajaxError', function (event, jqXHR, ajaxSettings, thrownError) {
                if (jqXHR.status === 401) {
                    redirect(LOGOUT_URL);
                }
            });

            // any ajax made from the site will restart the session timeout
            $(document).on('ajaxSend', function (event, jqXHR, settings) {
                if (getSessionActive()) {
                    if (!isBlacklistedUrl(settings.url)) {
                        PICS.debounce(requestRemainingSessionTime(startSession), 250);
                    }
                }
            });

            requestRemainingSessionTime(startSession);
        }

        function isBlacklistedUrl(url) {
            return AJAX_URL_BLACKLIST.indexOf(url) != -1;
        }

        function requestRemainingSessionTime(callback) {
            var start = nowInSeconds();

            PICS.ajax({
                url: "SessionAjax!getSessionTimeRemaining.action",
                dataType: 'json',
                success: function(data, textStatus, jqXHR) {
                    if (callback) {
                        var end = nowInSeconds();
                        var time_remaining = data.timeRemaining - (end - start);

                        callback(time_remaining);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                   log(errorThrown);
                   log(textStatus);
                   logout();
                }
            });
        }

        function nowInSeconds() {
            return new Date().getTime() / 1000;
        }

        function startSession(timeRemaining) {
            setSessionDuration(timeRemaining);
            setSessionActive(true);
            initializeSessionTimeout();
        }

        function initializeSessionTimeout() {
            //clear timers
            clearTimeout(session.expiration_timer);
            clearTimeout(session.notification_timer);

            //remove notification
            removeSessionTimeoutNotification();

            //reset notification counter to the default value
            session.notification_time_remaining = NOTIFICATION_DURATION;

            startSessionTimeout();
        }

        function removeSessionTimeoutNotification() {
            $('.session-timeout-notification').remove();
        }

        function startSessionTimeout() {
            if (timeUntilNotificationDisplays() === 0) {
                session.notification_time_remaining = Math.floor(session.duration);
                initializeTimeoutNotification();
            } else {
                session.notification_timer = setTimeout(initializeTimeoutNotification, timeUntilNotificationDisplays());
            }
        }

        function timeUntilNotificationDisplays() {
            return Math.max(0, (session.duration - NOTIFICATION_DURATION) * 1000);
        }

        function initializeTimeoutNotification() {
            // show session timeout notification n-minutes before session expires
            showSessionTimeoutNotification();

            //add on click event to body to trigger session restart
            $('body').on('click', function () {
                $('body').off('click');
                restartSessionTimeout();
            });

            // start session expiration process (count down of n-minutes before session expires)
            initiateFinalCountdown();
        }

        function showSessionTimeoutNotification() {
            if (typeof $timeout_notification === 'undefined') {
                requestSessionTimeoutNotification(showSessionTimeoutNotification);
            } else {
                $timeout_notification.prependTo('body');
                $('.session-timeout-notification').slideDown();
            }
        }

        // make an ajax request to restart session via ajaxSend() above to re-initialize session
        function restartSessionTimeout(event) {
            PICS.ajax({
                url: 'SessionAjax!resetTimeout.action',
                success: function () {
                    requestRemainingSessionTime(startSession);
                }
            });
        }

        function initiateFinalCountdown() {
            if (session.notification_time_remaining > 0) {
                updateSessionTimeoutNotification(session.notification_time_remaining);

                session.notification_time_remaining -= 1;

                session.expiration_timer = setTimeout(initiateFinalCountdown, 1000);
            } else {
                requestRemainingSessionTime(startSessionOrLogout);
            }
        }

        function updateSessionTimeoutNotification(seconds) {
            $('.session-timeout-notification').find('.time').html(seconds);
        }

        function startSessionOrLogout(timeRemaining) {
            if (timeRemaining > 0) {
                startSession(timeRemaining);
            } else {
                logout();
            }
        }

        function logout() {
            PICS.ajax({
                url: "Login!sessionLogout.action",
                dataType: 'json',
                success: function(data, textStatus, jqXHR) {
                    if (data.referer) {
                        //redirect to referer to set "from" cookie
                        //to triggger redirect to previous page on login
                        redirect(data.referer);
                    } else {
                        redirect(LOGOUT_URL);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    redirect(LOGOUT_URL);
                }
            });
        }

        function requestSessionTimeoutNotification(callback) {
            PICS.ajax({
                url: 'SessionTimeout.action',
                dataType: 'html',
                success: function(data, textStatus, jqXHR) {
                    var notification_html = $.trim(data);

                    $timeout_notification = $(notification_html);

                    if(callback) {
                        callback();
                    }
                }
            });
        }

        function redirect(url) {
            if (document.location.href !== url) {
                document.location = url;
            }
        }

        //DEVELOPMENT FUNCTIONS
        function displayRemainingSessionTime() {
            var start = nowInSeconds();

            PICS.ajax({
                url: "SessionAjax!getSessionTimeRemaining.action",  //!!!Rename method
                dataType: 'json',
                success: function(data, textStatus, jqXHR) {
                    var end = nowInSeconds(),
                        time_remaining = data.timeRemaining - (end - start);

                    log(time_remaining);

                },
                error: function(jqXHR, textStatus, errorThrown) {
                   log(errorThrown);
                   log(textStatus)
                }
            });
        }

        //GETTERS AND SETTERS
        function getSessionActive() {
            return session.sessionActive;
        }

        function setSessionActive(value) {
            session.sessionActive = value;
        }

        function setSessionDuration(duration) {
            session.duration = duration;
        }

        function getSession() {
            return session;
        }

        return {
            init: init,
            displayRemainingSessionTime: displayRemainingSessionTime
        };
    }())
});