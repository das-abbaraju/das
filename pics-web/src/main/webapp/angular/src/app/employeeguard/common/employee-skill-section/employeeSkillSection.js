angular.module('PICS.employeeguard')

.directive('employeeskillsection', function () {
    return {
        restrict: 'E',
        scope: {
            title: '@',
            skills: '=',
            icon: '@'
        },
        templateUrl: '/angular/src/app/employeeguard/common/employee-skill-section/_employee-skill-section.tpl.html'
    };
});