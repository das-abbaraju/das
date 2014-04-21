angular.module('PICS.directives')

.directive('employeeskillsection', function () {
    return {
        restrict: 'E',
        scope: {
            title: '@',
            skills: '=',
            icon: '@'
        },
        templateUrl: '/angular/src/common/directives/employee-skill-section/_employee-skill-section.tpl.html'
    };
});