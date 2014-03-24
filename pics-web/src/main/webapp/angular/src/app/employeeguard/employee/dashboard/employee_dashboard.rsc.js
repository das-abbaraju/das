angular.module('PICS.employeeguard')

.factory('EmployeeDashboard', function($resource, $routeParams) {
    return $resource('/angular/json/employee/employee-info.json');
});