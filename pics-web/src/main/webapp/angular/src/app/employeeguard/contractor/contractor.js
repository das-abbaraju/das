angular.module('PICS.employeeguard')

.config(function ($routeProvider) {
    $routeProvider
        .when('/employee-guard/contractor/dashboard', {
            templateUrl: '/angular/src/app/employeeguard/contractor/dashboard/dashboard.tpl.html'
        })
        .when('/employee-guard/contractor/changelog', {
            templateUrl: '/angular/src/app/employeeguard/contractor/beta-changelog/changelog.tpl.html'
        });
});