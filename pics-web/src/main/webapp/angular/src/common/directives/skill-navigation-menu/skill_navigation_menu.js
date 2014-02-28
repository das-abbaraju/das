angular.module('PICS.directives')

.directive('skillnavigationmenu', function () {
    return {
        restrict: 'E',
        scope: {
            employee: '=',
            onmenuitemclick: '='
        },
        templateUrl: '/angular/src/common/directives/skill-navigation-menu/_skill-navigation-menu.tpl.html'
    };
});