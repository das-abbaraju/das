(function ($) {
    PICS.define('frontend-guide.FrontendDevelopmentGuideController', {
        methods: (function () {
            function init() {
                if ($('.FrontendDevelopmentGuide-page').length > 0) {
                    hljs.initHighlighting();
                }
            }

            return {
                init: init;
            };
        }
    }())
}(jQuery));