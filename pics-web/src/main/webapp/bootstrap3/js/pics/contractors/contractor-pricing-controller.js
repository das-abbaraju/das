(function ($) {
    PICS.define('contractors.ContractorPricing', {
        methods: (function () {
            function init() {
                if ($('#ContractorPricing__page').length > 0) {
                    initPanelArrow();

                    $('.show-more-link, .show-less-link').on('click', onPanelLinkClick);
                }
            }

            // TODO: Move all of this panel logic to a separate class for use by all panel components,
            // i.e., a directive, after converting to AngularJS

            function initPanelArrow() {
                showPanelArrow();
            }

            function showPanelArrow() {
                var arrow_top = getClientListArrowTop(),
                    contractor_tier_arrow = $('.contractor-tier-arrow');

                contractor_tier_arrow.css('top', arrow_top);
                contractor_tier_arrow.show();
            }

            function getClientListArrowTop() {
                var arrow_target_element_top = $('.applies').offset().top,
                    arrow_target_element_height = $('.applies').outerHeight(),
                    arrow_panel_top = $('.client-list-panel').offset().top,
                    arrow_height = parseInt($('.contractor-tier-arrow').css('height'));

                return (arrow_target_element_top - arrow_panel_top) - (arrow_height / 2) + (arrow_target_element_height / 2) + 'px';
            }

            function onPanelLinkClick(event) {
                event.stopPropagation();
                event.preventDefault();

                $('.client-list-panel').toggleClass('expanded');
            }

            return {
                init: init
            };
        }())
    });
}(jQuery));