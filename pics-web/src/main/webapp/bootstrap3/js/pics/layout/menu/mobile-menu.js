(function ($) {
    PICS.define('layout.menu.MobileMenu', {
        methods: (function () {
            var MENU_LEVEL_TO_COLORS = {
                    0: {
                        BACKGROUND: '#333',
                        HIGHLIGHT: '#333',
                        BORDER_BOTTOM: '#333'
                    },
                    1: {
                        BACKGROUND: '#252525',
                        HIGHLIGHT: '#333',
                        BORDER_BOTTOM: '#1a1a1a'
                    },
                    2: {
                        BACKGROUND: '#1a1a1a',
                        HIGHLIGHT: '#252525',
                        BORDER_BOTTOM: '#1a1a1a'
                    }
                },

                current_menu_level = 0,

                $header = $('#mobile_menu').find('.header'),
                $navigation_el = $('#mobile_menu_navigation'),
                $search_el = $('#mobile_menu_search'),
                $results_container = $search_el.children('ul');

            function init() {
                configureMobileMenu();
                bindEvents();
            }

            function configureMobileMenu() {
                configureNavigation($navigation_el);
                configureSearch($search_el);

                $navigation_el.removeClass('loading');
                $search_el.removeClass('loading');
            }

            function configureNavigation($navigation_el) {
                $navigation_el.mmenu({
                    dragOpen: {
                        open: false,
                    }
                });
            }

            function configureSearch(search_el) {
                $search_el.mmenu({
                    dragOpen: {
                        open: false
                    },
                    position: 'right',
                    searchfield:{
                        add: true,
                        search: false,
                        maxStartPos: $(window).width()
                    }
                });
            }

            function bindEvents() {
                $navigation_el.find('.mm-subopen').on('click', onSubmenuOpen);
                $navigation_el.find('.mm-subclose').on('click', onSubmenuClose);

                $search_el.find('input').on('keyup', PICS.debounce(onSearchKeyUp, 300));
                $search_el.on('click', 'a', onSearchResultItemClick);

                $(window).on('resize', onWindowResize)
            }

            function onSubmenuOpen(event) {
                animateMenuStyleForOpen();
            }

            function onSubmenuClose(event) {
                animateMenuStyleForClose();
            }

            function onSearchKeyUp(event) {
                var query = event.target.value;

                if (query != '') {
                    requestSearchResults(query, showSearchResults);
                } else {
                    $results_container.empty();
                }
            }

            function onSearchResultItemClick(event) {
                var $link = $(event.currentTarget),
                    $li = $link.closest('li');

                $li.addClass('selected');

                window.location.href = $link.attr('href');

                event.preventDefault();
            }

            function onWindowResize(event) {
                if ($header.css('display') == 'none') {
                    closeMobileMenus();
                }
            }

            function animateMenuStyleForOpen() {
                current_menu_level += 1;

                animateMenuColorsForLevel(current_menu_level);
            }

            function animateMenuStyleForClose() {
                current_menu_level -= 1;

                animateMenuColorsForLevel(current_menu_level);
            }

            function animateMenuColorsForLevel(menu_level) {
                var $menu_element = $('.mm-ismenu'),
                    $menu_highlight = $('.mm-menu a.mm-subclose'),
                    colors = MENU_LEVEL_TO_COLORS;

                $menu_highlight.animate({
                    backgroundColor: colors[menu_level].HIGHLIGHT,
                    borderBottomColor: colors[menu_level].BORDER_BOTTOM
                });

                $menu_element.animate({
                    backgroundColor: colors[menu_level].BACKGROUND
                });
            }

            function requestSearchResults(query, success_callback) {
                var success_callback = success_callback || function () {};

                $.ajax({
                    url: '/SearchBox!json.action',
                    data: {
                        q: query
                    },
                    success: function (data) {
                        success_callback(data, query);
                    }
                });
            }

            function showSearchResults(data, query) {
                if (data.total_results > 0) {
                    showResultsList(data);
                } else {
                    showNoResultsMsg();
                }
            }

            function showResultsList(data, query) {
                var result_items_html = getResultItemsHtml(data);

                $results_container.html(result_items_html);

                if (data.total_results > data.results.length) {
                    showMoreResultsMsg(data, query);
                }
            }

            function getResultItemsHtml(data) {
                var tpl = $('#mobile_search_result_item').html(),
                    tpl_compiled = Hogan.compile(tpl);

                return tpl_compiled.render(data);
            }

            function showMoreResultsMsg(data, query) {
                var more_results_html = getMoreResultsHtml({
                    query: query,
                    more_results_link_text: 'More Results\u2026', // PICS.text('Menu.menu.MobileSearch.moreResultsLink'),
                    total_results_message: 'Showing ' + data.results.length + ' of ' + data.total_results // PICS.text('Menu.menu.MobileSearch.moreResultsTotalLabel', results_len, data.total_results)
                });

                $results_container.append(more_results_html);
            }

            function getMoreResultsHtml(data) {
                var tpl = $('#mobile_search_more_results').html(),
                    tpl_compiled = Hogan.compile(tpl);

                return tpl_compiled.render(data);
            }

            function showNoResultsMsg() {
                var no_results_html = getNoResultsHtml();

                $results_container.html(no_results_html);
            }

            function getNoResultsHtml() {
                return '<li class="no-results-message">No results</li>';
            }

            function closeMobileMenus() {
                closeMobileNavigationMenu();
                closeMobileSearchMenu();
            }

            function closeMobileNavigationMenu() {
                if ($navigation_el.hasClass('mm-opened')) {
                    $navigation_el.trigger('close');
                }
            }

            function closeMobileSearchMenu() {
                if ($search_el.hasClass('mm-opened')) {
                    $search_el.trigger('close');
                }
            }

            return {
                init: init
            }
        }())
    });
}(jQuery));