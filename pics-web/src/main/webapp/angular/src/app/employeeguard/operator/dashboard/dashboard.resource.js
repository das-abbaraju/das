angular.module('PICS.employeeguard')

.factory('SiteDashboard', function($resource, $routeParams) {
    // return $resource('/employee-guard/operators/skills/employees/');
    return $resource('/angular/json/dashboard.json');
});