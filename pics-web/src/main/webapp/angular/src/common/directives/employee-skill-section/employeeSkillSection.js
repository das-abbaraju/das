angular.module('PICS.directives')

.directive('employeeskillsection', function () {
    return {
        restrict: 'E',
        scope: {
            title: '@',
            skills: '='
        },
        templateUrl: '/angular/src/common/directives/employee-skill-section/_employee-skill-section.tpl.html'
    };
});