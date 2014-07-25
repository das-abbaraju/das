angular.module('PICS.employees', [
    'ngRoute'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/employee-guard/employees', {
            templateUrl: '/employee-guard/src/app/employeeguard/employees/employee_list.tpl.html'
        });
});
