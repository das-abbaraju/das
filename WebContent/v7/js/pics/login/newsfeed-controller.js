(function ($) {
    PICS.define('Login.NewsfeedController', {
        methods: {
            init: function () {
                if ($('.Login-page').length) {
                    this.loadRssFeed();
                }

            },

            loadRssFeed: function() {
                var feedUrl = this.getFeedUrl();

                $('#newsfeed').rssfeed(feedUrl, {
                    limit: 3,
                    header: true,
                    linktarget: '_blank',
                    sort: 'date',
                    sortasc: false
                  });
            },

            getFeedUrl: function (locale) {
                var locale = $('#current_locale');

                locale = locale.val();
                locale = (locale == 'en') ? '' : locale + '/';

                return 'http://www.picsauditing.com/' + locale + 'feed/' + '?cat=6,9,10,11';
            }
        }
    });
}(jQuery));