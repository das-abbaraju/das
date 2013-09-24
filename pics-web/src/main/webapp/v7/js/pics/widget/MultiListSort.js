/**
 * MultiListSort
 *
 * Enables drag-and-drop sorting of items across two vertically aligned lists, the first having a maximum number of items.
 *
 * Though currently restricted to the Manage Reports Favorites page, its current design should enable easy refactoring for generic use,
 * should need arise in the future.
 *
 * @author: Jason Roos
 * @date: 8-20-2013
 * @version: 1
 */
(function ($) {
    PICS.define('widget.MultiListSort', {
        methods: (function () {
            var TOP_LIST_MAX = 10,
                ANIMATION_DURATION = 500,
                CONTAINMENT_SELECTOR = '#favorite_reports_container',
                TOP_LIST_SELECTOR = '#favorite_reports .report-list',
                BOTTOM_LIST_SELECTOR = '#favorite_reports_overflow .report-list',
                UPDATE_URL = 'ManageReports!updateFavoritesOrder.action',
                UPDATE_PARAMETER_NAME = 'reports';

            function init() {
                if ($('.ManageReports-page #favorite_reports_container').length > 0) {

            /*** Nothing below this line is coupled to the Reports Manager Favorites page. ***/

                    $(TOP_LIST_SELECTOR).sortable({
                        containment: CONTAINMENT_SELECTOR,
                        connectWith: [BOTTOM_LIST_SELECTOR],
                        update: onTopListUpdate
                    });

                    $(BOTTOM_LIST_SELECTOR).sortable({
                        containment: CONTAINMENT_SELECTOR,
                        connectWith: [TOP_LIST_SELECTOR],
                        update: onBottomListUpdate
                    });
                }
            }

            // Event Handlers

            function onTopListUpdate(event, ui) {
                var $this = $(this),
                    this_list_items, last_item;

                if (ui.sender && topListMaxExceeded()) {
                    this_list_items = $this.children();

                    last_item = this_list_items.last();

                    bumpItem(last_item, ui.sender);
                }

                if (!ui.sender) {
                    updateOrder();
                }
            }

            function onBottomListUpdate(event, ui) {
                var $this = $(this),
                    this_list_items, first_item;

                if (ui.sender && !topListMaxExceeded()) {
                    this_list_items = $this.children();

                    first_item = this_list_items.first();

                    bumpItem(first_item, ui.sender);
                }

                if (!ui.sender) {
                    updateOrder();
                }
            }

            // Other Methods

            function bumpItem(item, target_list) {
                var $item = $(item);

                $item.animate({
                    top: getTranslation($item, target_list),
                },
                ANIMATION_DURATION,
                function () {
                    $item.removeAttr('style');

                    if (getTargetListLocation($item, target_list) == 'bottom') {
                        $(target_list).append(item);
                    } else {
                        $(target_list).prepend(item);
                    }
                });
            }

            function getTopListTranslation() {
                if (typeof top_list_translation == 'undefined') {
                    var $top_list_last_item = $(TOP_LIST_SELECTOR).children().last(),
                        $bottom_list_first_item = $(BOTTOM_LIST_SELECTOR).children().first();

                    top_list_translation = $bottom_list_first_item.position().top -
                        $top_list_last_item.position().top -
                        $top_list_last_item.height() -
                        ($top_list_last_item.css('margin-bottom').replace('px', '') * 2);
                }

                return top_list_translation;
            }

            function getBottomListTranslation() {
                if (typeof bottom_list_translation == 'undefined')  {
                    var $bottom_list_first_item = $(BOTTOM_LIST_SELECTOR).children().first(),
                        $top_list_last_item = $(TOP_LIST_SELECTOR).children().last();

                    bottom_list_translation = $top_list_last_item.position().top -
                        $bottom_list_first_item.position().top +
                        $bottom_list_first_item.height() +
                        ($bottom_list_first_item.css('margin-bottom').replace('px', '') * 2);
                }

                return bottom_list_translation;
            }

            function getTargetListLocation($item, target_list) {
                var source_list = $item.closest('ul');

                return target_list.position().top < source_list.position().top ? 'bottom' : 'top';
            }

            function getTranslation($item, target_list) {
                if (getTargetListLocation($item, target_list) == 'bottom') {
                    return getBottomListTranslation();
                } else {
                    return getTopListTranslation();
                }
            }

            function topListMaxExceeded() {
                return $(TOP_LIST_SELECTOR).children().length > TOP_LIST_MAX;
            }

            function updateOrder() {
                var top_list_item_ids = $(TOP_LIST_SELECTOR).sortable('toArray'),
                    bottom_list_item_ids = $(BOTTOM_LIST_SELECTOR).sortable('toArray'),
                    list_item_ids = $.merge(top_list_item_ids, bottom_list_item_ids),
                    data = {};

                data[UPDATE_PARAMETER_NAME] = list_item_ids.toString();

                PICS.ajax({
                    url: UPDATE_URL,
                    data: data
                });
            }

            return {
                init: init
            };
        }())
    });
}(jQuery));