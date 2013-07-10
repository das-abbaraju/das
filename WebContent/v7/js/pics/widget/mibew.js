PICS.define('widgets.Mibew', {
    methods: {
        init: function () {
            $chat_links = $('.chat-link');

            if ($chat_links.length) {
                $chat_links.on('click', this.openMibew);
            }
        },

        openMibew: function (event) {
            var $chat_link = $(this),
                mibew_url = $chat_link.attr('href'),
                url_param = escape(document.location.href),
                referrer_param = escape(document.referrer),
                url = mibew_url + '&url=' + url_param + '&referrer=' + referrer_param;

            if (navigator.userAgent.toLowerCase().indexOf('opera') != -1 && window.event.preventDefault) {
                window.event.preventDefault();
            }

            this.newWindow = window.open(url, 'webim', 'toolbar=0, scrollbars=0, location=0, status=1, menubar=0, width=640, height=480, resizable=1');
            this.newWindow.focus();
            this.newWindow.opener = window;

            return false;
        }
    }
});