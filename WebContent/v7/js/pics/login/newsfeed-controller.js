(function ($) {
    PICS.define('login.NewsfeedController', {
        methods: {
            init: function () {
                if ($('.Login-page').length) {
                    this.loadRssFeed();
                    var that = this;

                    $('#newsfeed_wrapper').on('updatefeed', function (event) {
                        that.loadRssFeed.apply(that);
                    });
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
                var locales = {
                    de: 'de',
                    en: '',
                    en_AU: '',
                    en_CA: 'ca',
                    en_GB: 'uk',
                    en_US: '',
                    en_ZA: 'za',
                    es: 'es',
                    es_ES: 'es',
                    es_MX: 'es',
                    fi: '',
                    fr: 'fr',
                    fr_CA: 'fr',
                    fr_FR: 'fr',
                    nl: '',
                    no: '',
                    pt: '',
                    sv: '',
                    zh: '',
                    zh_CN: '',
                    zh_TW: ''
                };

                var current_locale = $('#current_locale'),
                    current_locale = current_locale.val(),
                    feed_source = locales[current_locale] || '';

                if (feed_source !== '') {
                    feed_source += '/'
                }

                return 'http://www.picsauditing.com/' + feed_source + 'feed/?cat=6,9,10,11';
            }
        }
    });
}(jQuery));