angular.module('PICS.employeeguard')

.config(function ($routeProvider) {
    $routeProvider
        .when('/employee-guard/operators/dashboard', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/dashboard/dashboard.tpl.html'
        })
        .when('/employee-guard/operators/changelog', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/beta-changelog/changelog.tpl.html'
        })
        .when('/employee-guard/operators/assignments', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/assignmentlist/assignmentlist.tpl.html'
        })
        .when('/employee-guard/operators/projects', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/project/project_list.tpl.html'
        })
        .when('/employee-guard/operators/roles', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/role/role_list.tpl.html'
        })
        .when('/employee-guard/operators/employees/:id', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/employee/operator_employee.tpl.html'
        })
        .when('/employee-guard/operators/employees/:id/roles/:roleSlug', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/employee/operator_employee.tpl.html'
        })
        .when('/employee-guard/operators/employees/:id/projects/:projectSlug', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/employee/operator_employee.tpl.html'
        })
        .when('/employee-guard/operators/employees/:id/sites/:siteId', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/employee/operator_employee.tpl.html'
        })
        .when('/employee-guard/operators/employees/:id/sites/:siteId/roles/:roleSlug', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/employee/operator_employee.tpl.html'
        })
        .when('/employee-guard/operators/employees/:id/sites/:siteId/projects/:projectSlug', {
            templateUrl: '/employee-guard/src/app/employeeguard/operator/employee/operator_employee.tpl.html'
        });
});