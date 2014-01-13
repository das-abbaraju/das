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
                configureNavigation();
                configureSearch();

                $navigation_el.removeClass('loading');
                $search_el.removeClass('loading');
            }

            function configureNavigation() {
                $navigation_el.mmenu({
                    dragOpen: {
                        open: true,
                        maxStartPos: $(window).width(),
                        pageNode: $('#mobile_menu .header')
                    }
                });
            }

            function configureSearch() {
                $search_el.mmenu({
                    dragOpen: {
                        open: true,
                        maxStartPos: $(window).width(),
                        pageNode: $('#mobile_menu .header')
                    },
                    position: 'right',
                    searchfield:{
                        add: true,
                        search: false
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
                var $menu_element = $('#mobile_menu_navigation'),
                    $menu_highlight = $menu_element.find('a.mm-subclose'),
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

                PICS.ajax({
                    url: '/SearchBox!json.action',
                    data: {
                        q: query
                    },
                    dataType: 'json',
                    success: function (data) {
                        success_callback(data, query);
                    }
                });
            }

            function showSearchResults(data, query) {
                if (data.total_results > 0) {
                    showResultsList(data, query);
                } else {
                    showNoResultsMsg();
                }
            }

            function showResultsList(data, query) {
                var result_items_html = getResultItemsHtml(data);

                $results_container.html(result_items_html);

                if (data.total_results > data.results.length) {
                    showMoreResultsLink(query);
                }

                showMoreResultsMsg(data);
            }

            function getResultItemsHtml(data) {
                var tpl = $('#mobile_search_result_item').html(),
                    tpl_compiled = Hogan.compile(tpl);

                return tpl_compiled.render(data);
            }

            function showMoreResultsLink(query) {
                var more_results_link_html = getMoreResultsLinkHtml({
                    more_results_link_text: 'More Results\u2026', // PICS.text('Menu.menu.MobileSearch.moreResultsLink'),
                    query: query
                });

                $results_container.append(more_results_link_html);
            }

            function showMoreResultsMsg(data) {
                var more_results_html = getMoreResultsMsgHtml({
                    total_results_message: 'Showing ' + data.results.length + ' of ' + data.total_results // PICS.text('Menu.menu.MobileSearch.moreResultsTotalLabel', results_len, data.total_results)
                });

                $results_container.append(more_results_html);
            }

            function getMoreResultsLinkHtml(data) {
                var tpl = $('#mobile_search_more_results_link').html(),
                    tpl_compiled = Hogan.compile(tpl);

                return tpl_compiled.render(data);
            }

            function getMoreResultsMsgHtml(data) {
                var tpl = $('#mobile_search_more_results_msg').html(),
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