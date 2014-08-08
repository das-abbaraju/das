angular.module('PICS.employeeguard.skills', [
    'ngRoute'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider.when('/employee-guard/skills', {
        templateUrl: '/employee-guard/src/app/employeeguard/skills/skills.tpl.html'
    });
});