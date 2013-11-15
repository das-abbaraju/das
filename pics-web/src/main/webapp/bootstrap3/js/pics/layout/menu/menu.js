(function ($) {
    PICS.define('layout.menu.Menu', {
        methods: (function () {
            var SEARCH_RESULTS_LIMIT = 10,
                ENTER_KEY = 13,

                navbar_el = $('#primary_navigation'),
                dropdown_toggle_el = navbar_el.find('.dropdown-toggle'),
                search_query_el = navbar_el.find('.search-box'),
                search_box_el = navbar_el.find('.search-box');

            function init() {
                configureDropdown();
                configureUserSearch();

                bindEvents();
            }

            function configureDropdown() {
                dropdown_toggle_el.dropdown();
            }

            function configureUserSearch() {
                search_query_el.typeahead([{
                    name: 'primary-search',
                    remote: {
                        // url: '/v7/js/pics/layout/menu/typeahead.json?q=%QUERY',
                        url: '/SearchBox!json.action?q=%QUERY&',
                        filter: filterResponse,
                        cache: false
                },
                valueKey: 'result_name',
                limit: SEARCH_RESULTS_LIMIT,
                template: getResultItemTemplate(),
                footer: getFooterTemplate(),
                engine: Hogan
                }]);
            }

            function bindEvents() {
                search_query_el.on('typeahead:selected', onSearchQuerySelected);
                search_query_el.on('typeahead:suggestionsRendered', onSearchQuerySuggestionsRendered);

                search_box_el.on('focus', onSearchElFocus);
                search_box_el.on('blur', onSearchElBlur);

                $(document).on('mouseover', '.more-results-link', onMoreResultsLinkMouseOver);
                $(document).on('click', '.more-results-link', onMoreResultsLinkClick);
            }

            function onSearchQuerySelected(event, datum) {
                redirectToSelectionUrl(datum.result_id, datum.search_type);
            }

            function onSearchQuerySuggestionsRendered() {
                addUnderCursorStateToFirstResult();
            }

            function onSearchElFocus() {
                toggleSearchIconSelected();
            }

            function onSearchElBlur() {
                toggleSearchIconSelected();
            }

            function onMoreResultsLinkClick() {
                redirectToMoreResults();
            }

            function onMoreResultsLinkMouseOver() {
                removeUnderCursorStateFromAllResults();
            }

            function getResultItemTemplate() {
                return [
                     '<a href="/Search.action?button=getResult&searchID={{result_id}}&searchType={{search_type}}" class="search-item account-{{account_status}}">',
                        '<p>',
                            '<span class="name"><strong>{{result_name}}</strong></span>',
                            '<span class="id"><strong>{{result_id}}</strong></span>',
                        '</p>',
                        '<p>',
                            '<span class="location">{{result_at}}</span>',
                            '<span class="type">{{result_type}}</span>',
                        '</p>',
                    '</a>',
                ].join('');
            }

            function getFooterTemplate() {
                return [
                    '<div class="footer">',
                        '<a href="#" class="more-results-link">',
                            'More Results...',
                        '</a>',
                        '<p class="total-results">',
                        '</p>',
                    '</div>'
                ].join('');
            }

            function filterResponse(data) {
                // Insert empty datum to force rendering of dropdown,
                // even when there are no results
                if (data.total_results == 0) {
                    data.results = [{}];
                }

                renderFooter(data);

                return data.results;
            }

            function renderFooter(data) {
                var total_results = data.total_results,
                    results = data.results,
                    search_results_count = Math.min(total_results, SEARCH_RESULTS_LIMIT);

                if (total_results > results.length) {
                    $('.more-results-link').show();
                } else {
                    $('.more-results-link').hide();
                }

                if (total_results > 0) {
                    $('.total-results').html('Showing ' + search_results_count + ' of ' + total_results);
                    $('.tt-suggestions').show();
                } else {
                    $('.tt-suggestions').hide();
                    $('.total-results').html('<span class="no-results-msg">No Results</span>');
                }
            }

            function redirectToSelectionUrl(result_id, search_type) {
                var href = '/Search.action?button=getResult&searchID=' + result_id + '&searchType=' + search_type;

                window.location.href = href;
            }

            function addUnderCursorStateToFirstResult() {
                var total_results = $('.tt-suggestions').children('.tt-suggestion').length;

                if (total_results > 0) {
                    $('.tt-suggestion:first').addClass('tt-is-under-cursor');
                }
            }

            function toggleSearchIconSelected() {
                var $icon_search = $('#primary_navigation .icon-search');

                if ($icon_search.hasClass('selected')) {
                    $icon_search.removeClass('selected');
                } else {
                    $icon_search.addClass('selected');
                }
            }

            function removeUnderCursorStateFromAllResults() {
                $('.tt-suggestions .tt-is-under-cursor').removeClass('tt-is-under-cursor');
            }

            function redirectToMoreResults(event) {
                window.location.href = '/SearchBox.action?button=search&searchTerm=' + search_query_el.val();
            }

            return {
                init: init
            }
        }())
    });
}(jQuery));