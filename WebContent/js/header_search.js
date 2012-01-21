PICS.define('header.Search', {
    methods: {
        init: function () {
            if ($('#header #search_box').length) {
                $('body').bind('keydown', this.focusSearchBox);
            }
        },
        
        focusSearchBox: (function () {
            var timeout;
            var click = 0;
            
            return function (event) {
                if (event.keyCode == 192) {
                    click++;
                    
                    if (!timeout) {
                        var timeout = setTimeout(function () {
                            timeout = null;
                            click = 0;
                        }, 500);
                    }
                    
                    if (click > 1) {
                        var search_box_element = $('#header #search_box');
                        
                        search_box_element.val('');
                        search_box_element.focus();

                        return false;
                    }
                }
            };
        }())
    }
});