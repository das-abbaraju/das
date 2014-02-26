angular.module('PICS.employeeguard', [
    'ngRoute',
    'ngResource'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/', {
            templateUrl: '/src/app/employeeguard/operator/employee/operator_employee.tpl.html'
        });
});
