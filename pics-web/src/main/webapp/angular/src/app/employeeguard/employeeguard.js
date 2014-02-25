angular.module('PICS.employeeguard', [
    'ngRoute'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/', {
            templateUrl: '/src/app/employeeguard/operator_employee/operator_employee.tpl.html'
        });
});
