(function ($) {
    PICS.define('widget.SessionTimer', {
        methods: function () {

            //default session times in seconds
            var session = {
                duration: 3600,
                notification_duration: 60,
                hasSession: false
            };

            return {
                REDIRECTION_TARGET: 'Login.action',

                init: function () {
                    var that = this;

                    // any ajax made from the site that have an expired session will be forced to the redirection target
                    $(document).on('ajaxError', $.proxy(function (event, jqXHR, ajaxSettings, thrownError) {
                        if (jqXHR.status === 401) {
                            document.location = this.REDIRECTION_TARGET;
                        }
                    }, this));

                    // any ajax made from the site will restart the session timeout
                    $(document).on('ajaxSend', function (event, jqXHR, settings) {
                        if (that.hasSession) {
                            PICS.debounce(that.initializeSessionTimeout(), 250);
                        }
                    });

                    this.getSessionDuration();
                },

                getSessionDuration: function () {
                    var that = this;

                    PICS.ajax({
                        url: "SessionAjax!getUserSession.action",
                        dataType: 'json',
                        success: function(data, textStatus, jqXHR) {
                            session.duration = data.sessionDuration;

                            // Don't start the session timeout for @Anonymous pages
                            if (typeof session.duration != 'number') {
                                return false;
                            }

                            //boolean used to restrict ajaxSend to users with session established
                            that.hasSession = true;

                            // Initialize session timer for the first time
                            that.initializeSessionTimeout();
                        }
                    });
                },

                initializeSessionTimeout: function () {
                    //overrides for testing in seconds
                    // session.duration = 15;
                    // session.notification_duration = 10;

                    //clear timers
                    clearTimeout(session.sessionExpirationTimer);
                    clearTimeout(session.notification_timer);

                    //remove notification
                    this.removeSessionTimeoutNotification();

                    //init remaining time
                    session.remaining_time = session.notification_duration;

                    this.startSessionTimeout();
                },

                logout: function () {
                    document.location = this.REDIRECTION_TARGET + '?button=logout';
                },

                removeSessionTimeoutNotification: function () {
                    $('.session-timeout-notification').slideUp(400, function () {
                        $(this).remove();
                    });
                },

                // make an ajax request to restart session via ajaxSend() above to re-initialize session
                restartSessionTimeout: function (event) {
                    var that = this;

                    //remove global body click event
                    $('body').off('click');

                    //only restart session if time remaining
                    if (session.remaining_time > 0) {
                        PICS.ajax({
                            url: "SessionAjax!getUserSession.action"
                        });
                    } else {
                        that.logout();
                    }
                },

                showSessionTimeoutNotification: function () {
                    var $notification_element = $('<div class="session-timeout-notification alert navbar-fixed-top">').html(function () {
                        return [
                            '<button type="button" class="close" data-dismiss="alert">×</button>',
                            '<h4>Auto Logout</h4>',
                            '<p>You will be logged off in <strong class="time">' + session.remaining_time + '</strong> seconds due to inactivity. ',
                            '<a href="#">Click here to continue using PICS Organizer.</a>',
                            '</p>'
                        ].join('');
                    });

                    $notification_element.prependTo('body');

                    // show session timeout notification
                    $notification_element.slideDown();
                },

                startSessionTimeout: function () {
                    var that = this,
                        time_to_show_notification_millis = (session.duration - session.notification_duration) * 1000;

                    session.notification_timer = setTimeout(function() {
                        // show session timeout notification n-minutes before session expires
                        that.showSessionTimeoutNotification();

                        // start session expiration process (count down of n-minutes before being session expires)
                        that.expireSession();

                        //add on click event to body to trigger session restart
                        $('body').on('click', $.proxy(that.restartSessionTimeout, that));

                    }, time_to_show_notification_millis);
                },

                expireSession: function () {
                    if (session.remaining_time > 0) {
                        this.updateSessionTimeoutNotification(session.remaining_time);

                        session.remaining_time -= 1;

                        session.sessionExpirationTimer = setTimeout($.proxy(this.expireSession, this), 1000);
                    } else {
                        this.logout();
                    }
                },

                updateSessionTimeoutNotification: function (seconds) {
                    $('.session-timeout-notification').find('.time').html(seconds);
                }
            };
       }
    });
})(jQuery);