(function ($) {
    PICS.define('layout.menu.Menu', {
        methods: (function () {
            var SEARCH_RESULTS_LIMIT = 10,
                ENTER_KEY = 13;

            function init() {
                var navbar_element = $('#primary_navigation'),
                    dropdown_toggle_element = navbar_element.find('.dropdown-toggle'),
                    search_query_element = navbar_element.find('.search-box'),
                    icon_search_el = navbar_element.find('.search-box');

                if (navbar_element.length > 0) {
                    configureDropdown(dropdown_toggle_element);
                }

                configureUserSearch(search_query_element);

                $(document).on('click', '.more-results-info a', redirectToMoreResults)
                $(document).on('mouseover', '.more-results-info', onMoreResultsInfoMouseOver);

                icon_search_el.on('focus', toggleSearchIconSelected);
                icon_search_el.on('blur', toggleSearchIconSelected);

                search_query_element.on('typeahead:selected', onSearchQuerySelected);
                search_query_element.on('typeahead:suggestionsRendered', onSearchQueryRendered);
            }

            function onSearchQuerySelected(event, datum) {
                var href = '/Search.action?button=getResult&searchID=' + datum.result_id + '&searchType=' + datum.search_type;

                window.location.href = href;
            }

            function onSearchQueryRendered() {
                $('.tt-suggestion:first').addClass('tt-is-under-cursor');
            }

            function onMoreResultsInfoMouseOver(event) {
                removeSuggestionSelection();
            }

            function removeSuggestionSelection() {
                $('.tt-suggestions .tt-is-under-cursor').removeClass('tt-is-under-cursor');
            }

            function toggleSearchIconSelected(event) {
                var $icon_search = $('#primary_navigation .icon-search');

                if ($icon_search.hasClass('selected')) {
                    $icon_search.removeClass('selected');
                } else {
                    $icon_search.addClass('selected');
                }
            }

            function configureDropdown(dropdown_toggle_element) {
                dropdown_toggle_element.dropdown();
            }

            function configureUserSearch(search_query_element) {
                search_query_element.typeahead([{
                    name: 'primary-search',
                    remote: {
                        // url: '/v7/js/pics/layout/menu/typeahead.json?q=%QUERY',
                        url: '/SearchBox!json.action?q=%QUERY',
                        filter: filterResponse
                },
                valueKey: 'result_name',
                limit: SEARCH_RESULTS_LIMIT,
                template: [
                     '<a href="/Search.action?button=getResult&searchID={{result_id}}&searchType={{search_type}}" class="search-item">',
                        '<p>',
                            '<span class="name"><strong>{{result_name}}</strong></span>',
                            '<span class="id"><strong>{{result_id}}</strong></span>',
                        '</p>',
                        '<p>',
                            '<span class="location">{{result_at}}</span>',
                            '<span class="type">{{result_type}}</span>',
                        '</p>',
                    '</a>',
                ].join(''),
                footer: [
                    '<div class="more-results-info">',
                        '<a href="#">',
                            'More Results...',
                        '</a>',
                        '<p class="total-results">',
                        '</p>',
                    '</div>'
                ].join(''),
                engine: Hogan
                }]);
            }

            function filterResponse(data) {
                // This must be here in order to perform custom rendering
                // of the footer before TT's rendering of the list completes
                renderTotalResults(data.total_results);

                if ($('.search-box').val().length == 1) {
                    selectFirstResult();
                }

                return data.results;
            }

            function selectFirstResult() {
                $('.tt-suggestion:first').addClass('tt-is-under-cursor');
            }

            function renderTotalResults(total_results) {
                var more_results_info_el = $('.more-reults-info'),
                    total_results_el = more_results_info_el.find('.total-results'),
                    search_results_count = Math.min(total_results, SEARCH_RESULTS_LIMIT);

                if (total_results > 0) {
                    $('.total-results').html('Showing ' + search_results_count + ' of ' + total_results);
                    more_results_info_el.toggleClass('has-more-results', true);
                } else {
                    more_results_info_el.toggleClass('has-more-results', false);
                }
            }

            function redirectToMoreResults(event) {
                var search_query_element = $(event.target).closest('.search-form').find('.search-box');

                window.location.href = 'SearchBox.action?button=search&searchTerm=' + search_query_element.val();
            }

            return {
                init: init
            }
        }())
    });
}(jQuery));