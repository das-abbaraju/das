PICS.define('reference.NavigationRestructureController', {
    methods: {
        init: function () {
            var $page = $('#Reference_navigationRestructure_page');
            
            if ($page.length > 0) {
                var $nav_list = $('.nav-list'),
                    $menu_items = $nav_list.find('a'),
                    $window = $(window),
                    that = this;
                
                $menu_items.on('click', this.scrollTo);
                
                $page.scrollspy({
                    spy:'scroll',
                    target:'.side-bar',
                    offset: 30
                });
                
                // images loading may screw up offset calculations
                setTimeout(function () {
                    $nav_list.affix({
                        offset: {
                            top: function () {
                                var window_width = $window.width(),
                                    offset;
                                
                                if (window_width >= 1200) {
                                    offset = 615;
                                } else if (window_width >= 980 && window_width < 1200) {
                                    offset = 490;
                                } else if (window_width >= 768 && window_width < 980) {
                                    offset = 443;
                                }
                                
                                return offset;
                            }
                        }
                    });
                }, 1000);
            }
        },
        
        scrollTo: function (event) {
            var $element = $(event.currentTarget),
                $target = $($element.attr('href')),
                $body = $('body'),
                target_offset_top = $target.offset().top,
                scroll_to = target_offset_top - 29;
            
            $body.scrollTop(scroll_to);
            
            event.preventDefault();
        }
    }
});