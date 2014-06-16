angular.module('PICS.directives')

.directive('uglyFooter', function ($timeout) {
    return {
        restrict: 'E',
        replace: true,
        templateUrl: '/angular/src/common/directives/ugly-footer/ugly-footer.tpl.html',
        link: function (scope, element) {
            function placeFooter() {
                var scrollbarExists = $("body").height() > $(window).height();

                if (scrollbarExists) {
                    element.removeClass('no-scrollbar');
                } else {
                    element.addClass('no-scrollbar');
                }
            }

            $timeout(placeFooter, 0);
            $(window).on('resize', placeFooter);
        }
    };
});