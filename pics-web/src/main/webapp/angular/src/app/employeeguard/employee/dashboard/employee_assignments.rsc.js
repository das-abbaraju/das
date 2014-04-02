angular.module('PICS.employeeguard')

.factory('EmployeeAssignment', function($resource, $routeParams) {
    return $resource('/employee-guard/employee/summary/assignments');
});