angular.module('PICS.directives')

.directive('tooltip', function (TypeToChart) {
    return {
        restrict: 'A',
        scope: {
            title: '@',
            placement: '@',
            container: '@',
            html: '@'
        },
        link: function (scope, element, attrs) {
            scope.tooltip_props = ['title', 'placement', 'container'];

            scope.$watchCollection('tooltip_props', function(newValue) {
                var title = scope.title,
                    placement = scope.placement || 'top',
                    container = scope.container || 'body';

                $(element).tooltip({
                    title: title,
                    placement: placement,
                    container: container,
                    html: scope.html
                });
            });
        }
    };
});