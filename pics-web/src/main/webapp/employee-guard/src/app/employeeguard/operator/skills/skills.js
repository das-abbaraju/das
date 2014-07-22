angular.module('PICS.employeeguard.skills', [])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider.when('/employee-guard/skills', {
        templateUrl: '/employee-guard/src/app/employeeguard/operator/skills/skills.tpl.html'
    });
});