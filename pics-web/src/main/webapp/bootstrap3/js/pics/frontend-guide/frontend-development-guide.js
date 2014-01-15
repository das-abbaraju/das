(function ($) {
    PICS.define('frontend-guide.FrontendDevelopmentGuideController', {
        methods: (function () {
            var $toggle_markup_button, $html_collapsibles;

            function init() {
                if ($('.FrontendDevelopmentGuide-page').length > 0) {
                    initClassVars();
                    bindEvents();
                }
            }

            function initClassVars() {
                $toggle_markup_button = $('.show-markup');
                $html_collapsibles = $('.html-collapsible');
            }

            function bindEvents() {
                $('.show-markup').on('click', onToggleMarkupButtonClick);

                $html_collapsibles.on('show.bs.collapse', onHtmlCollapsibleShow);
                $html_collapsibles.on('hide.bs.collapse', onHtmlCollapsibleHide);
            }

            function onToggleMarkupButtonClick() {
                toggleHtmlCollapsibles();
            }

            function onHtmlCollapsibleShow(event) {
                var $html_collapsible = $(event.target);

                $html_collapsible.closest('.panel').addClass('open');
            }

            function onHtmlCollapsibleHide(event) {
                var $html_collapsible = $(event.target);

                $html_collapsible.closest('.panel').removeClass('open');
            }

            function toggleHtmlCollapsibles() {
                $toggle_markup_button.toggleClass('active');

                if ($toggle_markup_button.hasClass('active')) {
                    toggleCollapsiblesActive();
                } else {
                    toggleCollapsiblesInactive();
                }
            }

            function toggleCollapsiblesActive() {
                    $html_collapsibles
                        .removeClass('collapse')
                        .addClass('in')
                        // Override Bootstrap inline style
                        .css('height', 'auto')
                        .closest('.panel').addClass('open');
            }

            function toggleCollapsiblesInactive() {
                    $html_collapsibles
                        .removeClass('in')
                        .addClass('collapse')
                        // Override Bootstrap inline style
                        .css('height', 0)
                        .closest('.panel').removeClass('open');
            }

            return {
                init: init
            };
        }())
    });
}(jQuery));