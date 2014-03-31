angular.module('PICS.employeeguard', [
    'ngRoute',
    'ngResource',
    'PICS.charts'
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
        .when('/employee-guard/operators/assignments', {
            templateUrl: '/angular/src/app/employeeguard/operator/assignmentlist/assignmentlist.tpl.html'
        })
        .when('/employee-guard/operators/projects', {
            templateUrl: '/angular/src/app/employeeguard/operator/project/project_list.tpl.html'
        })
        .when('/employee-guard/contractor/dashboard', {
            templateUrl: '/angular/src/app/employeeguard/contractor/dashboard/dashboard.tpl.html'
        });
});
