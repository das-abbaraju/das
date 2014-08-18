angular.module('PICS.directives')

.directive('toggleFilter', function ($timeout) {
    return {
        restrict: 'E',
        replace: true,
        templateUrl: '/angular/src/common/directives/toggle-filter/toggle-filter.tpl.html',
        scope: {
            ngModel: '=',
            toggleModel: '=',
            filterLabel: '@'
        }
    };
});