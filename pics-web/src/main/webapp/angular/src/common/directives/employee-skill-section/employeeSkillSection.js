angular.module('PICS.directives')

.directive('employeeskillsection', function () {
    return {
        restrict: 'E',
        scope: {
            title: '@',
            skills: '='
        },
        templateUrl: '/src/common/directives/employee-skill-section/_employee-skill-section.tpl.html'
    };
});