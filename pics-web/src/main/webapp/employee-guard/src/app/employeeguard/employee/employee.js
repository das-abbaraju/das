angular.module('PICS.employeeguard')

.config(function ($routeProvider) {
    $routeProvider
        .when('/employee-guard/employee/dashboard', {
            templateUrl: '/employee-guard/src/app/employeeguard/employee/dashboard/dashboard.tpl.html'
        })
        .when('/employee-guard/employee/changelog', {
            templateUrl: '/employee-guard/src/app/employeeguard/employee/beta-changelog/changelog.tpl.html'
        })
        .when('/employee-guard/employee/skills', {
            templateUrl: '/employee-guard/src/app/employeeguard/employee/skills/skill_list.tpl.html'
        })
        .when('/employee-guard/employee/skills/sites/:siteSlug', {
            templateUrl: '/employee-guard/src/app/employeeguard/employee/skills/skill_list.tpl.html'
        })
        .when('/employee-guard/employee/skills/sites/:siteSlug/projects/:projectSlug', {
            templateUrl: '/employee-guard/src/app/employeeguard/employee/skills/skill_list.tpl.html'
        })
        .when('/employee-guard/employee/settings', {
            templateUrl: '/employee-guard/src/app/employeeguard/employee/profile/settings.tpl.html'
        })
        .when('/employee-guard/invalid-hash', {
            templateUrl: '/employee-guard/src/app/employeeguard/employee/invalidHash/invalid_hash.tpl.html'
        });
});