(function ($) {
    PICS.define('frontend-guide.FrontendDevelopmentGuideController', {
        methods:{
            init:function () {
                if ($('.FrontendDevelopmentGuide-page').length > 0) {
                    var that = this,
                        body = $('body'),
                        hash = window.location.hash;

                    $('body').scrollspy({
                        spy:'scroll',
                        target:'.side-bar',
                        offset:95
                    });

                    $('.side-bar .nav-list').affix({
                        offset:114
                    });

                    if (hash) {
                        this.scrollTo(hash);
                    }

                    $('.side-bar a').on('click', function (event) {
                        that.onSidebarNavigation.call(that, event);
                    });
                }
            },

            onSidebarNavigation:function (event) {
                var element = $(event.target);

                this.scrollTo(element.attr('href'));
            },

            scrollTo:function (selector) {
                var body = $('body'),
                    target = $(selector),
                    offset = 95,
                    target_offset_top = target && target.offset().top - offset;

                setTimeout(function () {
                    body.scrollTop(target_offset_top);
                }, 1);
            }
        }
    });
}(jQuery));