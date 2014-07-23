angular.module('PICS.roles', [
    'ngRoute'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/employee-guard/roles', {
            templateUrl: '/employee-guard/src/app/employeeguard/roles/roles.tpl.html'
        })
    });
});
