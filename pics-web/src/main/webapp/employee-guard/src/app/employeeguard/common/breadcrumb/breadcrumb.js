angular.module('PICS.breadcrumb', [])

.directive('breadcrumb', function () {
    return {
        restrict: 'E',
        scope: {
            links: '=',
            target: '&'
        },
        replace: true,
        templateUrl: '/employee-guard/src/app/employeeguard/common/breadcrumb/_breadcrumb.tpl.html'
    };
});