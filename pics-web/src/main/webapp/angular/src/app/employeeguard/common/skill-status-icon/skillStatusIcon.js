angular.module('PICS.employeeguard')

.directive('skillstatusicon', function () {
    return {
        restrict: 'E',
        scope: {
            status: '@status'
        },
        replace: true,
        templateUrl: '/angular/src/app/employeeguard/common/skill-status-icon/_skill-status-icon.tpl.html'
    };
});