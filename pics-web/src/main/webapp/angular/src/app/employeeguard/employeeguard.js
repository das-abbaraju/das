angular.module('PICS.employeeguard', [
    'ngRoute',
    'ngResource'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/employee-guard/operators/employees/:id', {
            templateUrl: '/angular/src/app/employeeguard/operator/employee/operator_employee.tpl.html'
        })
        .when('/employee-guard/operators/dashboard', {
            templateUrl: '/angular/src/app/employeeguard/operator/dashboard/dashboard.tpl.html'
        });
});
