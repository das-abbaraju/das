angular.module('PICS.employeeguard')

.directive('skillnavigationmenu', function () {
    return {
        restrict: 'E',
        scope: {
            employee: '=',
            onmenuitemclick: '='
        },
        templateUrl: '/employee-guard/src/app/employeeguard/common/skill-navigation-menu/_skill-navigation-menu.tpl.html'
    };
});