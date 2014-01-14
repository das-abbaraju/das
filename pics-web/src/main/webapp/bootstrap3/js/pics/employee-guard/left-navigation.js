PICS.define('employee-guard.LeftNavigation', {
    methods: (function () {
        function init() {
            applyAffixToNavigation();

            //$('#side-navigation a').on('click', setActiveProject);
        }

        function applyAffixToNavigation() {
            var $sideBar = $('.bs-sidebar');

            $sideBar.affix({
                offset: {
                    top: function () {
                        var offsetTop = $sideBar.offset().top,
                            sideBarMargin  = parseInt($sideBar.children(0).css('margin-top'), 10),
                            navOuterHeight = $('#primary_navigation').height() + 20;

                        return (this.top = offsetTop - navOuterHeight - sideBarMargin);
                    }
                }
            });
        }

        function setActiveProject(event) {
            var $element = $(event.target),
                $filterName = $element.attr('data-filter');

            setActiveMenuItem($element);

            filterProjectList($filterName);
        }

        function setActiveMenuItem($element) {
            var $list_item = $element.closest('li'),
                $navigation = $element.closest('#side-navigation');

            $navigation.find('li.active').removeClass('active');

            $list_item.addClass('active');
        }

        function filterProjectList($filterName) {
            var $activeProject = $('#' + $filterName);

            if ($filterName === 'none') {
                showAllProjects();
            } else {
                showSelectedProject($activeProject);
            }
        }

        function showSelectedProject($activeProject) {
            hideAllProjects();
            $activeProject.show();
            $activeProject.addClass('visible');
        }

        function hideAllProjects() {
            $('.employee-guard-section').hide();
            $('.employee-guard-section').removeClass('visible');
        }

        function showAllProjects() {
            $('.employee-guard-section').removeClass('visible');
            $('.employee-guard-section').show();
        }

        return {
            init: init
        };
    }())
});
