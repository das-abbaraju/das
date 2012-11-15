(function ($) {
    PICS.define('frontend-guide.FrontendDevelopmentGuideController', {
        methods: {
            init: function () {
                if ($('.FrontendDevelopmentGuide-page').length > 0) {
                    var that = this,
                        body = $('body'),
                        hash = window.location.hash;
                    
                    body.attr({
                        'data-spy': 'scroll',
                        'data-target': '.side-bar',
                        'data-offset': '100'
                    });
                    
                    if (hash) {
                        this.scrollTo(hash);
                    }
                    
                    $('.side-bar a').on('click', function (event) {
                        that.onSidebarNavigation.call(that, event);
                    });
                }
            },
            
            onSidebarNavigation: function (event) {
                var element = $(event.target);
                
                this.scrollTo(element.attr('href'));
            },
            
            scrollTo: function (selector) {
                var body = $('body'),
                    target = $(selector),
                    offset = 90,
                    target_offset_top = target && target.offset().top - offset;
            
                setTimeout(function () {
                    body.scrollTop(target_offset_top);
                }, 1);
            }
        }
    });
}(jQuery));