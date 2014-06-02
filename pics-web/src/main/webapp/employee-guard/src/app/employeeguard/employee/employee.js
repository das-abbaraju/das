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
        });
});