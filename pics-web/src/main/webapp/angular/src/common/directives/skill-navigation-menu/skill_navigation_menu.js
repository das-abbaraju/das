angular.module('PICS.directives')

.directive('skillnavigationmenu', function () {
    return {
        restrict: 'E',
        scope: {
            employee: '=',
            onmenuitemclick: '='
        },
        templateUrl: '/angular/src/common/directives/skill-navigation/_skill-navigation.tpl.html'
    };
});