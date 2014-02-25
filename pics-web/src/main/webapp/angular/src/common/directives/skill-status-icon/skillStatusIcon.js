angular.module('PICS.directives')

.directive('skillstatusicon', function () {
    return {
        restrict: 'E',
        scope: {
            status: '@status'
        },
        templateUrl: '/src/common/directives/skill-status-icon/_skill-status-icon.tpl.html'
    };
});