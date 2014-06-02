angular.module('PICS.employeeguard')

.config(function ($routeProvider) {
    $routeProvider
        .when('/employee-guard/contractor/dashboard', {
            templateUrl: '/employee-guard/src/app/employeeguard/contractor/dashboard/dashboard.tpl.html'
        })
        .when('/employee-guard/contractor/changelog', {
            templateUrl: '/employee-guard/src/app/employeeguard/contractor/beta-changelog/changelog.tpl.html'
        });
});