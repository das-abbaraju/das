angular.module('PICS.employeeguard', [
    'ngRoute',
    'ngResource',
    'PICS.charts',
    'PICS.utility'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/employee-guard/operators/employees/:id', {
            templateUrl: '/angular/src/app/employeeguard/operator/employee/operator_employee.tpl.html'
        })
        .when('/employee-guard/operators/dashboard', {
            templateUrl: '/angular/src/app/employeeguard/operator/dashboard/dashboard.tpl.html'
        })
        .when('/employee-guard/contractor/dashboard', {
            templateUrl: '/angular/src/app/employeeguard/contractor/dashboard/dashboard.tpl.html'
        })
        .when('/employee-guard/employee/dashboard', {
            templateUrl: '/angular/src/app/employeeguard/employee/dashboard/dashboard.tpl.html'
        });
});
