(function ($) {
    PICS.define('widget.SessionTimer', {
        methods: function () {

            //default session times in seconds
            var session = {
                duration: 3600,
                notification_duration: 60,
                sessionActive: false
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
                        if (that.getSessionActive()) {
                            PICS.debounce(that.initializeSessionTimeout(), 250);
                        }
                    });

                    this.getSessionDuration();
                },

                getSessionTimeoutNotification: function () {
                    PICS.ajax({
                        url: "SessionTimeout.action",
                        dataType: 'html',
                        success: function(data, textStatus, jqXHR) {
                            session.timeout_notification = data.trim();
                        }
                    });
                },

                getSessionActive: function () {
                    return this.sessionActive;
                },

                getSessionDuration: function () {
                    var that = this;

                    PICS.ajax({
                        url: "SessionAjax!getUserSession.action",
                        dataType: 'json',
                        success: function(data, textStatus, jqXHR) {
                            function isPageAnonymous() {
                                return data.error;
                            }

                            if (!isPageAnonymous()) {
                                that.setSessionDuration(data.sessionDuration);
                                that.setSessionActive(true);
                                that.initializeSessionTimeout();
                                that.getSessionTimeoutNotification();
                            }
                        }
                    });
                },

                initializeSessionTimeout: function () {
                    //overrides for testing in seconds
                    // session.duration = 15;
                    // session.notification_duration = 10;

                    //clear timers
                    clearTimeout(session.expiration_timer);
                    clearTimeout(session.notification_timer);

                    //remove notification
                    this.removeSessionTimeoutNotification();

                    //init remaining time
                    session.remaining_time = session.notification_duration;

                    this.startSessionTimeout();
                },

                logout: function () {
                    PICS.ajax({
                        url: "Login!sessionLogout.action",
                        dataType: 'json',
                        success: function(data, textStatus, jqXHR) {
                            if (data.referer) {
                                //redirect to referer to set "from" cookie
                                //to triggger redirect to previous page on login
                                document.location = data.referer;
                            } else {
                                this.REDIRECTION_TARGET + '?button=logout';
                            }
                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            this.REDIRECTION_TARGET + '?button=logout';
                        }
                    });
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

                setSessionDuration: function (duration) {
                    session.duration = duration;
                },

                setSessionActive: function (value) {
                    this.sessionActive = value;
                },

                showSessionTimeoutNotification: function () {
                    $(session.timeout_notification).prependTo('body');

                    $('.session-timeout-notification').slideDown();
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

                        session.expiration_timer = setTimeout($.proxy(this.expireSession, this), 1000);
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