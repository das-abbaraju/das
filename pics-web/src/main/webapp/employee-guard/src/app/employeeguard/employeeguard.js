angular.module('PICS.employeeguard', [
    'pascalprecht.translate',
    'ngRoute',
    'ngResource',
    'PICS.directives',
    'PICS.charts',
    'PICS.groups',
    'PICS.roles',
    'PICS.breadcrumb',
    'PICS.employeeguard.skills',
    'PICS.filters',
    'ui.select2',
    'ProfileService'
])

.config(function ($locationProvider, $routeProvider) {
    $locationProvider.html5Mode(true);

    $routeProvider
        .when('/employee-guard/info', {
            templateUrl: '/employee-guard/src/app/employeeguard/serverinfo/serverinfo.tpl.html'
        })
        .when('/employee-guard/employees/:id/skill/:skillId', {
            templateUrl: '/employee-guard/src/app/employeeguard/common/skill-detail/skill_detail.tpl.html'
        });
});
