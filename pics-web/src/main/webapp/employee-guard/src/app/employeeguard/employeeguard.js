angular.module('PICS.employeeguard', [
    'ngRoute',
    'ngResource',
    'PICS.charts',
    'PICS.skills',
    'PICS.filters'
])

.config(function ($locationProvider, $routeProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/employee-guard/info', {
            templateUrl: '/angular/src/app/employeeguard/serverinfo/serverinfo.tpl.html'
        });
});
