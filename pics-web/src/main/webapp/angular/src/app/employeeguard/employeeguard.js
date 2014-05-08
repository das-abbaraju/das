angular.module('PICS.employeeguard', [
    'ngRoute',
    'ngResource',
    'PICS.charts',
    'PICS.skills',
    'PICS.filters'
])

.config(function ($locationProvider) {
    $locationProvider.html5Mode(true);
});
