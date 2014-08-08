angular.module('PICS.groups', [
    'ngRoute'
])

.config(function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/employee-guard/groups', {
            templateUrl: '/employee-guard/src/app/employeeguard/groups/group_list.tpl.html'
        });
});
