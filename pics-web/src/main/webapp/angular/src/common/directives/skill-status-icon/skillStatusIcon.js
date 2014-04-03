angular.module('PICS.directives')

.directive('skillstatusicon', function () {
    return {
        restrict: 'E',
        scope: {
            status: '@status'
        },
        replace: true,
        templateUrl: '/angular/src/common/directives/skill-status-icon/_skill-status-icon.tpl.html'
    };
});