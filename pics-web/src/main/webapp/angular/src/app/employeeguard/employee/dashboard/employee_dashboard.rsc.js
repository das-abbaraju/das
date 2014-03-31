angular.module('PICS.employeeguard')

.factory('EmployeeDashboard', function($resource, $routeParams) {
    return $resource('/employee-guard/employee/summary/employee-info');
});